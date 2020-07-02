package com.netty.spring.boot.core.server.entity;

import com.netty.spring.boot.core.constant.ContentType;
import com.netty.spring.boot.core.util.JsonUtils;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author linzf
 * @since 2020/6/30
 * 类描述：
 */
public class RequestParser {

    private FullHttpRequest fullReq;

    /**
     * 构造一个解析器
     *
     * @param req
     */
    public RequestParser(FullHttpRequest req) {
        this.fullReq = req;
    }

    /**
     * 解析请求参数
     *
     * @return 包含所有请求参数的键值对, 如果没有参数, 则返回空Map
     * @throws IOException
     */
    public Map<String, Object> parse() throws IOException {
        HttpMethod method = fullReq.method();
        final Map<String, Object> parmMap = new HashMap<>();
        // 获取请求的类型是form表单提交还是JSON提交
        String contentType = fullReq.headers().get("Content-Type");
        if (HttpMethod.GET == method) {
            QueryStringDecoder decoder = new QueryStringDecoder(fullReq.uri());
            decoder.parameters().entrySet().forEach(entry -> {
                // entry.getValue()是一个List, 只取第一个元素
                parmMap.put(entry.getKey(), entry.getValue().get(0));
            });
        } else {
            if (contentType.indexOf(ContentType.JSON.getType()) != -1) {
                String content = fullReq.content().toString(Charset.forName("UTF-8"));
                return JsonUtils.jsonToMap(content);
            } else {
                HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(fullReq);
//                decoder.offer(fullReq);
                List<InterfaceHttpData> parmList = decoder.getBodyHttpDatas();
                for (InterfaceHttpData parm : parmList) {
                    Attribute data = (Attribute) parm;
                    parmMap.put(data.getName(), data.getValue());
                }
            }
        }
        return parmMap;
    }

}
