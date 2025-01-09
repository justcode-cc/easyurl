package com.cczj.urlservice.mapper.url;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cczj.common.entity.url.UrlVisitEntity;
import com.cczj.urlbean.params.UrlVisitListParams;
import com.cczj.urlbean.vo.UrlVisitLeadVo;
import com.cczj.urlbean.vo.UrlVisitListVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UrlVisitMapper extends BaseMapper<UrlVisitEntity> {

    List<UrlVisitListVO> selectVisitList(@Param("params") UrlVisitListParams params);

    List<UrlVisitLeadVo> selectVisitLead(@Param("beginTime") String beginTime);

}
