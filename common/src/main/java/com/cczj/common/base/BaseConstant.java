package com.cczj.common.base;

import com.cczj.common.utils.IpUtils;
import lombok.Getter;

public class BaseConstant {

    public static final String traceId = "traceId";

    public static final String CONTEXT_LOGIN_USERNAME = "login_username";

    public static String PROFILE_AUTO = "";

    public static String localIp = IpUtils.getLocalHostStr();

    /**
     * 执行定时任务的服务器IP，在集群模式下可以通过配置文件进行配置
     */
    public static String taskIp = "";

    /**
     * 应用的名称，会在项目启动后赋值
     */
    public static String appName = "";


    @Getter
    private static final String ossAccessKeyId = "";
    @Getter
    private static final String ossAccessKeySecret = "";
    @Getter
    private static final String ossBucketName = "";
    @Getter
    private static final String bucketInternalUrl = "";
    @Getter
    private static final String bucketPublicUrl = "";


}
