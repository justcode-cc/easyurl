package com.cczj.common.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@ApiModel(value = "KvResult")
@RequiredArgsConstructor
public class KvResult {

    private final String value;

    private final String name;


}
