package com.cczj.common.entity.wx;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cczj.common.entity.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@TableName("wx_attention")
public class WxAttentionEntity extends BaseEntity {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @ApiModelProperty(value = "用户类型  WxUserTypeEnum  ")
    private Integer userType;

    private Long userId;

    private String openId;

    private String unionId;

    private String appId;

    @ApiModelProperty(value = "二维码")
    private String qrCode;

    @ApiModelProperty(value = "二维码过期时间")
    private LocalDateTime expireTime;

    @ApiModelProperty(value = "是否关注 1关注 0未关注 WxAttentionEnum ")
    private Integer attention;



}
