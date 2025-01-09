package com.cczj.common.bean.params.sys;

import com.cczj.common.base.PageParams;
import lombok.Data;

@Data
public class SysRoleQueryParams extends PageParams {

    private String roleName;

}
