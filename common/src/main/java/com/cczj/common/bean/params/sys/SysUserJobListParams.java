package com.cczj.common.bean.params.sys;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel
public class SysUserJobListParams {

    private Long id;

    @NotNull(message = "角色id不能为空")
    private Long roleId;

    @NotNull(message = "部门id不能为空")
    private Long deptId;

//    @NotNull(message = "是否部门管理员不能为空")
//    private Integer deptAdmin;

}
