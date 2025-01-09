package com.cczj.framework.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Set;

@Data
@ConfigurationProperties(prefix = "xss")
@Component
public class XssIgnoreUrls {

    /**
     * 忽略urls
     */
    private Set<String> url;

}
