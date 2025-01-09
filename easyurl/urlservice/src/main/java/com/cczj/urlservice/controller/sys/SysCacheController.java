package com.cczj.urlservice.controller.sys;

import com.cczj.common.base.IdLongParams;
import com.cczj.common.base.R;
import com.cczj.common.bean.params.sys.SysCacheListParams;
import com.cczj.common.bean.params.sys.SysCacheParams;
import com.cczj.common.entity.sys.SysCacheEntity;
import com.cczj.urlservice.service.sys.SysCacheService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sysCache")
@RequiredArgsConstructor
public class SysCacheController {

    private final SysCacheService sysCacheService;

    @PostMapping("/addCache")
    @ApiOperation(value = "新增缓存")
    R<Object> addCache(@RequestBody SysCacheParams params) {
        return sysCacheService.addCache(params);
    }

    @PostMapping("/editCache")
    @ApiOperation(value = "编辑缓存")
    R<Object> editCache(@RequestBody SysCacheParams params) {
        return sysCacheService.editCache(params);
    }

    @PostMapping("/deleteCache")
    @ApiOperation(value = "删除缓存记录")
    R<Object> deleteCache(@RequestBody@Validated IdLongParams params) {
        return sysCacheService.deleteCache(params);
    }

    @PostMapping("/pageList")
    @ApiOperation(value = "分页查询缓存列表")
    R<PageInfo<SysCacheEntity>> pageList( @RequestBody @Validated SysCacheListParams params) {
        return this.sysCacheService.pageList(params);
    }

    @PostMapping("/flushCache")
    @ApiOperation(value = "刷新缓存")
    R<Object> flushCache(@RequestBody@Validated IdLongParams params) {
        return this.sysCacheService.flushCache(params);
    }

    @PostMapping("/info")
    @ApiOperation(value = "查询缓存详情")
    R<SysCacheEntity> info(@RequestBody @Validated IdLongParams params) {
        return this.sysCacheService.info(params);
    }

}
