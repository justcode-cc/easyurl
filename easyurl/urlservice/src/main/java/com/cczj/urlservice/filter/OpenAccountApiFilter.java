package com.cczj.urlservice.filter;

import cn.hutool.core.util.StrUtil;
import com.cczj.common.base.RedisConstant;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.annotation.Resource;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

@Slf4j
@Component
public class OpenAccountApiFilter implements Filter {

    @Resource
    private RedissonClient redissonClient;


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        if (antPathMatcher.match("/openApi/**", req.getServletPath())) {
            String appId = req.getHeader("appId");
            String appSecret = req.getHeader("appSecret");
            if (StrUtil.isBlank(appId) || StrUtil.isBlank(appSecret)) {
                log.info("缺少appId或appSecret,appId:{},appSecret:{}", appId, appSecret);
                res.setContentType("text/html;charset=UTF-8");
                res.getWriter().write("no permit");
                res.setStatus(401);
                return;
            }
            String redisKey = String.format(RedisConstant.openAccountCache(), appId);
            RBucket<Object> bucket = this.redissonClient.getBucket(redisKey);
            if (!bucket.isExists() || bucket.get() == null || !Objects.equals(bucket.get().toString(), appSecret)) {
                log.info("appSecret无效,appId:{},appSecret:{}", appId, appSecret);
                res.setContentType("text/html;charset=UTF-8");
                res.getWriter().write("no permit");
                res.setStatus(401);
                return;
            }
            chain.doFilter(request, response);
        } else {
            chain.doFilter(request, response);
        }

    }
}
