package com.cczj.common.bean.params;

import cn.hutool.crypto.digest.DigestUtil;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class LogonParams {

    @NotBlank(message = "缺少账户参数")
    private String account;

    @NotBlank(message = "缺少密码参数")
    private String password;

    @NotBlank(message = "缺少key参数")
    private String key;


    public static void main(String[] args) {
        //test case
        String res = DigestUtil.md5Hex("qwerasdf12349876" + "thnxhi");
        System.out.println(res);
    }
}
