package com.cczj.common.serialize;


import com.fasterxml.jackson.databind.module.SimpleModule;

public class Long2StrModule extends SimpleModule {
    public Long2StrModule() {
        addSerializer(Long.class, new Long2StrSerializer());
    }
}
