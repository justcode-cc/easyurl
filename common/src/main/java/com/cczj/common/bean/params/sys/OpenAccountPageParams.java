package com.cczj.common.bean.params.sys;

import com.cczj.common.base.PageParams;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OpenAccountPageParams extends PageParams {

    private String appId;

    private Integer status;
}
