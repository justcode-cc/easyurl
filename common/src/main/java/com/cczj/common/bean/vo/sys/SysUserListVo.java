package com.cczj.common.bean.vo.sys;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@ApiModel
public class SysUserListVo {

    private Long id;

    private String username;

    private String account;

    private String phone;

    @ApiModelProperty(value = "状态 1正常 0锁定")
    private Integer status;

    private String deptName;

    private String online;

    private String sessionTimeout;

    private List<SysUserJobInfoListVo> jobList;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;


}
