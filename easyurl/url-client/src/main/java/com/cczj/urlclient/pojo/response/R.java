package com.cczj.urlclient.pojo.response;


import java.io.Serializable;


public class R<T> implements Serializable {

    private static final long serialVersionUID = -8143412915723961070L;

    /**
     * 200成功，500失败
     */
    private String code;

    private String msg;

    private String traceId;

    private T data;

    private boolean success;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    private R() {
    }

    public R(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public R(String code, String msg, T t) {
        this.code = code;
        this.msg = msg;
        this.data = t;
    }


    public static <T> R<T> success(T data) {
        R<T> r = new R<>("200", "成功", data);
        r.setSuccess(true);
        return r;
    }

    public static <T> R<T> success() {
        R<T> r = new R<>("200", "成功");
        r.setSuccess(true);
        return r;
    }


    public static <T> R<T> fail(T data) {
        R<T> r = new R<>("500", "失败", data);
        r.setSuccess(false);
        return r;
    }

    public static <T> R<T> fail(String msg) {
        R<T> r = new R<>("500", msg);
        r.setSuccess(false);
        return r;
    }

    public static <T> R<T> fail(String msg, T data) {
        R<T> r = new R<>("500", msg, data);
        r.setSuccess(false);
        return r;
    }

    public static <T> R<T> fail(String code, String msg, T data) {
        R<T> r = new R<>(code, msg, data);
        r.setSuccess(false);
        return r;
    }

    public static <T> R<T> fail() {
        R<T> r = new R<>("500", "失败");
        r.setSuccess(false);
        return r;
    }

    //强制指定返回泛型T 以便于swagger解析
    public T getData() {
        return data;
    }

    /**
     * 判断结果返回是否正确
     * 不判断业务结果，只判断执行结果，即 if code==200
     *
     * @param r 结果集
     * @return true/false
     */
    public static boolean parseCode(R<Object> r) {
        return r != null && "200".equals(r.getCode());
    }

}
