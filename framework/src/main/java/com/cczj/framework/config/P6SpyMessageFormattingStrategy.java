package com.cczj.framework.config;

import cn.hutool.core.util.StrUtil;
import com.cczj.common.base.BaseConstant;
import com.cczj.common.base.ContextHolder;
import com.p6spy.engine.spy.appender.MessageFormattingStrategy;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class P6SpyMessageFormattingStrategy implements MessageFormattingStrategy {

    @Override
    public String formatMessage(int connectionId, String now, long elapsed, String category, String prepared, String sql, String url) {
        if (elapsed > 1000) {
            log.warn("slow sql,消耗时间:{}ms,执行sql:{}", elapsed, sql.replaceAll("[\\s]+", " "));
        }
        if (StrUtil.isBlank(sql)) {
            return StrUtil.format("请求id:{},Sql:null,category:{},Time:{}ms",
                    ContextHolder.getOrDefault(BaseConstant.traceId, ""), category, elapsed
            );
        }
        return StrUtil.format("Time:{}ms,{},Sql:{}",
                elapsed,
                category,
                sql.replaceAll("[\\s]+", " "));

    }


}
