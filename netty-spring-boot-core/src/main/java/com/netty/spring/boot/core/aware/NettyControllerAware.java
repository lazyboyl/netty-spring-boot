package com.netty.spring.boot.core.aware;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpHeaders;

import java.util.Map;

/**
 * @author linzf
 * @since 2020/7/7
 * 类描述： controller方法的aop的实现
 */
public interface NettyControllerAware {

    /**
     * 当前执行的级别
     * @return 返回当前的级别，数字越高代表拦截级别越高
     */
    int awareLevel();

    /**
     * 功能描述： 响应某个方法的时候的前置调用
     * @param ctx
     * @param paramMap
     * @param header
     * @return true：表示继续执行；false：表示直接结束
     */
    Boolean beforeAction(ChannelHandlerContext ctx, Map<String, Object> paramMap, HttpHeaders header);

    /**
     * 功能描述： 响应某个方法的时候的后置调用
     * @param ctx
     * @param object
     * @param header
     * @return true：表示继续执行；false：表示直接结束
     */
    Boolean afterAction(ChannelHandlerContext ctx, Object object, HttpHeaders header);

}
