package com.cczj.urlservice.interceptor;

import com.cczj.common.dto.LogonUser;
import com.cczj.framework.utils.IpUtils;
import com.cczj.framework.utils.LogonUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


@Slf4j
@Aspect
@Component
@Order(1)
public class LogInterceptor {

    @Resource
    private ObjectMapper objectMapper;
    private final DefaultParameterNameDiscoverer discoverer = new DefaultParameterNameDiscoverer();

    @Pointcut("@within(org.springframework.web.bind.annotation.RestController) || " +
            "@within(org.springframework.stereotype.Controller) || " +
            "@annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public void httpRequest() {
    }

    @Around("httpRequest()")
    public Object aroundController(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        long beginTime = System.currentTimeMillis();
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        HttpServletResponse httpServletResponse = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        int status = 0;
        if (httpServletResponse != null) {
            status = httpServletResponse.getStatus();
        }
        String methodName = proceedingJoinPoint.getSignature().getName();
        Class<?> targetClass = proceedingJoinPoint.getTarget().getClass();
        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
        Class<?>[] parameterTypes = methodSignature.getParameterTypes();
        Method method = targetClass.getDeclaredMethod(methodName, parameterTypes);

        String[] parameterNames = discoverer.getParameterNames(method);
        Map<String, Object> paramMap = new HashMap<>();
        if (parameterNames != null && parameterNames.length > 0) {
            Object[] params = proceedingJoinPoint.getArgs();
            //处理包括在实体类中的文件时会报错，建议文件不要放在实体类而是单独作为一个参数
            //有空做一个对json内容的泛解析
            int fileSize = 0;
            for (int i = 0; i < parameterNames.length; i++) {
                if (params[i] instanceof MultipartFile) {
                    MultipartFile file = (MultipartFile) params[i];
                    Map<String, Object> fileParam = new HashMap<>();
                    fileParam.put("name", file.getName());
                    fileParam.put("size", file.getSize());
                    fileParam.put("originalFileName", file.getOriginalFilename());
                    fileParam.put("contentType", file.getContentType());
                    paramMap.put("file" + ++fileSize, fileParam);
                    continue;
                } else if (params[i] instanceof HttpServletRequest || params[i] instanceof ServletRequest
                        || params[i] instanceof HttpServletResponse || params[i] instanceof ServletResponse) {
                    continue;
                }
                paramMap.put(parameterNames[i], params[i]);
            }
        }
        String paramsValue = "";
        try {
            paramsValue = this.objectMapper.writeValueAsString(paramMap);
        } catch (Exception e) {
            log.error("解析参数失败,path={}", request.getServletPath(), e);
        }
        LogonUser logonUser = LogonUtils.getCurrentUserQuiet();
        String userPrint = "";
        if (logonUser != null) {
            userPrint = String.format("%s-%s", logonUser.getJobId(), logonUser.getAccount());
        } else {
            userPrint = "免密访问";
        }

        String requestIp = IpUtils.getIpAddress(request);
        log.info("ip:{},请求人:{},请求路径:{},请求参数:{}", requestIp, userPrint, request.getServletPath(), paramsValue);
        Object proceed = null;
        try {
            proceed = proceedingJoinPoint.proceed();
        } finally {
            log.info("ip:{},请求人:{},响应状态码:{},请求耗时:{}ms,响应结果:{}", requestIp, userPrint, status,
                    (System.currentTimeMillis() - beginTime),
                    this.objectMapper.writeValueAsString(proceed)
            );
        }
        return proceed;
    }


}
