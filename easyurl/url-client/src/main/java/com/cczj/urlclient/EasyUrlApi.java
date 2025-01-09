package com.cczj.urlclient;

import com.cczj.urlclient.pojo.request.UrlGenerateRequest;
import com.cczj.urlclient.pojo.response.R;

public interface EasyUrlApi {

    R<String> generateUrl(UrlGenerateRequest request);
}
