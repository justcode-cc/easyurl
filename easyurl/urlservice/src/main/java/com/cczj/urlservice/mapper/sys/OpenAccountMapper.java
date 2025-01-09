package com.cczj.urlservice.mapper.sys;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cczj.common.bean.params.sys.OpenAccountPageParams;
import com.cczj.common.bean.vo.sys.OpenAccountPageVO;
import com.cczj.common.entity.sys.OpenAccountEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OpenAccountMapper extends BaseMapper<OpenAccountEntity> {

    List<OpenAccountPageVO> getOpenAccountPage(@Param("params") OpenAccountPageParams params);

}
