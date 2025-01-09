package com.cczj.common.dto;

import lombok.Data;

@Data
public class SysUserJobDTO {

    private Long id;

    private Long roleId;

    private String roleName;

    private Long deptId;

    private Integer deptAdmin;
}
