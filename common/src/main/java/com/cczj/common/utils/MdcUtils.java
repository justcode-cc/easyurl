package com.cczj.common.utils;

import cn.hutool.core.util.RandomUtil;
import com.cczj.common.base.BaseConstant;
import org.slf4j.MDC;

public class MdcUtils {

    public static void setRequestId(String... assignRequestId) {
        String requestId;
        if (assignRequestId != null && assignRequestId.length > 0) {
            requestId = String.join("-", assignRequestId);
        } else {
            requestId = RandomUtil.randomNumbers(10);
        }
        MDC.put(BaseConstant.traceId, requestId);
    }

    public static String getRequestId(){
        return MDC.get(BaseConstant.traceId);
    }

    public static void clearRequestId() {
        MDC.clear();
    }
}
