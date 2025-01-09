package com.cczj.urlservice.controller;

import com.cczj.common.base.IdLongParams;
import com.cczj.common.base.R;
import com.cczj.common.bean.params.LogonParams;
import com.cczj.common.bean.params.sys.SysUserUpdatePwdParams;
import com.cczj.common.dto.LogonUser;
import com.cczj.common.vo.LogonKeyIvVo;
import com.cczj.common.vo.RouterVo;
import com.cczj.framework.aop.GlobalRateLimiter;
import com.cczj.urlservice.service.BaseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/base")
@Api(tags = "基础接口")
public class BaseController {

    private final BaseService baseService;
    private final HttpServletRequest request;

    @PostMapping(value = "/getKeyIv")
    @ApiOperation(value = "获取key和iv")
    R<LogonKeyIvVo> getKeyIv() {
        return this.baseService.getKeyIv(request);
    }

    @ApiOperation(value = "登录")
    @PostMapping(value = "/logon")
    @GlobalRateLimiter(key = "#params.account", rate = 1)
    R<LogonUser> logon(@RequestBody @Validated LogonParams params) {
        return this.baseService.logon(params);
    }

    @PostMapping(value = "/userInfo")
    @ApiOperation(value = "获取用户信息")
    R<LogonUser> userInfo() {
        return this.baseService.userInfo();
    }

    @PostMapping(value = "/updatePwd")
    @ApiOperation(value = "修改密码")
    R<Object> updatePwd(@RequestBody @Validated SysUserUpdatePwdParams params) {
        return this.baseService.updatePwd(params);
    }

    @PostMapping(value = "/getRouters")
    @ApiOperation(value = "获取路由")
    R<List<RouterVo>> getRouters() {
        return this.baseService.getRouters();
    }

    @PostMapping(value = "/changeJob")
    @ApiOperation(value = "切换岗位")
    R<LogonUser> changeJob(@RequestBody @Validated IdLongParams params) {
        return this.baseService.changeJob(params);
    }

    @PostMapping(value = "/logout")
    @ApiOperation(value = "退出登录")
    R<Object> logout() {
        return this.baseService.logout();
    }

}


