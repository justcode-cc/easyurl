package com.cczj.urlservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@ConfigurationProperties(prefix = "url")
@Component
public class UrlConfig {

    private String maxTryKey = "max_try";

    private Integer maxTry = 10;

    private String baseUrl;

    private String notfoundUrl;

}
