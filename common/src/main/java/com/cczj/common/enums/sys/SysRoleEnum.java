package com.cczj.common.enums.sys;

import com.cczj.common.base.BizException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SysRoleEnum {

    //新角色在下面 1-10001之间继续添加
    SYSTEM(1L, "系统管理员", 1),




    //1-10001为系统预占
    operate(10001L, "预分配角色", 0);

    private final Long value;
    private final String name;
    //是否为主管
    private final Integer isAdmin;


    public static String getNameByValue(Long value) {
        for (SysRoleEnum roleEnum : SysRoleEnum.values()) {
            if (roleEnum.getValue().equals(value)) {
                return roleEnum.getName();
            }
        }
        return null;
    }

    public static SysRoleEnum getByValue(Long value) {
        for (SysRoleEnum roleEnum : SysRoleEnum.values()) {
            if (roleEnum.getValue().equals(value)) {
                return roleEnum;
            }
        }
        throw new BizException("无效的角色值");
    }
}
