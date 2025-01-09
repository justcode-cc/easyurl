package com.cczj.urlbean.params;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
public class UrlGenerateParams {

    @ApiModelProperty(value = "源url")
    @NotBlank(message = "缺少源url")
    private String originUrl;

    @ApiModelProperty(value = "过期时间: 单位为秒，优先级更高")
    private Long expireTime;

    @ApiModelProperty(value = "过期时间: 指定的过期时间，优先级较低")
    private LocalDateTime expireDateTime;
}
