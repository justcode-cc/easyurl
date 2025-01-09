package com.cczj.urlbean.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel
public class UrlMappingListVO {

    private Long id;

    private String originUrl;

    private String shortUrl;

    private Integer status;

    private LocalDateTime expireTime;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private String createUser;

}
