package com.cczj.common.bean.params.sys;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@ApiModel
public class SysUserParams {

    private Long id;

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "账号不能为空")
    private String account;

    private String phone;

    private String password;

    @Valid
    private List<SysUserJobListParams> jobList;

}
