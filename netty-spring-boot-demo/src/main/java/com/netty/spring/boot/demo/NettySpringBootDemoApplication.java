package com.netty.spring.boot.demo;

import com.netty.spring.boot.core.factory.NettyDefaultListableBeanFactory;
import com.netty.spring.boot.core.integrate.EnableNettyServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableNettyServer(nettyScanPackage={"com.netty.spring.boot.demo.controller","com.netty.spring.boot.controller1"})
public class NettySpringBootDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(NettySpringBootDemoApplication.class, args);
        NettyDefaultListableBeanFactory factory = new NettyDefaultListableBeanFactory();
        try {
            factory.registerBean(Class.forName("com.netty.spring.boot.demo.controller.NettyTestController"));
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
