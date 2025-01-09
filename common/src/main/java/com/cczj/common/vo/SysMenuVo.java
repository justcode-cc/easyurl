package com.cczj.common.vo;

import com.cczj.common.entity.sys.SysMenuEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SysMenuVo extends SysMenuEntity {

    private List<SysMenuVo> children;
}
