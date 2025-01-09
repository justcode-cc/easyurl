package com.cczj.common.bean.params.sys;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@ApiModel
public class SysCacheParams {

    private Long id;

    @NotBlank(message = "缺少缓存key")
    @Length(max = 255, message = "缓存key长度不能超过255")
    private String cacheKey;

    @NotBlank(message = "缺少缓存value")
    @Length(max = 255, message = "缓存value长度不能超过255")
    private String cacheValue;

    @NotNull(message = "缺少缓存过期时间")
    private Integer cacheTtl;


}
