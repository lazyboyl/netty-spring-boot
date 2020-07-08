package com.netty.spring.boot.demo.aop;

import com.netty.spring.boot.core.aware.NettyControllerAware;
import io.netty.channel.ChannelHandlerContext;

import java.util.Map;

/**
 * @author linzf
 * @since 2020/7/8
 * 类描述：
 */
public class NettyControllerAwareImpl3 implements NettyControllerAware {


    @Override
    public int awareLevel() {
        return 10;
    }

    @Override
    public Boolean beforeAction(ChannelHandlerContext ctx, Map<String, Object> paramMap) {
        System.out.println("beforeAction==>");
        return true;
    }

    @Override
    public Boolean afterAction(ChannelHandlerContext ctx, Object obj) {
        System.out.println("afterAction==>");
        return true;
    }
}
