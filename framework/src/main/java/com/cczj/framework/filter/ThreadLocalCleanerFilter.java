package com.cczj.framework.filter;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.cczj.common.base.BaseConstant;
import com.cczj.common.base.ContextHolder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
@AllArgsConstructor
@Order(Ordered.LOWEST_PRECEDENCE - 1001)
@Slf4j
public class ThreadLocalCleanerFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void destroy() {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        try {
            ContextHolder.init();
            HttpServletRequest request = (HttpServletRequest) servletRequest;
            String requestId = request.getHeader(BaseConstant.traceId);
            if (StrUtil.isBlank(requestId)) {
                requestId = RandomUtil.randomNumbers(10);
                MDC.put(BaseConstant.traceId, requestId);
            } else {
                MDC.put(BaseConstant.traceId, requestId);
            }
            ContextHolder.set(BaseConstant.traceId, requestId);
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            ContextHolder.clear();
            MDC.clear();
        }
    }
}
