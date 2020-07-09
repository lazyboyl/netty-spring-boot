package com.netty.spring.boot.core.server;

import com.netty.spring.boot.core.annotation.NettyController;
import com.netty.spring.boot.core.aware.NettyControllerAware;
import com.netty.spring.boot.core.factory.NettyControllerAwareBeanFactory;
import com.netty.spring.boot.core.factory.NettyControllerBeanFactory;
import com.netty.spring.boot.core.factory.NettyDefaultBeanFactory;
import com.netty.spring.boot.core.util.NettyScanner;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.logging.LogLevel;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;

/**
 * @author linzf
 * @since 2020/6/30
 * 类描述：
 */
@Component
public class NettyServer implements ApplicationContextAware {

    @Value("${netty.scan.package}")
    private String[] nettyScanPackage;

    @Autowired
    private Environment environment;

    public static NettyControllerBeanFactory nettyControllerBeanFactory;

    public static NettyControllerAwareBeanFactory nettyControllerAwareBeanFactory;

    private static ApplicationContext ac = null;

    @PostConstruct
    public void start() throws Exception {
        initConfig();
        ServerBootstrap bootstrap = new ServerBootstrap();
        EventLoopGroup boss = new NioEventLoopGroup(50);
        EventLoopGroup work = new NioEventLoopGroup(100);
        bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
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
        for (String pack : nettyScanPackage) {
            nettyScanner.initClasses(pack);
        }
        // 初始化NettyController的扫描和注入
        nettyControllerBeanFactory = NettyControllerBeanFactory.getInstance();
        injectionBean(NettyController.class, nettyControllerBeanFactory, nettyScanner);
        // 初始化NettyControllerAware的扫描和注入
        nettyControllerAwareBeanFactory = NettyControllerAwareBeanFactory.getInstance();
        injectionBean(NettyControllerAware.class, nettyControllerAwareBeanFactory, nettyScanner);
        System.out.println("-------------");
    }

    /**
     * 功能描述： 实现相应的bean的注入
     *
     * @param cls          待注入的类型的class
     * @param factory      相应的工程
     * @param nettyScanner 扫描对象
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    protected void injectionBean(Class cls, NettyDefaultBeanFactory factory, NettyScanner nettyScanner) throws Exception {
        if (cls.isAnnotation()) {
            for (Class c : nettyScanner.getAnnotationClasses(cls)) {
                factory.registerBean(c, environment);
            }
        } else if (cls.isInterface()) {
            for (Class c : nettyScanner.getInterfaceClasses(cls)) {
                factory.registerBean(c, environment);
            }
        }

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ac = applicationContext;
    }

    public static Object getBean(String beanName) {
        return ac.getBean(beanName);
    }
}
