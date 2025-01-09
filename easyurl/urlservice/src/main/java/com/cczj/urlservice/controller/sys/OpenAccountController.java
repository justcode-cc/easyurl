package com.cczj.urlservice.controller.sys;

import com.cczj.common.base.IdLongParams;
import com.cczj.common.base.R;
import com.cczj.common.bean.params.sys.OpenAccountAddParams;
import com.cczj.common.bean.params.sys.OpenAccountPageParams;
import com.cczj.common.bean.vo.sys.OpenAccountPageVO;
import com.cczj.urlservice.service.sys.OpenAccountService;
import com.github.pagehelper.PageInfo;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/openAccount")
@Api(tags = "账户配置")
public class OpenAccountController {

    private final OpenAccountService openAccountService;


    @PostMapping(value = "/addOpenAccount")
    @ApiOperation(value = "添加账户配置")
    @ApiOperationSupport(order = 10)
    R<Object> addOpenAccount(@RequestBody @Validated OpenAccountAddParams params) {
        return this.openAccountService.addOpenAccount(params);
    }

    @PostMapping(value = "/updateOpenAccount")
    @ApiOperation(value = "修改账户配置")
    @ApiOperationSupport(order = 20)
    R<Object> updateOpenAccount(@RequestBody @Validated OpenAccountAddParams params) {
        return this.openAccountService.updateOpenAccount(params);
    }

    @PostMapping(value = "/getOpenAccountPage")
    @ApiOperation(value = "获取账户分页列表")
    @ApiOperationSupport(order = 30)
    R<PageInfo<OpenAccountPageVO>> getOpenAccountPage(@RequestBody @Validated OpenAccountPageParams params) {
        return this.openAccountService.getOpenAccountPage(params);
    }

    @PostMapping(value = "/deleteOpenAccount")
    @ApiOperation(value = "删除账户配置")
    @ApiOperationSupport(order = 40)
    R<Object> deleteOpenAccount(@RequestBody @Validated IdLongParams params) {
        return this.openAccountService.deleteOpenAccount(params);
    }



}
