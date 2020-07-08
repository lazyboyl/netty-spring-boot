package com.netty.spring.boot.demo.aop;

import com.netty.spring.boot.core.aware.NettyControllerAware;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;

/**
 * @author linzf
 * @since 2020/7/8
 * 类描述：
 */
public class NettyControllerAwareImpl implements NettyControllerAware {

    @Value("${te.abc}")
    private String abc;

    @Value("${te.abcInt}")
    private Integer abcInt;

    @Value("${te.abcLong}")
    private Long abcLong;

    @Value("${te.abcBoolean}")
    private Boolean abcBoolean;

    @Override
    public int awareLevel() {
        return 9;
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
