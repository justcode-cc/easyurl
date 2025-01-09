package com.cczj.common.entity.sys;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cczj.common.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@TableName("sys_menu")
@Accessors(chain = true)
public class SysMenuEntity extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 菜单名称 */
    private String menuName;

    /** 父菜单ID */
    private Long parentId;

    /** 显示顺序 */
    private Integer orderNum;

    /** 路由地址 */
    private String path;

    /** 组件路径 */
    private String component;

    /** 路由参数 */
    private String query;

    /** 是否为外链（1是 0否） */
    private Integer isFrame;

    /** 是否缓存（1缓存 0不缓存） */
    private Integer isCache;

    /** 类型（M目录 C菜单 F按钮） */
    private String menuType;

    /** 显示状态（1显示 0隐藏） */
    private Integer visible;

    /** 菜单状态（1正常 0停用） */
    private Integer status;

    /** 菜单图标 */
    private String icon;

}
