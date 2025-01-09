package com.cczj.common.utils;


import com.cczj.common.base.BaseConstant;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProfileUtils {

    public static Boolean isProd() {
        return "prod".equals(BaseConstant.PROFILE_AUTO);
    }

    public static Boolean isTaskEcs() {
        return BaseConstant.taskIp.contains(BaseConstant.localIp);
    }


}
