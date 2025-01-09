package com.cczj.common.base;

public class RedisConstant {

    public static String logon_key_iv(){
        return BaseConstant.appName + ":logon_key_iv:";
    }

    public static String logon_max_limit(){
        return BaseConstant.appName + ":logon_max_limit:";
    }

    public static String system_cache(){
        return BaseConstant.appName + ":system_cache:";
    }

    public static String openAccountCache(){
        return BaseConstant.appName + ":openAccountCache:%s";
    }


    public static final String visit_url_queue = "visit_url_queue";

}
