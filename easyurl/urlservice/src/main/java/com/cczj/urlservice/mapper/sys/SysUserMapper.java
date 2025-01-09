package com.cczj.urlservice.mapper.sys;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cczj.common.bean.params.sys.SysUserListParams;
import com.cczj.common.bean.vo.sys.SysUserListVo;
import com.cczj.common.entity.sys.SysUserEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysUserMapper extends BaseMapper<SysUserEntity> {

    List<SysUserListVo> selectUserList(@Param("params") SysUserListParams params);

}
