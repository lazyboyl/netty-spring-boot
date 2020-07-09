package com.netty.spring.boot.core.server.channel;

import com.netty.spring.boot.core.server.channel.entity.WebSocketRequestEntity;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.concurrent.GlobalEventExecutor;


/**
 * @author linzf
 * @since 2020/7/9
 * 类描述：
 */
public class WebSocketHandler extends SimpleChannelInboundHandler<WebSocketRequestEntity> {

    public static ChannelGroup channelGroup;
    static {
        channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketRequestEntity webSocketRequestEntity) throws Exception {
        //处理握手
        if(webSocketRequestEntity.getRequest() != null){
            this.handleShake(ctx, webSocketRequestEntity.getRequest());
        }
        //处理websocket数据
        if(webSocketRequestEntity.getFrame() != null){
            this.handleFrame(ctx, webSocketRequestEntity.getFrame());
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("与客户端断开连接，通道关闭！");
        channelGroup.remove(ctx.channel());
    }

    /**
     * 功能描述： 处理websocket数据
     * @param ctx
     * @param frame
     */
    private void handleFrame(ChannelHandlerContext ctx, WebSocketFrame frame){
        // 判断是否ping消息
        if (frame instanceof PingWebSocketFrame) {
            ctx.channel().writeAndFlush(new PongWebSocketFrame(frame.content().retain()));
            return;
        }
        // 暂仅支持文本消息，不支持二进制消息
        if (! (frame instanceof TextWebSocketFrame)) {
            throw new UnsupportedOperationException("暂不支持该消息类型：" + frame.getClass().getName());
        }
        // 处理消息
        String msg = ((TextWebSocketFrame) frame).text();
        System.out.println("服务器收到的数据：" + msg);
    }

    /**
     * 功能描述： 处理握手
     * @param ctx
     * @param request
     */
    private void handleShake(ChannelHandlerContext ctx, FullHttpRequest request){
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(null, null, false);
        WebSocketServerHandshaker handshaker = wsFactory.newHandshaker(request);
        if (handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        } else {
            handshaker.handshake(ctx.channel(), request);
            channelGroup.add(ctx.channel());
            //TODO
            System.out.println("[" + ctx.channel() + "]正在握手。。。");
        }
    }
}
