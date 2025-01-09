package com.cczj.common.utils;

import cn.hutool.crypto.symmetric.AES;

//本类位于common包，common中并没有引入bcprov-jdk15to18，所以直接在common中执行main函数测试会报错
//如果要测试，将方法复制到已经引入bcprov-jdk15to18的模块中即可
public class CryptoUtils {


    /**
     * aes解密
     *
     * @param content 解密的内容
     * @param key     key
     * @param iv      iv
     * @return 解密后的内容
     */
    public static String aesDecrypt(String content, String key, String iv) {
        AES aes = new AES("CBC", "PKCS7Padding",
                // 密钥，可以自定义
                key.getBytes(),
                // iv加盐，按照实际需求添加
                iv.getBytes());
        return aes.decryptStr(content);
    }
}
