package com.cczj.urlservice.mapper.url;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cczj.common.dto.KvResult;
import com.cczj.common.entity.url.UrlMappingEntity;
import com.cczj.urlbean.params.UrlMappingListParams;
import com.cczj.urlbean.vo.UrlMappingListVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UrlMappingMapper extends BaseMapper<UrlMappingEntity> {

    List<UrlMappingListVO> selectForPage(@Param("params") UrlMappingListParams params);

    int selectTotal(@Param("time") String time);

    int selectAdd(@Param("beginTime") String beginTime, @Param("endTime") String endTime);

    int selectValid(@Param("time") String time);

    List<Long> selectTimeout(@Param("expireTime") String expireTime);

    List<KvResult> getWeekAddUrl(@Param("beginTime") String beginTime);

    int selectTotalV2(@Param("beginTime") String beginTime);

    int selectExpire(@Param("beginTime") String beginTime, @Param("expireTime") String expireTime);
}
