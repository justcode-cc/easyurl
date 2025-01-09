package com.cczj.common.bean.params.sys;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class SysRoleParams {

    private Long id;

    @NotBlank(message = "角色名称不能为空")
    private String roleName;

    @NotNull(message = "父级id不能为空")
    private Long parentId;

    @NotNull(message = "菜单id不能为空")
    private List<Long> menuIdList;

}
