package com.netty.spring.boot.core.server;

import com.netty.spring.boot.core.annotation.NettyController;
import com.netty.spring.boot.core.factory.NettyDefaultListableBeanFactory;
import com.netty.spring.boot.core.util.NettyScanner;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.logging.LogLevel;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author linzf
 * @since 2020/6/30
 * 类描述：
 */
@Component
public class NettyServer {

    @Value("${netty.scan.package}")
    private String[] nettyScanPackage;

    public static NettyDefaultListableBeanFactory nettyDefaultListableBeanFactory;

    @PostConstruct
    public void start() throws Exception {
        initConfig();
        ServerBootstrap bootstrap = new ServerBootstrap();
        EventLoopGroup boss = new NioEventLoopGroup(50);
        EventLoopGroup work = new NioEventLoopGroup(100);
        bootstrap.group(boss, work)
                .handler(new LoggingHandler(String.valueOf(LogLevel.DEBUG)))
                .channel(NioServerSocketChannel.class)
                .childHandler(new HttpServerInitializer());

        ChannelFuture f = bootstrap.bind(new InetSocketAddress(8099)).sync();
        System.out.println(" server start up on port : " + 8099);
        f.channel().closeFuture().sync();
    }

    /**
     * 功能描述： 初始化bean的配置
     */
    protected void initConfig() throws Exception {
        System.out.println("-----" + nettyScanPackage.length);
        NettyScanner nettyScanner = new NettyScanner();
        Set<Class<?>> classes = new LinkedHashSet();
        for (String pack : nettyScanPackage) {
            classes.addAll(nettyScanner.getAnnotationClasses(pack, NettyController.class));
        }
        nettyDefaultListableBeanFactory = new NettyDefaultListableBeanFactory();
        for(Class c:classes){
            nettyDefaultListableBeanFactory.registerBean(c);
        }
        System.out.println("-------------");
    }

}
