package com.cczj.urlservice.mapper.sys;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cczj.common.entity.sys.SysDeptEntity;
import org.apache.ibatis.annotations.Param;

public interface SysDeptMapper extends BaseMapper<SysDeptEntity> {

    Integer selectHasChild(@Param("deptId") Long deptId);
}
