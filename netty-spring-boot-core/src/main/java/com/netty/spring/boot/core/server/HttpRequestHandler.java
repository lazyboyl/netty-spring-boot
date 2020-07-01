package com.netty.spring.boot.core.server;

import com.netty.spring.boot.core.beans.NettyBeanDefinition;
import com.netty.spring.boot.core.beans.NettyMethodDefinition;
import com.netty.spring.boot.core.beans.RequestParser;
import com.netty.spring.boot.core.server.entity.NettyGeneralResponse;
import com.netty.spring.boot.core.util.JsonUtils;
import com.netty.spring.boot.core.util.NettyResponseUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.Map;

import static io.netty.handler.codec.http.HttpUtil.is100ContinueExpected;

/**
 * @author linzf
 * @since 2020/6/18
 * 类描述： http请求分发处理的类
 */
public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
        //100 Continue
        if (is100ContinueExpected(req)) {
            ctx.write(new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1,
                    HttpResponseStatus.CONTINUE));
        }
        // 获取请求的uri
        String uri = req.uri().split("\\?")[0];
        if (uri.indexOf("favicon.ico") != -1) {
            return;
        }
        doNettyController(ctx, req, uri);
    }

    /**
     * 功能描述： 实现接口的调用
     *
     * @param ctx netty通道镀锡
     * @param req http对象
     * @param uri 请求地址
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws IOException
     */
    protected void doNettyController(ChannelHandlerContext ctx, FullHttpRequest req, String uri) throws InvocationTargetException, IllegalAccessException, IOException {
        Map<String, Object> paramMap = new RequestParser(req).parse();
        // 根据uri获取相应的method的对象
        NettyMethodDefinition nettyMethodDefinition = NettyServer.nettyDefaultListableBeanFactory.getNettyMethodDefinition(uri.substring(1) + "/");
        if (nettyMethodDefinition == null) {
            NettyResponseUtil.write(ctx, new NettyGeneralResponse(HttpResponseStatus.NOT_FOUND.code(), "无此方法！"), HttpResponseStatus.NOT_FOUND);
        } else {
            if (!"".equals(nettyMethodDefinition.getNettyRequestMethod()) && !req.method().name().equals(nettyMethodDefinition.getNettyRequestMethod())) {
                NettyResponseUtil.write(ctx, new NettyGeneralResponse(HttpResponseStatus.METHOD_NOT_ALLOWED.code(), "请求方式错误！"), HttpResponseStatus.NOT_FOUND);
            } else {
                NettyBeanDefinition nettyBeanDefinition = NettyServer.nettyDefaultListableBeanFactory.getNettyBeanDefinition(nettyMethodDefinition.getBeanName());
                if (nettyBeanDefinition == null) {
                    NettyResponseUtil.write(ctx, new NettyGeneralResponse(HttpResponseStatus.NOT_FOUND.code(), "无此方法！"), HttpResponseStatus.NOT_FOUND);
                } else {
                    invokeMethod(ctx, nettyMethodDefinition, nettyBeanDefinition, paramMap);
                }
            }
        }
    }

    /**
     * 功能描述： 实现反射调用方法
     *
     * @param ctx                   netty通道镀锡
     * @param nettyMethodDefinition 方法对象
     * @param nettyBeanDefinition   类对象
     * @param paramMap              请求参数
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    protected void invokeMethod(ChannelHandlerContext ctx, NettyMethodDefinition nettyMethodDefinition, NettyBeanDefinition nettyBeanDefinition, Map<String, Object> paramMap) throws InvocationTargetException, IllegalAccessException {
        Object object;
        if (nettyMethodDefinition.getParameters().length == 0) {
            object = nettyMethodDefinition.getMethod().invoke(nettyBeanDefinition.getObject());
        } else {
            Parameter[] ps = nettyMethodDefinition.getParameters();
            Object[] obj = new Object[ps.length];
            Class[] parameterTypesClass = nettyMethodDefinition.getParameterTypesClass();
            for (int i = 0; i < ps.length; i++) {
                Parameter p = ps[i];
                if (isMyClass(parameterTypesClass[i])) {
                    obj[i] = paramMap.get(p.getName());
                } else {
                    obj[i] = JsonUtils.map2object(paramMap, parameterTypesClass[i]);
                }
            }
            object = nettyMethodDefinition.getMethod().invoke(nettyBeanDefinition.getObject(), obj);
        }
        NettyResponseUtil.write(ctx, object, HttpResponseStatus.OK);
    }


    /**
     * 功能描述： 判断当前的class是否是自己定义的class
     *
     * @param s 需要判断的class对象
     * @return true: JDK本身的类；false：自己定义的类
     */
    protected Boolean isMyClass(Class s) {
        if (s.getClassLoader() == null) {
            return true;
        } else {
            return false;
        }
    }

}
