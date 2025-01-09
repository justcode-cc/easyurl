package com.cczj.common.base;


import com.cczj.common.utils.JsonUtil;

import java.util.HashMap;
import java.util.Map;

public class ContextHolder {

    private static final ThreadLocal<Map<String, Object>> context = new ThreadLocal<>();

    public static void clear() {
        Map<String, Object> tmp = ContextHolder.context.get();
        if (tmp != null) {
            ContextHolder.context.remove();
            tmp.clear();
        }
    }

    public static void init() {
        ContextHolder.clear();
        ContextHolder.context.set(new HashMap<>());
    }

    public static void set(String key, Object value) {
        if (ContextHolder.context.get() == null) {
            ContextHolder.init();
        }
        ContextHolder.context.get().put(key, value);
    }

    public static Object get(String key) {
        if (null == ContextHolder.context.get()) {
            ContextHolder.init();
        }
        return ContextHolder.context.get().get(key);
    }

    public static Object getOrDefault(String key, Object defaultValue) {
        Object rtn = ContextHolder.get(key);
        return rtn == null ? defaultValue : rtn;
    }

    public static String getTraceId() {
        return ContextHolder.get(BaseConstant.traceId) == null ? "" :
                ContextHolder.get(BaseConstant.traceId).toString();
    }

    public static <T> T getCurrentUser(Class<T> clazz) {
        return JsonUtil.strToObj(ContextHolder.get(BaseConstant.CONTEXT_LOGIN_USERNAME).toString(), clazz);
    }

    public static void setCurrentUser(String logon) {
        set(BaseConstant.CONTEXT_LOGIN_USERNAME, logon);
    }

}
