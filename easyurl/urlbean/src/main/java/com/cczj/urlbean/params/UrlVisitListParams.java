package com.cczj.urlbean.params;

import com.cczj.common.base.PageParams;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UrlVisitListParams extends PageParams {

    private String beginDate;

    private String endDate;

    private String shortUrl;

}
