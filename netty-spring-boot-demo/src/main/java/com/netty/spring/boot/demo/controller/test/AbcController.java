package com.netty.spring.boot.demo.controller.test;

import com.netty.spring.boot.core.annotation.NettyController;
import com.netty.spring.boot.core.annotation.NettyRequestMapping;
import com.netty.spring.boot.core.annotation.NettyRequestMethod;

/**
 * @author linzf
 * @since 2020/6/30
 * 类描述：
 */
@NettyController
@NettyRequestMapping("abc")
public class AbcController {

    @NettyRequestMapping(path = "nTest",  method = NettyRequestMethod.GET)
    public String nTest() {
        System.out.println("----");
        return "adasdasd";
    }

}
