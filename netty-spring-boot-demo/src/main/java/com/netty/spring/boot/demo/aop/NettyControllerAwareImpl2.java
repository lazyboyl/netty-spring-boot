package com.netty.spring.boot.demo.aop;

import com.netty.spring.boot.core.aware.NettyControllerAware;
import com.netty.spring.boot.demo.service.NettyService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * @author linzf
 * @since 2020/7/8
 * 类描述：
 */
public class NettyControllerAwareImpl2 implements NettyControllerAware {

    @Autowired
    private NettyService nettyService;

    @Override
    public int awareLevel() {
        return 8;
    }

    @Override
    public Boolean beforeAction(ChannelHandlerContext ctx, Map<String, Object> paramMap, HttpHeaders header) {
        System.out.println("beforeAction==>");
        nettyService.testAbc();
        return true;
    }

    @Override
    public Boolean afterAction(ChannelHandlerContext ctx, Object obj, HttpHeaders header) {
        System.out.println("afterAction==>" + obj);
        nettyService.testAbc();
        return true;
    }
}
