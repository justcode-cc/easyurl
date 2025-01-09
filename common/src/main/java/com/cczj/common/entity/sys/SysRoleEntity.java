package com.cczj.common.entity.sys;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cczj.common.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName("sys_role")
public class SysRoleEntity extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Integer mainRole;

    private String roleName;

    private Long parentId;



}
