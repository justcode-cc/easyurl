package com.cczj.common.bean.params.sys;

import com.cczj.common.base.PageParams;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SysUserListParams extends PageParams {

    private String username;

    private Integer status;

    private String phone;

    private Long deptId;

    private String beginTime;

    private String endTime;

}
