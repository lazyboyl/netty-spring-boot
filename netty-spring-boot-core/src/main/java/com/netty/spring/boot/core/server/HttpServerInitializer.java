package com.netty.spring.boot.core.server;

import com.netty.spring.boot.core.server.channel.DispatchHandler;
import com.netty.spring.boot.core.server.channel.WebSocketHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;

import java.util.Map;


/**
 * @author linzf
 * @since 2020/6/18
 * 类描述：
 */
public class HttpServerInitializer extends ChannelInitializer<Channel> {

    @Override
    protected void initChannel(Channel channel) throws Exception {
        ChannelPipeline p = channel.pipeline();
        p.addLast("compressor", new HttpContentCompressor());
        //HTTP 服务的解码器
        p.addLast(new HttpServerCodec(4096, 8192, 1024 * 1024 * 10));
        // 用于上传文件
        //HTTP 消息的合并处理
        p.addLast(new HttpObjectAggregator(10 * 1024));
        // 新增ChunkedHandler，主要作用是支持异步发送大的码流（例如大文件传输），但是不占用过多的内存，防止发生java内存溢出错误
        p.addLast(new ChunkedWriteHandler());
        //自定义Handler
        channel.pipeline().addLast("dispatchHandler", new DispatchHandler());
        channel.pipeline().addLast("httpRequestHandler", new HttpRequestHandler());
//        channel.pipeline().addLast("httUploadHandler", new HttUploadHandler());
        channel.pipeline().addLast("webSocketHandler", new WebSocketHandler());
    }
}