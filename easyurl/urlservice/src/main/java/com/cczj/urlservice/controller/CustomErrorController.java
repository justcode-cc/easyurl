package com.cczj.urlservice.controller;

import com.cczj.common.base.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@Api(hidden = true)
public class CustomErrorController implements ErrorController {

    //保底错误处理，主要是处理404
    @RequestMapping(value = "/error")
    @ApiOperation(hidden = true, value = "")
    public R<String> error(HttpServletRequest request) {
        return R.fail("请求错误,请确认接口路径是否正确或其他错误");
    }


}
