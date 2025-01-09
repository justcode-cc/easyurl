package com.cczj.urlservice.controller.sys;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.cczj.common.base.IdLongParams;
import com.cczj.common.base.R;
import com.cczj.common.bean.params.sys.SysUserListParams;
import com.cczj.common.bean.params.sys.SysUserParams;
import com.cczj.common.bean.params.sys.SysUserStatusParams;
import com.cczj.common.bean.vo.sys.SysUserListVo;
import com.cczj.urlservice.service.sys.SysUserService;
import com.github.pagehelper.PageInfo;
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
@RequestMapping(value = "/sysUser")
@Api(tags = "用户接口")
@SaCheckRole({"1"})
public class SysUserController {

    private final SysUserService sysUserService;


    @PostMapping(value = "/addUser")
    @ApiOperation(value = "新增用户")
    R<Object> addUser(@RequestBody @Validated SysUserParams params) {
        return sysUserService.addUser(params);
    }

    @PostMapping(value = "/editUser")
    @ApiOperation(value = "编辑用户")
    R<Object> editUser(@RequestBody @Validated SysUserParams params) {
        return sysUserService.editUser(params);
    }

    @PostMapping(value = "/userList")
    @ApiOperation(value = "用户列表")
    R<PageInfo<SysUserListVo>> userList(@RequestBody @Validated SysUserListParams params) {
        return this.sysUserService.userList(params);
    }

    @PostMapping(value = "/userDetail")
    @ApiOperation(value = "用户信息")
    R<SysUserListVo> userDetail(@RequestBody @Validated IdLongParams params) {
        return this.sysUserService.userDetail(params);
    }

    @PostMapping(value = "/changeStatus")
    @ApiOperation(value = "修改用户状态")
    R<Object> changeStatus(@RequestBody @Validated SysUserStatusParams params) {
        return this.sysUserService.changeStatus(params);
    }


}
