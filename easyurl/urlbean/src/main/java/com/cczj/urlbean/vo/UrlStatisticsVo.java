package com.cczj.urlbean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class UrlStatisticsVo {

    @ApiModelProperty(value = "总计")
    private Integer total;

    private Integer preWeekTotal;

    private BigDecimal preWeekRatio;

    @ApiModelProperty(value = "新增")
    private Integer thisWeekAdd;

    private Integer preWeekAdd;

    private BigDecimal preWeekAddRatio;

    @ApiModelProperty(value = "有效")
    private Integer valid;

    private Integer preWeekValid;

    private BigDecimal preWeekValidRatio;

    @ApiModelProperty(value = "过期")
    private Integer expire;

    private Integer preWeekExpire;

    private BigDecimal preWeekExpireRatio;
}
