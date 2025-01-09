package com.cczj.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CommonStatusEnum {

    normal(1, "正常"),
    stop(0, "停用");

    private final Integer value;
    private final String name;
}
