package com.cczj.common.bean.params.sys;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class SysDeptParams {

    private Long id;

    @ApiModelProperty(value = "父部门ID")
    @NotNull(message = "缺少父部门id")
    private Long parentId;

//    @ApiModelProperty(value = "祖级列表")
//    @NotBlank(message = "缺少祖级列表")
//    private String ancestors;

    @ApiModelProperty(value = "部门名称")
    @NotBlank(message = "缺少部门名称")
    private String deptName;

    @ApiModelProperty(value = "显示顺序")
    @NotNull(message = "缺少显示顺序")
    private Integer orderNum;

    @ApiModelProperty(value = "部门状态（0正常 1停用）")
    private Integer status;

}
