package com.cczj.framework.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "super-pwd")
public class SuperPwdConfig {

    private boolean enable;

    private String pwd;
}
