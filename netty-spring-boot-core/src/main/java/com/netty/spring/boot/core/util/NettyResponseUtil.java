package com.netty.spring.boot.core.util;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

/**
 * @author linzf
 * @since 2020/7/1
 * 类描述： 前后端数据交互工具类
 */
public class NettyResponseUtil {

    /**
     * 功能描述： 实现将JSON数据返回给到前端
     *
     * @param ctx    netty的ChannelHandlerContext对象
     * @param object 需要返回给到前端的对象
     */
    public static void write(ChannelHandlerContext ctx, Object object,HttpResponseStatus status) {
        // 创建http响应
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                status,
                Unpooled.copiedBuffer(JsonUtils.objToJson(object), CharsetUtil.UTF_8));
        // 设置头信息
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json; charset=UTF-8");
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

}
