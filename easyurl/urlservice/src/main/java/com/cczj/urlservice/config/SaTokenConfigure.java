package com.cczj.urlservice.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.strategy.SaStrategy;
import cn.hutool.core.util.RandomUtil;
import com.cczj.common.base.BaseConstant;
import com.cczj.common.base.ContextHolder;
import com.cczj.framework.config.IgnoreUrls;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE + 1000)
public class SaTokenConfigure implements WebMvcConfigurer {

    @Resource
    private IgnoreUrls ignoreUrls;


    // 注册 Sa-Token 拦截器，打开注解式鉴权功能
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册 Sa-Token 拦截器，打开注解式鉴权功能
        registry.addInterceptor(new SaInterceptor(
                        handle -> {
                            StpUtil.checkLogin();
                            SaSession session = StpUtil.getSession();
                            ContextHolder.setCurrentUser(session.get(BaseConstant.CONTEXT_LOGIN_USERNAME).toString());
                        }
                ))
                .addPathPatterns("/**")
                .excludePathPatterns(excludePathList());
    }

    private List<String> excludePathList() {
        return new ArrayList<>(ignoreUrls.getUrl());
    }

    @PostConstruct
    public void init() {
        SaStrategy.instance.setCreateToken((x, y) -> "t_" + RandomUtil.randomString(20));
    }
}
