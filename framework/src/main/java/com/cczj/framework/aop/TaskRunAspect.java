package com.cczj.framework.aop;

import com.cczj.common.base.ContextHolder;
import com.cczj.common.utils.MdcUtils;
import com.cczj.framework.utils.IpUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;


@Aspect
@Component
@Slf4j
public class TaskRunAspect {

    @Value("${task_execute_ip:none}")
    private String executeIp;

    @Pointcut("@annotation(com.cczj.framework.aop.TaskRunManager)")
    public void cut() {
    }

    @Around(value = "cut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        Class<?> targetClass = joinPoint.getTarget().getClass();
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Class<?>[] parameterTypes = methodSignature.getParameterTypes();
        Method method = targetClass.getDeclaredMethod(methodName, parameterTypes);
        TaskRunManager birdTaskCheck = method.getDeclaredAnnotation(TaskRunManager.class);
        String localhostIp = IpUtils.getLocalHostAddress();
        String taskName = birdTaskCheck.name();
        String threadName = Thread.currentThread().getName();
        try {
            MdcUtils.setRequestId();
            if (!"none".equals(executeIp) && executeIp.contains(localhostIp)) {
                log.info("非执行定时任务服务器,本机IP:{}", localhostIp);
                return null;
            }
            Thread.currentThread().setName(taskName);
            long l = System.currentTimeMillis();
            log.info("{}-开始执行", taskName);
            Object proceed = joinPoint.proceed();
            log.info("{}-执行完成,耗时{}ms", taskName, System.currentTimeMillis() - l);
            return proceed;
        } catch (Exception e) {
            log.error("{}-执行异常", taskName, e);
            throw e;
        } finally {
            Thread.currentThread().setName(threadName);
            MdcUtils.clearRequestId();
            ContextHolder.clear();
        }
    }

}
