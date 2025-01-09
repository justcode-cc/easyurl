package com.cczj.common.enums.wx;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WxAttentionEnum {

    yes(1, "已关注"),
    no(0, "未关注");

    private final Integer value;
    private final String name;
}
