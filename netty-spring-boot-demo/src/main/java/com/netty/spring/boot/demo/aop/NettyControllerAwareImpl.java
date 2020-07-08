package com.netty.spring.boot.demo.aop;

import com.netty.spring.boot.core.aware.NettyControllerAware;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
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

    @Value("${te.abcDouble}")
    private Double abcDouble;

    @Value("${te.abcFloat}")
    private Float abcFloat;

    @Value("${te.abcBoolean}")
    private Boolean abcBoolean;

    @Value("${te.abcint}")
    private Integer abcint;

    @Value("${te.abclong}")
    private Long abclong;

    @Value("${te.abcdouble}")
    private Double abcdouble;

    @Value("${te.abcfloat}")
    private Float abcfloat;

    @Value("${te.abcboolean}")
    private Boolean abcboolean;

    @Value("${maps}")
    private Map<String,String> maps;

    @Value("${strList}")
    private List<String> strList;



    @Override
    public int awareLevel() {
        return 9;
    }

    @Override
    public Boolean beforeAction(ChannelHandlerContext ctx, Map<String, Object> paramMap, HttpHeaders header) {
        System.out.println("beforeAction==>");
        return true;
    }

    @Override
    public Boolean afterAction(ChannelHandlerContext ctx, Object obj, HttpHeaders header) {
        System.out.println("afterAction==>");
        return true;
    }
}
