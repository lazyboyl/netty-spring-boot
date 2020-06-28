package com.netty.spring.boot.core.beans;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;

/**
 * @author linzf
 * @since 2020/6/28
 * 类描述： 属性的定义信息
 */
public class NettyFieldDefinition {

    /**
     * 属性对象
     */
    private Field f;

    /**
     * 属性名称
     */
    private String fieldName;

    /**
     * 属性上的注解
     */
    private List<Annotation> fieldAnnotation;

    /**
     * 属性类型
     */
    private String fieldType;

}
