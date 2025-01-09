package com.cczj.urlservice.controller.url;

import com.cczj.common.base.R;
import com.cczj.urlbean.params.UrlGenerateParams;
import com.cczj.urlservice.service.url.UrlService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/openApi/url")
public class UrlOpenApiController {

    private final UrlService urlService;

    @RequestMapping(value = "/generate")
    @ApiOperation(value = "生成短链接")
    public R<String> generate(@RequestBody @Validated UrlGenerateParams params) {
        return this.urlService.generate(params);
    }
}
