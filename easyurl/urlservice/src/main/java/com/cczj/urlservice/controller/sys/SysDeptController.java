package com.cczj.urlservice.controller.sys;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.hutool.core.lang.tree.Tree;
import com.cczj.common.base.IdLongParams;
import com.cczj.common.base.R;
import com.cczj.common.bean.params.sys.SysDeptListParams;
import com.cczj.common.bean.params.sys.SysDeptParams;
import com.cczj.common.entity.sys.SysDeptEntity;
import com.cczj.urlservice.service.sys.SysDeptService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

//@SaCheckRole("admin")
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/sysDept")
@Api(tags = "部门接口")
@SaCheckRole({"1"})
public class SysDeptController {

    private final SysDeptService sysDeptService;

    @PostMapping(value = "/addDept")
    @ApiOperation(value = "新增部门")
    R<Object> addDept(@RequestBody @Validated SysDeptParams params) {
        return this.sysDeptService.addDept(params);
    }

    @PostMapping(value = "/editDept")
    @ApiOperation(value = "编辑部门")
    R<Object> editDept(@RequestBody @Validated SysDeptParams params) {
        return this.sysDeptService.editDept(params);
    }

    @PostMapping(value = "/pageList")
    @ApiOperation(value = "部门列表")
    R<List<SysDeptEntity>> pageList(@RequestBody @Validated SysDeptListParams params) {
        return this.sysDeptService.pageList(params);
    }

    @PostMapping(value = "/deptTreeList")
    @ApiOperation(value = "菜单树")
    R<List<Tree<Long>>> deptTreeList(@RequestBody IdLongParams params) {
        return this.sysDeptService.deptTreeList(params);
    }

    @PostMapping(value = "/info")
    @ApiOperation(value = "部门详情")
    R<SysDeptEntity> info(@RequestBody @Validated IdLongParams params) {
        return this.sysDeptService.info(params);
    }

    @PostMapping(value = "/delete")
    @ApiOperation(value = "删除部门")
    R<Object> delete(@RequestBody @Validated IdLongParams params) {
        return this.sysDeptService.delete(params);
    }
}
