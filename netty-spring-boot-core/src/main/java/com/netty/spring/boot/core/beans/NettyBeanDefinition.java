package com.netty.spring.boot.core.beans;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;

/**
 * @author linzf
 * @since 2020/6/28
 * 类描述：
 */
public class NettyBeanDefinition {

    /**
     * 当前实例化的对象
     */
    private Object object;

    /**
     * 类的全名
     */
    private String className;

    /**
     * 类上的注解
     */
    private List<Annotation> classAnnotation;

    /**
     * 方法名称作为key，方法的信息作为value进行保存
     */
    private Map<String,NettyMethodDefinition> methodMap;

}
