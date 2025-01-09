package com.cczj.common.bean.vo.sys;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class SysUserInfoVo {

    private Long id;

    private String username;

    private String account;

    private String phone;

    @ApiModelProperty(value = "状态 1正常 0锁定")
    private Integer status;


}
