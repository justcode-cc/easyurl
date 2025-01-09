package com.cczj.framework.utils;

import com.cczj.common.base.BaseConstant;
import com.cczj.common.base.BizException;
import com.cczj.common.base.ContextHolder;
import com.cczj.common.dto.LogonUser;
import com.cczj.common.utils.JsonUtil;

public class LogonUtils {

    public static LogonUser getCurrentUser() {
        Object currentObj = ContextHolder.get(BaseConstant.CONTEXT_LOGIN_USERNAME);
        if (currentObj != null) {
            return JsonUtil.strToObj(currentObj.toString(), LogonUser.class);
        }
        throw new BizException("无效的登录用户");
    }

    public static LogonUser getCurrentUserQuiet() {
        Object currentObj = ContextHolder.get(BaseConstant.CONTEXT_LOGIN_USERNAME);
        if (currentObj != null) {
            return JsonUtil.strToObj(currentObj.toString(), LogonUser.class);
        }
        return null;
    }

    public static boolean isAdmin(LogonUser logonUser) {
        return logonUser.getJobId() == 1;
    }

}
