package com.netty.spring.boot.core.beans;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author linzf
 * @since 2020/6/28
 * 类描述： 方法的定义信息
 */
public class NettyMethodDefinition {

    /**
     * 方法对象
     */
    private Method method;

    /**
     * 方法名称
     */
    private String methodName;

    /**
     * 方法上的注解
     */
     private List<Annotation> methodAnnotation;

    /**
     * 方法上的请求的入参
     */
    private Class[] parameterTypesClass;

    /**
     * 方法调用返回的class
     */
    private Class returnClass;


}
