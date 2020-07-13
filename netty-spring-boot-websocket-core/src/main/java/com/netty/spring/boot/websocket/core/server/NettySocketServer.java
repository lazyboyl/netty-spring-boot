package com.netty.spring.boot.websocket.core.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.springframework.beans.BeansException;
import org.springframework.boot.logging.LogLevel;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;

/**
 * @author linzf
 * @since 2020/7/10
 * 类描述： websocket的启动类
 */
@Component
public class NettySocketServer implements ApplicationContextAware {

    private static ApplicationContext ac = null;

    /**
     * 存放所有的socketId的集合
     */
    public static ChannelGroup channelGroup = null;

    @PostConstruct
    public void start() throws InterruptedException {
        // 初始化配置
        initWebSocketConfig();
        ServerBootstrap bootstrap = new ServerBootstrap();
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup work = new NioEventLoopGroup();
        bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
        bootstrap.group(boss, work)
                .handler(new LoggingHandler(String.valueOf(LogLevel.DEBUG)))
                .channel(NioServerSocketChannel.class)
                .childHandler(new websocketServerInitializer());

        ChannelFuture f = bootstrap.bind(new InetSocketAddress(8399)).sync();
        f.addListener(future -> {
            if (future.isSuccess()) {
                System.out.println("服务启动成功");
            } else {
                System.out.println("服务启动失败");
            }
        });
        f.channel().closeFuture().sync();
    }

    /**
     * 功能描述： 初始化bean的配置
     */
    protected void initWebSocketConfig(){
        channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    }

    /**
     * 功能描述： 根据bean的名称来获取相应的bean
     *
     * @param beanName bean的名称
     * @return 返回相应的实例化的bean
     */
    public static Object getBean(String beanName) {
        return ac.getBean(beanName);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ac = applicationContext;
    }

}
