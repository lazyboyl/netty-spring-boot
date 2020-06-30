package com.netty.spring.boot.core.integrate;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author linzef
 * @since 2020-03-16
 * 注解描述： 实现开启netty服务
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import({NettyScannerRegister.class})
public @interface EnableNettyServer {

    /**
     * 功能描述： 需要进行spring扫描的目录
     *
     * @return
     */
    String[] basePackages() default {"com.netty.spring.boot.core"};

    /**
     * 当前需要进行netty扫描的目录
     *
     * @return
     */
    String[] nettyScanPackage() default {"com.netty.spring.boot.core"};

}
