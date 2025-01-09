package com.cczj.common.entity.url;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("url_visit")
public class UrlVisitEntity {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @ApiModelProperty(value = "关联的短链url id")
    private Long urlId;

    @ApiModelProperty(value = "访问ip")
    private String ip;

    @ApiModelProperty(value = "浏览器")
    private String browser;

    @ApiModelProperty(value = "浏览器版本")
    private String browserVersion;

    @ApiModelProperty(value = "操作系统")
    private String os;

    @ApiModelProperty(value = "平台")
    private String platform;

    @ApiModelProperty(value = "引擎")
    private String engine;

    @ApiModelProperty(value = "引擎版本")
    private String engineVersion;

    @ApiModelProperty(value = "访问来源")
    private String referrer;

    @ApiModelProperty(value = "1手机移动端 0非移动端")
    private Integer mobile;

    @ApiModelProperty(value = "1有效访问 0无效访问")
    private Integer valid;

    private LocalDateTime createTime;




}
