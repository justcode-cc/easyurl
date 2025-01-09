package com.cczj.common.bean.params.sys;

import com.cczj.common.base.PageParams;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SysCacheListParams extends PageParams {

    private String cacheKey;

}
