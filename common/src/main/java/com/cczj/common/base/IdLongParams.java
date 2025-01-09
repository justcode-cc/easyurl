package com.cczj.common.base;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel(value = "IdLongParams")
public class IdLongParams {


    @NotNull(message = "id为空")
    private Long id;
}
