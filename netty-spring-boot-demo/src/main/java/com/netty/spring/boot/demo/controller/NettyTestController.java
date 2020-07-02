package com.netty.spring.boot.demo.controller;

import com.netty.spring.boot.core.annotation.NettyController;
import com.netty.spring.boot.core.annotation.NettyRequestMapping;
import com.netty.spring.boot.core.annotation.NettyRequestMethod;
import com.netty.spring.boot.demo.entity.UserInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * @author linzf
 * @since 2020/6/30
 * 类描述：
 */
@NettyController
@NettyRequestMapping("nettyTest")
public class NettyTestController {

    @NettyRequestMapping(path = "nTest",  method = NettyRequestMethod.GET)
    public String nTest() {
        System.out.println("----");
        return "adasdasd";
    }

    @NettyRequestMapping(path = "nTest1",  method = NettyRequestMethod.GET)
    public Map<String,Object> nTest1() {
        System.out.println("----");
        Map<String,Object> m = new HashMap<>();
        m.put("asasa","dsad");
        m.put("asasa","啥的范德萨");
        m.put("ass啊啊啊","阿达");
        return m;
    }

    @NettyRequestMapping(path = "nTest2",  method = NettyRequestMethod.GET)
    public Map<String,Object> nTest2(String abc) throws InterruptedException {
        Thread.sleep(5000);
        System.out.println("----" + abc);
        Map<String,Object> m = new HashMap<>();
        m.put("asasa",abc);
        return m;
    }

    @NettyRequestMapping(path = "nTest3",  method = NettyRequestMethod.POST)
    public Map<String,Object> nTest3(String abc) {
        System.out.println("----" + abc);
        Map<String,Object> m = new HashMap<>();
        m.put("asasa",abc);
        return m;
    }

    @NettyRequestMapping(path = "nTest4",  method = NettyRequestMethod.POST)
    public UserInfo nTest4(UserInfo userInfo) {
        System.out.println("----" + userInfo);
        return userInfo;
    }

}
