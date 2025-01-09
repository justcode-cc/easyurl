package com.cczj.framework.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Set;

@Data
@ConfigurationProperties(prefix = "ignore")
@Component
public class IgnoreUrls {

    /**
     * 忽略urls
     */
    private Set<String> url;

}
