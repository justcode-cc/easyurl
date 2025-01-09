package com.cczj.common.base;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class PageParams {


    @NotNull(message = "未指定分页pageNum")
    private Integer pageNumber = 1;

    @NotNull(message = "未指定分页pageSize")
    private Integer pageSize = 10;
}
