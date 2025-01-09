package com.cczj.common.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
@ApiModel(value = "LogonUser", description = "登录用户信息")
public class LogonUser {


    private String username;

    private String account;

    private Long userId;

    private Long roleId;

    private Long jobId;

    private String token;

    private List<SysUserJobDTO> jobList;



}
