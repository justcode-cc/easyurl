package com.cczj.common.entity.sys;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cczj.common.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName("sys_user")
public class SysUserEntity extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    private String account;

    private String phone;

    private String password;

    private String salt;

    private Integer status;



}
