package com.cczj.urlservice.service.sys;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cczj.common.entity.sys.SysRoleMenuEntity;
import com.cczj.urlservice.mapper.sys.SysRoleMenuMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SysRoleMenuService extends ServiceImpl<SysRoleMenuMapper, SysRoleMenuEntity> {
}
