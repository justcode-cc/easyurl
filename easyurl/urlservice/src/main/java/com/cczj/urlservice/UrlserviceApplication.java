package com.cczj.urlservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.cczj.*"})
public class UrlserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UrlserviceApplication.class, args);
    }

}
