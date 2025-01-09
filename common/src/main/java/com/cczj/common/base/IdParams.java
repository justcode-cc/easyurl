package com.cczj.common.base;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel(value = "IdParams")
public class IdParams {

    @NotNull(message = "id为空")
    private Integer id;

}
