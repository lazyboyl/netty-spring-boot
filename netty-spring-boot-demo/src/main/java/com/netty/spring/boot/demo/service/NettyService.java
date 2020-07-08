package com.netty.spring.boot.demo.service;

import org.springframework.stereotype.Service;

/**
 * @author linzf
 * @since 2020/7/8
 * 类描述：
 */
@Service
public class NettyService {

    public String testAbc(){
        System.out.println("------testAbc-------");
        return "12121";
    }

}
