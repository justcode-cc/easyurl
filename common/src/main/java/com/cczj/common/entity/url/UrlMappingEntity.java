package com.cczj.common.entity.url;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("url_mapping")
public class UrlMappingEntity {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String originUrl;

    private String shortUrl;

    @ApiModelProperty(value = "状态  见 UrlStatusEnum  ")
    private Integer status;

    private LocalDateTime expireTime;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Long createUserId;


}
