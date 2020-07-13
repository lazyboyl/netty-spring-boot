package com.netty.spring.boot.core.server.channel;

import com.netty.spring.boot.core.beans.NettyBeanDefinition;
import com.netty.spring.boot.core.beans.NettyMethodDefinition;
import com.netty.spring.boot.core.server.NettyServer;
import com.netty.spring.boot.core.server.entity.NettyFile;
import com.netty.spring.boot.core.server.entity.NettyGeneralResponse;
import com.netty.spring.boot.core.util.JsonUtils;
import com.netty.spring.boot.core.util.NettyResponseUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.*;
import io.netty.util.ReferenceCountUtil;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author linzf
 * @since 2020/7/1
 * 类描述：
 */
public class HttUploadHandler extends SimpleChannelInboundHandler<HttpObject> {

    public HttUploadHandler() {
        super(false);
    }

    private static final HttpDataFactory factory = new DefaultHttpDataFactory(true);
    private HttpPostRequestDecoder httpDecoder;
    private HttpRequest request;
    private NettyMethodDefinition nettyMethodDefinition;
    private NettyBeanDefinition nettyBeanDefinition;
    private HttpHeaders headers;

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final HttpObject httpObject){
        if (httpObject instanceof HttpRequest) {
            request = (HttpRequest) httpObject;
            // 前置处理
            doNettyUploadBefore(request, ctx);
            if (nettyMethodDefinition != null && nettyBeanDefinition != null) {
                httpDecoder = new HttpPostRequestDecoder(factory, request);
                httpDecoder.setDiscardThreshold(0);
            } else {
                // 交给下一个channel来处理
                ctx.fireChannelRead(httpObject);
            }
        }
        if (httpObject instanceof HttpContent) {
            if (httpDecoder != null) {
                final HttpContent chunk = (HttpContent) httpObject;
                httpDecoder.offer(chunk);
                if (chunk instanceof LastHttpContent) {
                    writeChunk(ctx);
                    //关闭httpDecoder
                    httpDecoder.destroy();
                    httpDecoder = null;
                }
                // 说明数据还没有下载完成，因此需要再次释放当前通道下载数据
                ReferenceCountUtil.release(httpObject);
            } else {
                ctx.fireChannelRead(httpObject);
            }
        }

    }

    /**
     * 功能描述： 调用上传文件接口的时候的前置处理
     *
     * @param httpObject
     * @param ctx
     */
    protected void doNettyUploadBefore(HttpObject httpObject, ChannelHandlerContext ctx) {
        request = (HttpRequest) httpObject;
        headers = request.headers();
        String uri = request.uri().split("\\?")[0];
        // 根据uri获取相应的method的对象
        nettyMethodDefinition = NettyServer.nettyControllerBeanFactory.getNettyMethodDefinition(uri.substring(1) + "/");
        if (nettyMethodDefinition == null) {
            NettyResponseUtil.write(ctx, new NettyGeneralResponse(HttpResponseStatus.NOT_FOUND.code(), "无此方法！"), HttpResponseStatus.NOT_FOUND);
        } else {
            if (!"".equals(nettyMethodDefinition.getNettyRequestMethod()) && !request.method().name().equals(nettyMethodDefinition.getNettyRequestMethod())) {
                NettyResponseUtil.write(ctx, new NettyGeneralResponse(HttpResponseStatus.METHOD_NOT_ALLOWED.code(), "请求方式错误！"), HttpResponseStatus.NOT_FOUND);
            } else {
                nettyBeanDefinition = NettyServer.nettyControllerBeanFactory.getNettyBeanDefinition(nettyMethodDefinition.getBeanName());
                if (nettyBeanDefinition == null) {
                    NettyResponseUtil.write(ctx, new NettyGeneralResponse(HttpResponseStatus.NOT_FOUND.code(), "无此方法！"), HttpResponseStatus.NOT_FOUND);
                }
            }
        }
        Boolean isUpload = false;
        for(Class p : nettyMethodDefinition.getParameterTypesClass()){
            if(p.getName().equals(NettyFile.class.getName())){
                isUpload = true;
            }
        }
        if(!isUpload){
            nettyMethodDefinition = null;
            nettyBeanDefinition = null;
        }
    }

    /**
     * 功能描述： 当文件上传成功以后响应当前的方法
     *
     * @param ctx
     * @throws IOException
     */
    private void writeChunk(ChannelHandlerContext ctx)  {
        FileUpload fileUpload = null;
        if (httpDecoder.hasNext()) {
            InterfaceHttpData data = httpDecoder.next();
            if (data != null && InterfaceHttpData.HttpDataType.FileUpload.equals(data.getHttpDataType())) {
                fileUpload = (FileUpload) data;
            }
        }
        if (nettyMethodDefinition != null && nettyBeanDefinition != null) {
            Parameter[] ps = nettyMethodDefinition.getParameters();
            Class[] parameterTypesClass = nettyMethodDefinition.getParameterTypesClass();
            Object[] obj = new Object[ps.length];
            NettyFile nettyFile = new NettyFile(fileUpload, headers);
            Map<String, Object> paramMap = new HashMap<>();
            headers.entries().forEach(e -> {
                paramMap.put(e.getKey(),e.getValue());
            });
            for (int i = 0; i < ps.length; i++) {
                Parameter p = ps[i];
                if (parameterTypesClass[i].equals(NettyFile.class)) {
                    obj[i] = nettyFile;
                } else {
                    if (isMyClass(parameterTypesClass[i])) {
                        obj[i] = paramMap.get(p.getName());
                    } else {
                        obj[i] = JsonUtils.map2object(paramMap, parameterTypesClass[i]);
                    }
                }
            }
            Object object = null;
            try {
                object = nettyMethodDefinition.getMethod().invoke(nettyBeanDefinition.getObject(), obj);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            NettyResponseUtil.write(ctx, object, HttpResponseStatus.OK);
        } else {
            NettyResponseUtil.write(ctx, new NettyGeneralResponse(HttpResponseStatus.NOT_FOUND.code(), "无此方法！"), HttpResponseStatus.NOT_FOUND);
        }

    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.channel().close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (httpDecoder != null) {
            httpDecoder.cleanFiles();
        }
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
