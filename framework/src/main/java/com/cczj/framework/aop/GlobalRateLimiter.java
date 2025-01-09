package com.cczj.framework.aop;

import org.redisson.api.RateIntervalUnit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface GlobalRateLimiter {

    /**
     * 自定义的key 注意使用对象，比如对客户使用，就要在key中定义客户id
     */
    String key();

    String errMsg() default "您的请求频率过高，此操作已被限制";

    /**
     * 频率  如果是1，则代表每秒(或指定的其他单位)只能访问一次
     */
    long rate() default 1L;

    /**
     * 间隔，默认秒
     */
    long rateInterval() default 1L;

    /**
     * 间隔单位
     */
    RateIntervalUnit rateIntervalUnit() default RateIntervalUnit.SECONDS;


}
