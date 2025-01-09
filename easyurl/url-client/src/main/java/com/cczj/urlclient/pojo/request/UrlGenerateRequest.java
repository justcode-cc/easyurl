package com.cczj.urlclient.pojo.request;

import java.time.LocalDateTime;

public class UrlGenerateRequest {

    /**
     * 源url
     */
    private String originUrl;

    /**
     * 过期时间: 单位为秒，优先级更高
     */
    private Long expireTime;

    /**
     * 过期时间: 指定的过期时间，优先级较低
     */
    private LocalDateTime expireDateTime;

    public String getOriginUrl() {
        return originUrl;
    }

    public UrlGenerateRequest setOriginUrl(String originUrl) {
        this.originUrl = originUrl;
        return this;
    }

    public Long getExpireTime() {
        return expireTime;
    }

    public UrlGenerateRequest setExpireTime(Long expireTime) {
        this.expireTime = expireTime;
        return this;
    }

    public LocalDateTime getExpireDateTime() {
        return expireDateTime;
    }

    public UrlGenerateRequest setExpireDateTime(LocalDateTime expireDateTime) {
        this.expireDateTime = expireDateTime;
        return this;
    }
}
