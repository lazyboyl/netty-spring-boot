package com.netty.spring.boot.websocket.core.server;

import com.netty.spring.boot.websocket.core.server.channel.DispatchHandler;
import com.netty.spring.boot.websocket.core.server.channel.WebSocketHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * @author linzf
 * @since 2020/7/10
 * 类描述： websocket的服务初始化
 */
public class websocketServerInitializer extends ChannelInitializer<Channel> {

    @Override
    protected void initChannel(Channel channel) throws Exception {
        ChannelPipeline p = channel.pipeline();
        p.addLast("compressor", new HttpContentCompressor());
        //HTTP 服务的解码器
        p.addLast(new HttpServerCodec(4096, 8192, 1024 * 1024 * 10));
        //HTTP 消息的合并处理
        p.addLast(new HttpObjectAggregator(10 * 1024));
        // 新增ChunkedHandler，主要作用是支持异步发送大的码流（例如大文件传输），但是不占用过多的内存，防止发生java内存溢出错误
        p.addLast(new ChunkedWriteHandler());
        p.addLast(new DispatchHandler());
        p.addLast(new WebSocketHandler());
    }
}
