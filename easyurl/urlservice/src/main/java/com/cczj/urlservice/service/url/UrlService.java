package com.cczj.urlservice.service.url;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cczj.common.base.ContextHolder;
import com.cczj.common.base.IdLongParams;
import com.cczj.common.base.R;
import com.cczj.common.base.RedisConstant;
import com.cczj.common.dto.KvResult;
import com.cczj.common.entity.url.UrlMappingEntity;
import com.cczj.common.entity.url.UrlVisitEntity;
import com.cczj.common.enums.url.UrlStatusEnum;
import com.cczj.common.utils.JsonUtil;
import com.cczj.common.utils.SequenceUtils;
import com.cczj.framework.utils.IpUtils;
import com.cczj.framework.utils.LogonUtils;
import com.cczj.urlbean.params.UrlGenerateParams;
import com.cczj.urlbean.params.UrlMappingListParams;
import com.cczj.urlbean.params.UrlVisitListParams;
import com.cczj.urlbean.vo.*;
import com.cczj.urlservice.config.UrlConfig;
import com.cczj.urlservice.mapper.url.UrlMappingMapper;
import com.cczj.urlservice.mapper.url.UrlVisitMapper;
import com.cczj.urlservice.utils.LocalDateUtils;
import com.cczj.urlservice.utils.RandomStrUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {

    private final UrlConfig urlConfig;

    @Resource(name = "bloomFilter")
    private RBloomFilter<String> bloomFilter;
    private final UrlMappingMapper urlMappingMapper;
    private final UrlVisitMapper urlVisitMapper;
    private final RedissonClient redissonClient;
    private final UrlVisitService urlVisitService;

    public R<String> generate(UrlGenerateParams params) {
        if (params.getExpireTime() == null && params.getExpireDateTime() == null) {
            return R.fail("未指定过期时间");
        }
        R<String> checkMaxTry = this.checkMaxTry();
        if (!checkMaxTry.isSuccess()) {
            return checkMaxTry;
        }
        String shortUrl = RandomStrUtils.generateRandomString();
        boolean add = this.bloomFilter.add(shortUrl);
        if (add) {
            UrlMappingEntity urlMappingEntity = this.getUrlMappingEntity(params, shortUrl);
            this.urlMappingMapper.insert(urlMappingEntity);
        } else {
            //hash冲突的情况下，检查一下数据库是否存在
            UrlMappingEntity urlMappingEntity = this.urlMappingMapper.selectOne(Wrappers.lambdaQuery(UrlMappingEntity.class)
                    .eq(UrlMappingEntity::getShortUrl, shortUrl)
                    .last("limit 1"));
            if (urlMappingEntity == null) {
                urlMappingEntity = this.getUrlMappingEntity(params, shortUrl);
                this.urlMappingMapper.insert(urlMappingEntity);
            } else {
                this.generate(params);
            }
        }
        return R.success(this.urlConfig.getBaseUrl() + shortUrl);
    }

    private UrlMappingEntity getUrlMappingEntity(UrlGenerateParams params, String shortUrl) {
        UrlMappingEntity urlMappingEntity = new UrlMappingEntity();
        urlMappingEntity.setOriginUrl(params.getOriginUrl());
        urlMappingEntity.setShortUrl(shortUrl);
        if (params.getExpireTime() != null) {
            if (params.getExpireTime() == -1) {
                urlMappingEntity.setExpireTime(LocalDateTime.of(2099, 12, 31, 23, 59, 59));
            } else {
                urlMappingEntity.setExpireTime(LocalDateTime.now().plusSeconds(params.getExpireTime()));
            }
        } else {
            urlMappingEntity.setExpireTime(params.getExpireDateTime());
        }
        urlMappingEntity.setCreateUserId(LogonUtils.getCurrentUserQuiet() == null ? -1 : LogonUtils.getCurrentUserQuiet().getJobId());
        return urlMappingEntity;
    }


    private R<String> checkMaxTry() {
        Object maxTry = ContextHolder.getOrDefault(this.urlConfig.getMaxTryKey(), 0);
        int maxTryNum = Integer.parseInt(maxTry.toString());
        if (maxTryNum >= this.urlConfig.getMaxTry()) {
            log.error("生成随机数重复次数达到上限");
            return R.fail("生成失败，请重试");
        }
        maxTryNum += 1;
        ContextHolder.set(this.urlConfig.getMaxTryKey(), maxTryNum);
        if (maxTryNum > 1) {
            log.info("生成随机数已重复{}次,尝试继续生成", maxTryNum);
        }
        return R.success();
    }

    public R<PageInfo<UrlMappingListVO>> list(UrlMappingListParams params) {
        if (StrUtil.isNotBlank(params.getBeginDate())) {
            params.setBeginDate(params.getBeginDate() + " 00:00:00");
        }
        if (StrUtil.isNotBlank(params.getEndDate())) {
            params.setEndDate(params.getEndDate() + " 23:59:59");
        }
        PageInfo<UrlMappingListVO> pageInfo = PageHelper.startPage(params.getPageNumber(), params.getPageSize())
                .doSelectPageInfo(
                        () -> this.urlMappingMapper.selectForPage(params)
                );
        if (pageInfo.hasContent()) {
            pageInfo.getList().forEach(v -> {
                v.setShortUrl(this.urlConfig.getBaseUrl() + v.getShortUrl());
            });

        }
        return R.success(pageInfo);
    }

    public String getOriginUrl(String shortUrl, HttpServletRequest request) {
        UrlMappingEntity urlMappingEntity = this.urlMappingMapper.selectOne(Wrappers.lambdaQuery(UrlMappingEntity.class)
                .eq(UrlMappingEntity::getShortUrl, shortUrl)
                .last("limit 1"));
        if (urlMappingEntity == null) {
            return null;
        }
        if (!urlMappingEntity.getExpireTime().isAfter(LocalDateTime.now())) {
            this.saveVisit(urlMappingEntity, request, false);
            return null;
        }
        this.saveVisit(urlMappingEntity, request, true);
        log.info("origin:{}", urlMappingEntity.getOriginUrl());
        return urlMappingEntity.getOriginUrl();
    }

    private void saveVisit(UrlMappingEntity urlMapping, HttpServletRequest request, boolean isValid) {
        UrlVisitEntity visit = new UrlVisitEntity();
        visit.setId(SequenceUtils.getNextId());
        visit.setUrlId(urlMapping.getId());
        visit.setIp(IpUtils.getIpAddress(request));
        String userAgent = request.getHeader("User-Agent");
        if (StrUtil.isNotBlank(userAgent)) {
            UserAgent ua = UserAgentUtil.parse(userAgent);
            visit.setBrowser(ua.getBrowser().toString());
            visit.setBrowserVersion(ua.getVersion());
            visit.setEngine(ua.getEngine().toString());
            visit.setEngineVersion(ua.getEngineVersion());
            visit.setOs(ua.getOs().toString());
            visit.setPlatform(ua.getPlatform().toString());
            visit.setMobile(ua.isMobile() ? 1 : 0);
        }
        visit.setValid(isValid ? 1 : 0);
        visit.setReferrer(request.getHeader("Referer"));
        this.redissonClient.getBlockingQueue(RedisConstant.visit_url_queue).add(JsonUtil.toJsonString(visit));
    }

    public void receiveMessage() {
        try {
            RBlockingQueue<String> visitQueue = this.redissonClient.getBlockingQueue(RedisConstant.visit_url_queue);
            List<String> poll = visitQueue.poll(10);
            if (CollUtil.isNotEmpty(poll)) {
                this.urlVisitService.saveBatch(poll.stream().map(v -> JsonUtil.strToObj(v, UrlVisitEntity.class)).collect(Collectors.toList()));
                log.info("保存访问记录成功");
            }
        } catch (Exception e) {
            log.error("保存访问记录失败", e);
        }
    }

    public R<PageInfo<UrlVisitListVO>> visitList(UrlVisitListParams params) {
        if (StrUtil.isNotBlank(params.getBeginDate())) {
            params.setBeginDate(params.getBeginDate() + " 00:00:00");
        }
        if (StrUtil.isNotBlank(params.getEndDate())) {
            params.setEndDate(params.getEndDate() + " 23:59:59");
        }
        if (StrUtil.isNotBlank(params.getShortUrl())) {
            params.setShortUrl(params.getShortUrl().replaceAll(this.urlConfig.getBaseUrl(), ""));
        }
        PageInfo<UrlVisitListVO> pageInfo = PageHelper.startPage(params.getPageNumber(), params.getPageSize()).doSelectPageInfo(
                () -> this.urlVisitMapper.selectVisitList(params)
        );
        if (pageInfo.hasContent()) {
            pageInfo.getList().forEach(v -> {
                v.setShortUrl(this.urlConfig.getBaseUrl() + v.getShortUrl());
            });
        }
        return R.success(pageInfo);
    }

    public R<UrlStatisticsVo> statistics() {
        UrlStatisticsVo vo = new UrlStatisticsVo();
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDate weekMonday = LocalDateUtils.getWeekMonday(LocalDate.now());
        LocalDate weekSunday = LocalDateUtils.getWeekSunday(LocalDate.now());

        int total = this.urlMappingMapper.selectTotal(now);
        vo.setTotal(total);
        int beforeTotal = this.urlMappingMapper.selectTotal(weekMonday.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + " 00:00:00");
        vo.setPreWeekAdd(beforeTotal);
        vo.setPreWeekRatio(beforeTotal == 0 ? BigDecimal.ZERO :
                BigDecimal.valueOf(total - beforeTotal).divide(BigDecimal.valueOf(beforeTotal), 2, RoundingMode.HALF_UP)
        );

        int thisWeekAdd = this.urlMappingMapper.selectAdd(weekMonday.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + " 00:00:00",
                weekSunday.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + " 23:59:59");
        vo.setThisWeekAdd(thisWeekAdd);

        int preWeekAdd = this.urlMappingMapper.selectAdd(weekMonday.minusDays(7).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + " 00:00:00",
                weekMonday.minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + " 23:59:59");
        vo.setPreWeekAdd(preWeekAdd);

        vo.setPreWeekAddRatio(preWeekAdd == 0 ? BigDecimal.ZERO :
                BigDecimal.valueOf(thisWeekAdd - preWeekAdd).divide(BigDecimal.valueOf(preWeekAdd), 2, RoundingMode.HALF_UP)
        );


        int valid = this.urlMappingMapper.selectValid(now);
        vo.setValid(valid);

        int preWeekValid = this.urlMappingMapper.selectValid(weekMonday.format(DateTimeFormatter.ofPattern("yyyy-MM-dd 00:00:00")));
        vo.setPreWeekValid(preWeekValid);

        vo.setPreWeekValidRatio(preWeekValid == 0 ? BigDecimal.ZERO :
                BigDecimal.valueOf(valid - preWeekValid).divide(BigDecimal.valueOf(preWeekValid), 2, RoundingMode.HALF_UP)
        );

        vo.setExpire(total - valid);
        vo.setPreWeekExpire(vo.getPreWeekAdd() - vo.getPreWeekValid());

        vo.setPreWeekExpireRatio(vo.getPreWeekAdd() == 0 ? BigDecimal.ZERO :
                BigDecimal.valueOf(vo.getPreWeekExpire()).divide(BigDecimal.valueOf(vo.getPreWeekAdd()), 2, RoundingMode.HALF_UP)
        );
        return R.success(vo);
    }

    public R<List<UrlVisitLeadVo>> visitLead() {
        String beginTime = LocalDateTime.now().minusDays(7).format(DateTimeFormatter.ofPattern("yyyy-MM-dd 00:00:00"));
        List<UrlVisitLeadVo> list = this.urlVisitMapper.selectVisitLead(beginTime);
        list.forEach(v -> {
            v.setShortUrl(this.urlConfig.getBaseUrl() + v.getShortUrl());
        });
        return R.success(list);
    }

    @Transactional(rollbackFor = Exception.class)
    public R<Object> offline(IdLongParams params) {
        UrlMappingEntity urlMappingEntity = this.urlMappingMapper.selectById(params.getId());
        if (urlMappingEntity == null) {
            return R.fail("无效的短链接");
        }
        if (Objects.equals(urlMappingEntity.getStatus(), UrlStatusEnum.offline.getValue())) {
            return R.fail("短链接已下线");
        }

        urlMappingEntity.setStatus(UrlStatusEnum.offline.getValue());
        urlMappingEntity.setUpdateTime(LocalDateTime.now());
        this.urlMappingMapper.updateById(urlMappingEntity);
        return R.success();
    }

    public void timeout(String expireTime) {
        List<Long> timeoutIdList = this.urlMappingMapper.selectTimeout(expireTime);
        if (CollUtil.isNotEmpty(timeoutIdList)) {
            this.urlMappingMapper.update(new UrlMappingEntity(), Wrappers.lambdaUpdate(UrlMappingEntity.class)
                    .set(UrlMappingEntity::getStatus, UrlStatusEnum.timeout.getValue())
                    .set(UrlMappingEntity::getUpdateTime, LocalDateTime.now())
                    .in(UrlMappingEntity::getId, timeoutIdList));
        }
    }

    public R<List<KvResult>> getWeekAddUrl() {
        String beginTime = LocalDateTime.now().minusDays(6).format(DateTimeFormatter.ofPattern("yyyy-MM-dd 00:00:00"));
        List<KvResult> list = this.urlMappingMapper.getWeekAddUrl(beginTime);
        return R.success(list);
    }

    public R<UrlPieVO> getUrlStatusPie() {
        String beginTime = LocalDateTime.now().minusDays(6).format(DateTimeFormatter.ofPattern("yyyy-MM-dd 00:00:00"));
        int total = this.urlMappingMapper.selectTotalV2(beginTime);
        int expire = this.urlMappingMapper.selectExpire(beginTime, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        UrlPieVO vo = new UrlPieVO();
        vo.setInvalid(expire);
        vo.setValid(total - expire);
        return R.success(vo);
    }
}
