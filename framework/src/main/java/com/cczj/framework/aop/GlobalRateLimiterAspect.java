package com.cczj.framework.aop;

import cn.hutool.core.util.RandomUtil;
import com.cczj.common.base.BizException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.time.Duration;

@Aspect
@Component
@Slf4j
public class GlobalRateLimiterAspect {

    @Resource
    private RedissonClient redissonClient;
    @Value("${spring.application.name}")
    private String applicationName;
    private final DefaultParameterNameDiscoverer discoverer = new DefaultParameterNameDiscoverer();

    @Pointcut(value = "@annotation(com.cczj.framework.aop.GlobalRateLimiter)")
    public void cut() {

    }

    @Around(value = "cut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        Class<?> targetClass = joinPoint.getTarget().getClass();
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Class<?>[] parameterTypes = methodSignature.getParameterTypes();
        Method method = targetClass.getDeclaredMethod(methodName, parameterTypes);
        GlobalRateLimiter globalRateLimiter = method.getDeclaredAnnotation(GlobalRateLimiter.class);
        Object[] params = joinPoint.getArgs();
        long rate = globalRateLimiter.rate();
        String key = globalRateLimiter.key();
        long rateInterval = globalRateLimiter.rateInterval();
        RateIntervalUnit rateIntervalUnit = globalRateLimiter.rateIntervalUnit();
        if (key.contains("#")) {
            ExpressionParser parser = new SpelExpressionParser();
            StandardEvaluationContext ctx = new StandardEvaluationContext();
            String[] parameterNames = discoverer.getParameterNames(method);
            if (parameterNames != null) {
                for (int i = 0; i < parameterNames.length; i++) {
                    ctx.setVariable(parameterNames[i], params[i]);
                }
            }
            // 执行表达式
            Expression expression = parser.parseExpression(key);
            Object value = expression.getValue(ctx);
            if (value == null) {
                throw new BizException("无效的缓存key");
            }
            key = value.toString();
        }
        key = applicationName + "_" + targetClass.getName() + "_" + methodName + "_" + key;
        log.info("限流锁key={}", key);
        RRateLimiter rateLimiter = this.redissonClient.getRateLimiter(key);
        if (!rateLimiter.isExists()) {
            log.info("设置流量,rate={},rateInterval={},rateIntervalUnit={}", rate, rateInterval, rateIntervalUnit);
            rateLimiter.trySetRate(RateType.OVERALL, rate, rateInterval, rateIntervalUnit);
            //设置一个过期时间，避免key一直存在浪费内存
            this.redissonClient.getBucket(key).expire(Duration.ofSeconds(Long.sum(RandomUtil.randomLong(5, 10) * 60, rateInterval)));
        }
        boolean acquire = rateLimiter.tryAcquire(1);
        if (!acquire) {
            throw new BizException(globalRateLimiter.errMsg());
        }
        return joinPoint.proceed();
    }
}
