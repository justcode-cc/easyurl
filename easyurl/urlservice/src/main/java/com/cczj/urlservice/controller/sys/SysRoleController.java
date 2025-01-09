package com.cczj.urlservice.controller.sys;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.cczj.common.base.IdLongParams;
import com.cczj.common.base.R;
import com.cczj.common.bean.params.sys.SysRoleParams;
import com.cczj.common.bean.params.sys.SysRoleQueryParams;
import com.cczj.common.dto.KvResult;
import com.cczj.common.entity.sys.SysRoleEntity;
import com.cczj.urlservice.service.sys.SysRoleService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/sysRole")
@Api(tags = "角色接口")
@SaCheckRole({"1"})
public class SysRoleController {

    private final SysRoleService sysRoleService;

    @PostMapping(value = "/roleParentList")
    @ApiOperation(value = "可用的父角色列表")
    R<List<KvResult>> roleParentList() {
        return sysRoleService.roleParentList();
    }

    @PostMapping(value = "/roleAllList")
    @ApiOperation(value = "所有的角色列表")
    R<List<KvResult>> roleAllList() {
        return sysRoleService.roleAllList();
    }

    @PostMapping(value = "/addRole")
    @ApiOperation(value = "新增角色")
    R<Object> addRole(@RequestBody @Validated SysRoleParams params) {
        return sysRoleService.addRole(params);
    }

    @PostMapping(value = "/editRole")
    @ApiOperation(value = "编辑角色")
    R<Object> editRole(@RequestBody @Validated SysRoleParams params) {
        return sysRoleService.editRole(params);
    }

    @PostMapping(value = "/delete")
    @ApiOperation(value = "删除角色")
    R<Object> delete(@RequestBody @Validated IdLongParams params) {
        return this.sysRoleService.delete(params);
    }

    @PostMapping(value = "/info")
    @ApiOperation(value = "角色详情")
    R<SysRoleEntity> info(@RequestBody @Validated IdLongParams params) {
        return this.sysRoleService.info(params);
    }

    @PostMapping(value = "/roleList")
    @ApiOperation(value = "角色列表")
    R<PageInfo<SysRoleEntity>> roleList(@RequestBody @Validated SysRoleQueryParams params) {
        return this.sysRoleService.roleList(params);
    }

    @PostMapping(value = "/getRoleMenuIdList")
    @ApiOperation(value = "获取角色菜单id列表")
    R<List<Long>> getRoleMenuIdList(@RequestBody @Validated IdLongParams params) {
        return sysRoleService.getRoleMenuIdList(params);
    }

}
