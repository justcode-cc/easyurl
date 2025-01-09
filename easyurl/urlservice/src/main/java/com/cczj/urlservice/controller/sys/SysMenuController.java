package com.cczj.urlservice.controller.sys;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.hutool.core.lang.tree.Tree;
import com.cczj.common.base.IdLongParams;
import com.cczj.common.base.R;
import com.cczj.common.bean.params.sys.SysMenuListParams;
import com.cczj.common.bean.params.sys.SysMenuParams;
import com.cczj.common.entity.sys.SysMenuEntity;
import com.cczj.urlservice.service.sys.SysMenuService;
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
@RequestMapping(value = "/sysMenu")
@Api(tags = "菜单接口")
@SaCheckRole({"1"})
public class SysMenuController {

    private final SysMenuService sysMenuService;

    @PostMapping(value = "/addMenu")
    @ApiOperation(value = "新增菜单")
    R<Object> addMenu(@RequestBody @Validated SysMenuParams params) {
        return sysMenuService.addMenu(params);
    }

    @PostMapping(value = "/editMenu")
    @ApiOperation(value = "编辑菜单")
    R<Object> editMenu(@RequestBody @Validated SysMenuParams params) {
        return sysMenuService.editMenu(params);
    }

    @PostMapping(value = "/menuList")
    @ApiOperation(value = "菜单列表")
    R<List<SysMenuEntity>> menuList(@RequestBody @Validated SysMenuListParams params) {
        return sysMenuService.menuList(params);
    }

    @PostMapping(value = "/treeList")
    @ApiOperation(value = "菜单树")
    R<List<Tree<Long>>> treeList(@RequestBody IdLongParams params) {
        return sysMenuService.treeList(params);
    }

    @PostMapping(value = "/info")
    @ApiOperation(value = "菜单详情")
    R<SysMenuEntity> info(@RequestBody @Validated IdLongParams params) {
        return sysMenuService.info(params);
    }

    @PostMapping(value = "/delete")
    @ApiOperation(value = "删除菜单")
    R<Object> delete(@RequestBody @Validated IdLongParams params) {
        return sysMenuService.delete(params);
    }


}
