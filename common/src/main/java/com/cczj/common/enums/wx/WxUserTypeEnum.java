package com.cczj.common.enums.wx;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WxUserTypeEnum {

    sys(1, "系统用户");

    private final Integer value;
    private final String name;

}
