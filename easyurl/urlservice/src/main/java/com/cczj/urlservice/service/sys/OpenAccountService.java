package com.cczj.urlservice.service.sys;

import com.cczj.common.base.IdLongParams;
import com.cczj.common.base.R;
import com.cczj.common.base.RedisConstant;
import com.cczj.common.bean.params.sys.OpenAccountAddParams;
import com.cczj.common.bean.params.sys.OpenAccountPageParams;
import com.cczj.common.bean.vo.sys.OpenAccountPageVO;
import com.cczj.common.entity.sys.OpenAccountEntity;
import com.cczj.common.enums.CommonStatusEnum;
import com.cczj.urlservice.mapper.sys.OpenAccountMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenAccountService {

    private final OpenAccountMapper openAccountMapper;
    private final RedissonClient redissonClient;


    @Transactional(rollbackFor = Exception.class)
    public R<Object> addOpenAccount(OpenAccountAddParams params) {
        OpenAccountEntity openAccount = new OpenAccountEntity();
        BeanUtils.copyProperties(params, openAccount);
        this.openAccountMapper.insert(openAccount);
        this.redissonClient.getBucket(String.format(RedisConstant.openAccountCache(), openAccount.getAppId())).set(openAccount.getAppSecret());
        return R.success();
    }

    @Transactional(rollbackFor = Exception.class)
    public R<Object> updateOpenAccount(OpenAccountAddParams params) {
        OpenAccountEntity openAccount = this.openAccountMapper.selectById(params.getId());
        boolean updateCache = !openAccount.getAppSecret().equals(params.getAppSecret());
        boolean statusChange = !Objects.equals(openAccount.getStatus(), params.getStatus());
        BeanUtils.copyProperties(params, openAccount);
        openAccount.setUpdateTime(LocalDateTime.now());
        this.openAccountMapper.updateById(openAccount);
        if (updateCache) {
            log.info("更新。。。。。。");

            this.redissonClient.getBucket(String.format(RedisConstant.openAccountCache(), openAccount.getAppId())).set(openAccount.getAppSecret());
        }
        if (statusChange && CommonStatusEnum.stop.getValue().equals(params.getStatus())) {
            this.redissonClient.getBucket(String.format(RedisConstant.openAccountCache(), openAccount.getAppId())).delete();
        }
        if (statusChange && CommonStatusEnum.normal.getValue().equals(params.getStatus())) {
            this.redissonClient.getBucket(String.format(RedisConstant.openAccountCache(), openAccount.getAppId())).set(openAccount.getAppSecret());
        }
        return R.success();
    }

    public R<Object> deleteOpenAccount(IdLongParams params) {
        OpenAccountEntity openAccount = this.openAccountMapper.selectById(params.getId());
        openAccount.setDeleted(1);
        this.openAccountMapper.updateById(openAccount);
        this.redissonClient.getBucket(String.format(RedisConstant.openAccountCache(), openAccount.getAppId())).delete();
        return R.success();
    }

    public R<PageInfo<OpenAccountPageVO>> getOpenAccountPage(OpenAccountPageParams params) {
        PageInfo<OpenAccountPageVO> pageInfo = PageHelper.startPage(params.getPageNumber(), params.getPageSize()).doSelectPageInfo(
                () -> this.openAccountMapper.getOpenAccountPage(params)
        );
        return R.success(pageInfo);
    }
}
