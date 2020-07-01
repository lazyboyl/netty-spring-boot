package com.netty.spring.boot.core.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;


/**
 * @author linzf
 * @since 2020/6/18
 * 类描述：
 */
public class HttpServerInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        EventExecutorGroup executors = new DefaultEventExecutorGroup(1);
        ChannelPipeline p = channel.pipeline();
        //HTTP 服务的解码器
        p.addLast(new HttpServerCodec(4096, 8192, 1024 * 1024 * 10));
        // 用于上传文件
        channel.pipeline().addLast(executors, new HttUploadHandler());
        //HTTP 消息的合并处理
        p.addLast(new HttpObjectAggregator(10 * 1024));
        // 新增ChunkedHandler，主要作用是支持异步发送大的码流（例如大文件传输），但是不占用过多的内存，防止发生java内存溢出错误
        channel.pipeline().addLast(new ChunkedWriteHandler());
        // http请求处理器
        p.addLast(new HttpRequestHandler());
    }
}