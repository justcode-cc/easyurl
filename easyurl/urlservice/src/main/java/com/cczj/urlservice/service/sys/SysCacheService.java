package com.cczj.urlservice.service.sys;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cczj.common.base.IdLongParams;
import com.cczj.common.base.R;
import com.cczj.common.base.RedisConstant;
import com.cczj.common.bean.params.sys.SysCacheListParams;
import com.cczj.common.bean.params.sys.SysCacheParams;
import com.cczj.common.entity.sys.SysCacheEntity;
import com.cczj.urlservice.mapper.sys.SysCacheMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class SysCacheService {

    private final RedissonClient redissonClient;
    private final SysCacheMapper sysCacheMapper;

    @Transactional(rollbackFor = Exception.class)
    public R<Object> addCache(SysCacheParams params) {
        SysCacheEntity exist = this.sysCacheMapper.selectOne(Wrappers.lambdaQuery(SysCacheEntity.class)
                .eq(SysCacheEntity::getCacheKey, params.getCacheKey())
                .eq(SysCacheEntity::getDeleted, 0));
        if (exist != null) {
            return R.fail("缓存key已存在");
        }
        SysCacheEntity sysCacheEntity = new SysCacheEntity();
        BeanUtils.copyProperties(params, sysCacheEntity);
        sysCacheEntity.setId(null);
        this.sysCacheMapper.insert(sysCacheEntity);
        this.updateCache(params);
        return R.success();
    }


    private void updateCache(SysCacheParams params) {
        RBucket<Object> bucket = this.redissonClient.getBucket(RedisConstant.system_cache() + params.getCacheKey());
        if (params.getCacheTtl() != null && params.getCacheTtl() != -1) {
            bucket.set(params.getCacheValue(), params.getCacheTtl(), TimeUnit.SECONDS);
        } else {
            bucket.set(params.getCacheValue());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public R<Object> editCache(SysCacheParams params) {
        SysCacheEntity exist = this.sysCacheMapper.selectOne(Wrappers.lambdaQuery(SysCacheEntity.class)
                .ne(SysCacheEntity::getId, params.getId())
                .eq(SysCacheEntity::getCacheKey, params.getCacheKey())
                .eq(SysCacheEntity::getDeleted, 0));
        if (exist != null) {
            return R.fail("缓存key重复设置");
        }
        SysCacheEntity sysCacheEntity = this.sysCacheMapper.selectById(params.getId());
        BeanUtils.copyProperties(params, sysCacheEntity);
        sysCacheEntity.setUpdateTime(LocalDateTime.now());
        this.sysCacheMapper.updateById(sysCacheEntity);
        this.updateCache(params);
        return R.success();
    }

    public R<Object> deleteCache(IdLongParams params) {
        SysCacheEntity sysCacheEntity = this.sysCacheMapper.selectById(params.getId());
        sysCacheEntity.setDeleted(1);
        sysCacheEntity.setUpdateTime(LocalDateTime.now());
        this.sysCacheMapper.updateById(sysCacheEntity);
        this.redissonClient.getBucket(RedisConstant.system_cache() + sysCacheEntity.getCacheKey()).delete();
        return R.success();
    }

    public R<PageInfo<SysCacheEntity>> pageList(SysCacheListParams params) {
        PageInfo<SysCacheEntity> pageInfo = PageHelper.startPage(params.getPageNumber(), params.getPageSize()).doSelectPageInfo(
                () -> this.sysCacheMapper.selectList(Wrappers.lambdaQuery(SysCacheEntity.class)
                        .eq(SysCacheEntity::getDeleted, 0)
                        .like(StrUtil.isNotBlank(params.getCacheKey()), SysCacheEntity::getCacheKey, params.getCacheKey())
                        .orderByDesc(SysCacheEntity::getId)
                ));
        return R.success(pageInfo);
    }

    public R<Object> flushCache(IdLongParams params) {
        SysCacheEntity sysCacheEntity = this.sysCacheMapper.selectById(params.getId());
        SysCacheParams sysCacheParams = new SysCacheParams();
        BeanUtils.copyProperties(sysCacheEntity, sysCacheParams);
        this.updateCache(sysCacheParams);
        return R.success();
    }

    public R<SysCacheEntity> info(IdLongParams params) {
        SysCacheEntity sysCacheEntity = this.sysCacheMapper.selectById(params.getId());
        return R.success(sysCacheEntity);
    }
}
