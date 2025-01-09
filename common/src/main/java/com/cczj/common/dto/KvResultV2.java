package com.cczj.common.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel(value = "KvResultV2")
public class KvResultV2 {

    private String name;
    private BigDecimal value;
    private String labelText;


}
