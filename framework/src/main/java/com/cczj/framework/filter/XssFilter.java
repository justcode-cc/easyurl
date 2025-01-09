package com.cczj.framework.filter;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HtmlUtil;
import cn.hutool.json.JSONUtil;
import com.cczj.framework.config.XssIgnoreUrls;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.nio.charset.Charset;
import java.util.*;


@Component
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class XssFilter implements Filter {

    @Resource
    private XssIgnoreUrls xssIgnoreUrls;

    private static final List<String> defaultIgnoreUrls = Arrays.asList(
            "/sysCache/addCache",
            "/sysCache/editCache"
    );


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        if (defaultIgnoreUrls.contains(httpServletRequest.getServletPath()) ||
                (CollUtil.isNotEmpty(xssIgnoreUrls.getUrl()) && xssIgnoreUrls.getUrl().contains(httpServletRequest.getServletPath()))
        ) {
            chain.doFilter(httpServletRequest, response);
        } else {
            chain.doFilter(new XssRequestWrapper((HttpServletRequest) request), response);
        }
    }

    public static class XssRequestWrapper extends HttpServletRequestWrapper {

        public XssRequestWrapper(HttpServletRequest request) {
            super(request);
        }

        @Override
        public String getParameter(String name) {
            String value = super.getParameter(name);
            if (!StrUtil.hasEmpty(value)) {
                value = HtmlUtil.filter(value);
            }
            return value;
        }

        @Override
        public String[] getParameterValues(String name) {
            String[] values = super.getParameterValues(name);
            if (values != null) {
                for (int i = 0; i < values.length; i++) {
                    String value = values[i];
                    if (!StrUtil.hasEmpty(value)) {
                        value = HtmlUtil.filter(value);
                    }
                    values[i] = value;
                }
            }
            return values;
        }

        @Override
        public Map<String, String[]> getParameterMap() {
            Map<String, String[]> parameters = super.getParameterMap();
            Map<String, String[]> map = new LinkedHashMap<>();
            if (parameters != null) {
                for (String key : parameters.keySet()) {
                    String[] values = parameters.get(key);
                    for (int i = 0; i < values.length; i++) {
                        String value = values[i];
                        if (!StrUtil.hasEmpty(value)) {
                            value = HtmlUtil.filter(value);
                        }
                        values[i] = value;
                    }
                    map.put(key, values);
                }
            }
            return map;
        }

        @Override
        public String getHeader(String name) {
            String value = super.getHeader(name);
            if (!StrUtil.hasEmpty(value)) {
                value = HtmlUtil.filter(value);
            }
            return value;
        }

        @Override
        public ServletInputStream getInputStream() throws IOException {
            InputStream in = super.getInputStream();
            StringBuffer body = new StringBuffer();
            InputStreamReader reader = new InputStreamReader(in, Charset.forName("UTF-8"));
            BufferedReader buffer = new BufferedReader(reader);
            String line = buffer.readLine();
            while (line != null) {
                body.append(line);
                line = buffer.readLine();
            }
            buffer.close();
            reader.close();
            in.close();
            String content;
            if (StrUtil.isBlank(body)) {
                content = "";
            } else if (body.toString().startsWith("{")) {
                Map<String, Object> map;
                map = JSONUtil.parseObj(body.toString());
                //这里参数如果是数字类型，可能会发生格式化，比如100.00 被格式化为了100.0，如果有参数签名相关的需求，这里可能会导致签名错误
                Map<String, Object> resultMap = new HashMap<>(map.size());
                for (String key : map.keySet()) {
                    Object val = map.get(key);
                    if (map.get(key) instanceof String) {
                        resultMap.put(key, HtmlUtil.filter(val.toString()));
                    } else {
                        resultMap.put(key, val);
                    }
                }
                content = JSONUtil.toJsonStr(resultMap);
            } else if (body.toString().startsWith("[")) {
                content = body.toString();
            } else {
                content = HtmlUtil.filter(body.toString());
            }
            final ByteArrayInputStream bain = new ByteArrayInputStream(content.getBytes());
            return new ServletInputStream() {
                @Override
                public int read() throws IOException {
                    return bain.read();
                }

                @Override
                public boolean isFinished() {
                    return false;
                }

                @Override
                public boolean isReady() {
                    return false;
                }

                @Override
                public void setReadListener(ReadListener listener) {
                }
            };
        }
    }
}
