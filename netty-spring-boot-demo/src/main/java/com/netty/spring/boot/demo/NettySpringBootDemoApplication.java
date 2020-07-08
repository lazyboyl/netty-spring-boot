package com.netty.spring.boot.demo;

import com.netty.spring.boot.core.integrate.EnableNettyServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableNettyServer(nettyScanPackage = {"com.netty.spring.boot.demo", "com.netty.spring.boot.controller1"}, basePackages = "com.netty.spring.boot.core")
public class NettySpringBootDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(NettySpringBootDemoApplication.class, args);
    }

}
