package com.cczj.common.enums.url;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UrlStatusEnum {


    normal(1, "正常"),

    offline(2, "下线"),

    timeout(3, "过期"),


    ;

    private final Integer value;
    private final String name;
}
