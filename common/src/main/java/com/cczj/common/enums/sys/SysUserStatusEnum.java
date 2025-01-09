package com.cczj.common.enums.sys;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SysUserStatusEnum {


    normal(1, "正常"),
    lock(0, "锁定");

    private final Integer value;
    private final String name;

}
