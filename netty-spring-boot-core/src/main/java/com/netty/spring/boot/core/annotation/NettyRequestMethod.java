package com.netty.spring.boot.core.annotation;

/**
 * 类描述：netty的http请求的枚举类
 *
 * @author linzef
 * @since 2020-06-30
 */
public enum NettyRequestMethod {

    GET,
    HEAD,
    POST,
    PUT,
    PATCH,
    DELETE,
    OPTIONS,
    TRACE;

    private NettyRequestMethod() {
    }

}
