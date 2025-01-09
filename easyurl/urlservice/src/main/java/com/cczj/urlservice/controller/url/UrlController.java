package com.cczj.urlservice.controller.url;

import cn.hutool.core.util.StrUtil;
import com.cczj.common.base.IdLongParams;
import com.cczj.common.base.R;
import com.cczj.common.dto.KvResult;
import com.cczj.urlbean.params.UrlGenerateParams;
import com.cczj.urlbean.params.UrlMappingListParams;
import com.cczj.urlbean.params.UrlVisitListParams;
import com.cczj.urlbean.vo.*;
import com.cczj.urlservice.config.UrlConfig;
import com.cczj.urlservice.service.url.UrlService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/url")
public class UrlController {

    private final UrlService urlService;
    private final UrlConfig urlConfig;

    @RequestMapping(value = "/generate")
    @ApiOperation(value = "生成短链接")
    public R<String> generate(@RequestBody @Validated UrlGenerateParams params) {
        return this.urlService.generate(params);
    }

    @RequestMapping(value = "/redirect/{shortUrl}")
    @ApiOperation(value = "重定向")
    public RedirectView redirect(@PathVariable String shortUrl, HttpServletRequest request) {
        String originUrl = this.urlService.getOriginUrl(shortUrl, request);
        if (StrUtil.isBlank(originUrl)) {
            originUrl = this.urlConfig.getNotfoundUrl();
        }
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl(originUrl);
        redirectView.setStatusCode(HttpStatus.FOUND);
        return redirectView;
    }

    @PostMapping(value = "/list")
    @ApiOperation(value = "列表")
    R<PageInfo<UrlMappingListVO>> list(@RequestBody @Validated UrlMappingListParams params) {
        return this.urlService.list(params);
    }

    @PostMapping(value = "/visitList")
     @ApiOperation(value = "访问列表")
    R<PageInfo<UrlVisitListVO>> visitList(@RequestBody @Validated UrlVisitListParams params) {
        return this.urlService.visitList(params);
    }

    @PostMapping(value = "/offline")
    @ApiOperation(value = "下线")
    R<Object> offline(@RequestBody @Validated IdLongParams params) {
        return this.urlService.offline(params);
    }
    @PostMapping(value = "/statistics")
    @ApiOperation(value = "统计")
    R<UrlStatisticsVo> statistics() {
        return this.urlService.statistics();
    }

    @PostMapping(value = "/visitLead")
    @ApiOperation(value = "访问趋势")
    R<List<UrlVisitLeadVo>> visitLead() {
        return this.urlService.visitLead();
    }

    @PostMapping(value = "/getWeekAddUrl")
    @ApiOperation(value = "获取7日新增的url")
    R<List<KvResult>> getWeekAddUrl(){
        return this.urlService.getWeekAddUrl();
    }

    @PostMapping(value = "/getUrlStatusPie")
    @ApiOperation(value = "获取url状态饼图")
    R<UrlPieVO> getUrlStatusPie(){
        return this.urlService.getUrlStatusPie();
    }

}
