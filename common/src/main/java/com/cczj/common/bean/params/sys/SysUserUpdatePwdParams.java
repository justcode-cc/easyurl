package com.cczj.common.bean.params.sys;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class SysUserUpdatePwdParams {

    @NotBlank(message = "旧密码不能为空")
    private String oldPwd;

    @NotBlank(message = "新密码不能为空")
    private String newPwd;


}
