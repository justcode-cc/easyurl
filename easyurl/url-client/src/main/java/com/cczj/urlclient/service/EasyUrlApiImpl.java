package com.cczj.urlclient.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.cczj.urlclient.EasyUrlApi;
import com.cczj.urlclient.config.OpenAccountConfig;
import com.cczj.urlclient.pojo.request.UrlGenerateRequest;
import com.cczj.urlclient.pojo.response.R;

public class EasyUrlApiImpl implements EasyUrlApi {

    private String requestUrl = "";

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    private OpenAccountConfig openAccountConfig;

    public OpenAccountConfig getOpenAccountConfig() {
        return openAccountConfig;
    }

    public void setOpenAccountConfig(OpenAccountConfig openAccountConfig) {
        this.openAccountConfig = openAccountConfig;
    }

    @Override
    public R<String> generateUrl(UrlGenerateRequest request) {
        String res = HttpUtil.createPost(this.requestUrl + "/openApi/url/generate")
                .header("appId", this.openAccountConfig.getAppId())
                .header("appSecret", this.openAccountConfig.getAppSecret())
                .body(JSONUtil.toJsonStr(request))
                .execute().body();
        if (StrUtil.isBlank(res)) {
            return R.fail("生成短链接失败");
        }
        return  JSONUtil.toBean(res, R.class);
    }
}
