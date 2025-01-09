package com.cczj.urlbean.params;

import com.cczj.common.base.PageParams;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UrlMappingListParams extends PageParams {

    private String beginDate;

    private String endDate;

    private Integer status;



}
