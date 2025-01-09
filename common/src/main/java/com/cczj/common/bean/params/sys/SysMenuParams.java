package com.cczj.common.bean.params.sys;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@ApiModel
public class SysMenuParams {

    private Long id;

    @ApiModelProperty(value = "菜单名称")
    @NotBlank(message = "缺少菜单名称")
    private String menuName;

    @ApiModelProperty(value = "父菜单ID")
    @NotNull(message = "缺少父菜单id")
    private Long parentId;

    @ApiModelProperty(value = "显示顺序")
    @NotNull(message = "缺少显示顺序")
    private Integer orderNum;

    @ApiModelProperty(value = "路由地址")
    @NotBlank(message = "缺少路由地址")
    private String path;

    @ApiModelProperty(value = "组件路径")
//    @NotBlank(message = "缺少组件路径")
    private String component;

    @ApiModelProperty(value = "路由参数")
    private String query;

    @ApiModelProperty(value = "是否为外链（1是 0否）")
    @NotNull(message = "缺少是否为外链")
    private Integer isFrame;

    @ApiModelProperty(value = "是否缓存（1缓存 0不缓存）")
    @NotNull(message = "缺少是否缓存")
    private Integer isCache;

    @ApiModelProperty(value = "类型（M目录 C菜单 F按钮）")
    @NotBlank(message = "缺少类型")
    private String menuType;

    @ApiModelProperty(value = "显示状态（1显示 0隐藏）")
    @NotNull(message = "缺少显示状态")
    private Integer visible;

    @ApiModelProperty(value = "菜单状态（1正常 0停用）")
    @NotNull(message = "缺少菜单状态")
    private Integer status;

    @ApiModelProperty(value = "菜单图标")
    @NotBlank(message = "缺少菜单图标")
    private String icon;
}
