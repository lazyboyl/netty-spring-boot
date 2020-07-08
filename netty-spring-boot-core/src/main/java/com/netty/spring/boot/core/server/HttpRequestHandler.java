package com.netty.spring.boot.core.server;

import com.netty.spring.boot.core.beans.NettyBeanDefinition;
import com.netty.spring.boot.core.beans.NettyMethodDefinition;
import com.netty.spring.boot.core.server.entity.RequestParser;
import com.netty.spring.boot.core.server.entity.NettyGeneralResponse;
import com.netty.spring.boot.core.util.JsonUtils;
import com.netty.spring.boot.core.util.NettyResponseUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.List;
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
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) {
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
    protected void doNettyController(ChannelHandlerContext ctx, FullHttpRequest req, String uri) {
        Map<String, Object> paramMap = null;
        try {
            paramMap = new RequestParser(req).parse();
            List<NettyBeanDefinition> nettyBeanDefinitions = NettyServer.nettyControllerAwareBeanFactory.getNettyBeanDefinitionList();
            if (!doAwareInvoke(nettyBeanDefinitions, ctx, paramMap, "beforeAction")) {
                return;
            }
            paramMap.put("headers", req.headers());
            // 根据uri获取相应的method的对象
            NettyMethodDefinition nettyMethodDefinition = NettyServer.nettyControllerBeanFactory.getNettyMethodDefinition(uri.substring(1) + "/");
            if (nettyMethodDefinition == null) {
                NettyResponseUtil.write(ctx, new NettyGeneralResponse(HttpResponseStatus.NOT_FOUND.code(), "无此方法！"), HttpResponseStatus.NOT_FOUND);
            } else {
                if (!"".equals(nettyMethodDefinition.getNettyRequestMethod()) && !req.method().name().equals(nettyMethodDefinition.getNettyRequestMethod())) {
                    NettyResponseUtil.write(ctx, new NettyGeneralResponse(HttpResponseStatus.METHOD_NOT_ALLOWED.code(), "请求方式错误！"), HttpResponseStatus.NOT_FOUND);
                } else {
                    NettyBeanDefinition nettyBeanDefinition = NettyServer.nettyControllerBeanFactory.getNettyBeanDefinition(nettyMethodDefinition.getBeanName());
                    if (nettyBeanDefinition == null) {
                        NettyResponseUtil.write(ctx, new NettyGeneralResponse(HttpResponseStatus.NOT_FOUND.code(), "无此方法！"), HttpResponseStatus.NOT_FOUND);
                    } else {
                        invokeMethod(ctx, nettyMethodDefinition, nettyBeanDefinition, paramMap,nettyBeanDefinitions);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 功能描述： 前置/后置调用逻辑
     *
     * @param nettyBeanDefinitions 实例化的bean
     * @param ctx                  ctx对象
     * @param object               入参
     * @param doAction             beforeAction：前置调用；afterAction：后置调用
     * @return
     */
    protected Boolean doAwareInvoke(List<NettyBeanDefinition> nettyBeanDefinitions, ChannelHandlerContext ctx, Object object, String doAction) {
        for (NettyBeanDefinition nbd : nettyBeanDefinitions) {
            for (Map.Entry<String, NettyMethodDefinition> entry : nbd.getMethodMap().entrySet()) {
                String[] k1s = entry.getKey().split("\\.");
                Object[] obj = new Object[]{ctx, object};
                if (k1s[k1s.length - 1].equals(doAction)) {
                    try {
                        Boolean isContinue = (Boolean) entry.getValue().getMethod().invoke(nbd.getObject(), obj);
                        if (!isContinue) {
                            return false;
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return true;
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
    protected void invokeMethod(ChannelHandlerContext ctx, NettyMethodDefinition nettyMethodDefinition, NettyBeanDefinition nettyBeanDefinition, Map<String, Object> paramMap, List<NettyBeanDefinition> nettyBeanDefinitions) {
        Object object = null;
        if (nettyMethodDefinition.getParameters().length == 0) {
            try {
                object = nettyMethodDefinition.getMethod().invoke(nettyBeanDefinition.getObject());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } else {
            Parameter[] ps = nettyMethodDefinition.getParameters();
            Object[] obj = new Object[ps.length];
            Class[] parameterTypesClass = nettyMethodDefinition.getParameterTypesClass();
            for (int i = 0; i < ps.length; i++) {
                Parameter p = ps[i];
                if (isMyClass(parameterTypesClass[i])) {
                    obj[i] = paramMap.get(p.getName());
                } else {
                    if (parameterTypesClass[i].getName().equals(HttpHeaders.class.getName())) {
                        obj[i] = paramMap.get("headers");
                    } else {
                        obj[i] = JsonUtils.map2object(paramMap, parameterTypesClass[i]);
                    }
                }
            }
            try {
                object = nettyMethodDefinition.getMethod().invoke(nettyBeanDefinition.getObject(), obj);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        doAwareInvoke(nettyBeanDefinitions, ctx, object, "afterAction");
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
