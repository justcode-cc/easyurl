package com.cczj.framework.exception;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotRoleException;
import cn.dev33.satoken.exception.SaTokenException;
import cn.hutool.core.util.StrUtil;
import com.cczj.common.base.BizException;
import com.cczj.common.base.R;
import com.cczj.common.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.util.Arrays;
import java.util.Objects;

@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE + 100)
public class GlobalExceptionHandler {

    @Resource
    private HttpServletRequest request;

    private String getRequestPath() {
        String requestPath = "";
        if (this.request != null) {
            requestPath = this.request.getServletPath();
        }
        return requestPath;
    }

    private R<Object> response(R<Object> r) {
        log.info("全局异常,响应结果:{}", JsonUtil.toJsonString(r));
        return r;
    }

    @ExceptionHandler(SaTokenException.class)
    @ResponseStatus(HttpStatus.OK)
    public R<Object> bizExceptionHandler(SaTokenException e) {
        log.error("[全局异常]requestPath:{},自定义业务异常,异常代码:{},异常信息:{}", getRequestPath(), e.getCode(), e.getMessage(), e);
        R<Object> r;
        if (e instanceof NotLoginException) {
            r = R.fail("401", "用户登录失效", null);
        } else if (e instanceof NotRoleException) {
            r = R.fail("402", "未授权不能访问", null);
        } else {
            r = R.fail("401", "用户登录失效", null);
        }
        return this.response(r);
    }

    @ExceptionHandler(NotRoleException.class)
    @ResponseStatus(HttpStatus.OK)
    public R<Object> bizExceptionHandler2(NotRoleException e) {
        log.error("[全局异常]requestPath:{},自定义业务异常,异常代码:{},异常信息:{}", getRequestPath(), e.getCode(), e.getMessage(), e);
        R<Object> r = R.fail("权限不足", null);
        return this.response(r);
    }

    @ExceptionHandler(BizException.class)
    @ResponseStatus(HttpStatus.OK)
    public R<Object> bizExceptionHandler(BizException e) {
        log.error("[全局异常]requestPath:{},自定义业务异常,异常代码:{},异常信息:{}", getRequestPath(), e.getCode(), e.getMessage(), e);
        R<Object> r = R.fail(StrUtil.sub(e.getMessage(), 0, 30), null);
        return this.response(r);
    }

    @ExceptionHandler({
            ServletRequestBindingException.class,
            HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class
    })
    @ResponseStatus(HttpStatus.OK)
    public R<Object> badRequestExceptionHandler(Exception e) {
        log.error("[全局异常]requestPath:{},请求参数相关异常,异常信息:{}", getRequestPath(), e.getMessage(), e);
        R<Object> r = R.fail("请求参数格式错误", null);
        return this.response(r);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.OK)
    public R<Object> methodNotAllowedExceptionHandler(HttpRequestMethodNotSupportedException e) {
        log.error("[全局异常]错误的请求方式,requestPath:{},请求方式:{},支持的请求方式:{}",
                getRequestPath(), e.getMethod(), Arrays.toString(e.getSupportedMethods()));
        R<Object> r = R.fail("请求方式不允许", null);
        return this.response(r);
    }

    @ExceptionHandler({ConstraintViolationException.class, MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.OK)
    public R<Object> badParamsRequestHandler(Exception e) {
        String message = "参数校验【未知错误】";
        if (e.getClass().isAssignableFrom(ConstraintViolationException.class)) {
            message = ((ConstraintViolationException) e).getConstraintViolations().iterator().next()
                    .getMessage();
        } else if (e.getClass().isAssignableFrom(MethodArgumentNotValidException.class)) {
            message = Objects.requireNonNull(
                            ((MethodArgumentNotValidException) e).getBindingResult().getFieldError())
                    .getDefaultMessage();
        }
        log.error("[全局异常]requestPath:{}，参数校验失败:{}", getRequestPath(), message, e);
        R<Object> r = R.fail(message, null);
        return this.response(r);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.OK)
    public R<Object> serverExceptionHandler(Exception e) {
        log.error("[全局异常]requestPath={},服务器未知异常:{}", getRequestPath(), e.getMessage(), e);
        if (e instanceof BizException) {
            return R.fail(e.getMessage());
        }
        R<Object> r = R.fail("网络异常", null);
        return this.response(r);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.OK)
    public R<Object> MaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        log.error("[全局异常]文件上传大小超限制,requestPath={},最大字节:{}", getRequestPath(), e.getMaxUploadSize());
        R<Object> r = R.fail("上传文件大小超过限制", null);
        return this.response(r);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.OK)
    public R<Object> NoHandlerFoundException(Exception e) {
        log.error("[全局异常]请求接口路径不存在,requestPath={}", getRequestPath());
        R<Object> r = R.fail("请求接口路径不存在", null);
        return this.response(r);
    }

}
