package com.cczj.common.base;

/**
 * 业务异常类
 *
 */
public class BizException extends RuntimeException {

    private String code;

    public String getCode() {
        return code;
    }

    public BizException(String code, String message) {
        super(message);
        this.code = code;
    }

    public BizException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public BizException(String message) {
        super(message);
        this.code = "500";
    }

    public BizException(String message, Exception e) {
        super(message + e.getMessage());
        this.code = "500";
    }

    public static void verify(boolean condition, String message) {
        if (condition) {
            throw new BizException("500", message);
        }
    }

}
