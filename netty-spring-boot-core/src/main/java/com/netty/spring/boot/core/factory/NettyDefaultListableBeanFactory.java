package com.netty.spring.boot.core.factory;

import com.netty.spring.boot.core.beans.NettyBeanDefinition;
import com.netty.spring.boot.core.beans.NettyFieldDefinition;
import com.netty.spring.boot.core.beans.NettyMethodDefinition;
import com.netty.spring.boot.core.factory.impl.DefaultNettySingletonBeanRegistry;
import com.netty.spring.boot.core.util.SpringConfigTool;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author linzf
 * @since 2020/6/29
 * 类描述：
 */
public class NettyDefaultListableBeanFactory extends DefaultNettySingletonBeanRegistry {

    /**
     * 定义用于存放netty的bean的map集合
     */
    private final Map<String, NettyBeanDefinition> nettyBeanDefinitionMap = new ConcurrentHashMap<>(256);

    /**
     * 定义相应的地址的映射，/dict/addDict 对应相应的类中的相应的方法
     */
    private final Map<String, NettyMethodDefinition> nettyMethodDefinitionMap = new ConcurrentHashMap<>(256);

    /**
     * 功能描述：将class的注册到系统中
     *
     * @param c 类的路径
     */
    protected void registerBean(Class c) {

    }

    /**
     * 功能描述： 注册netty的bean的类的扫描
     *
     * @param c 需要进行处理的类的对象
     */
    protected void registerNettyBeanDefinition(Class c) throws IllegalAccessException, InstantiationException {
        NettyBeanDefinition nettyBeanDefinition = new NettyBeanDefinition();
        // 获取类上的注解的集合
        nettyBeanDefinition.setClassAnnotation(c.getAnnotations());
        Object o = c.newInstance();

        // 实例化类
        nettyBeanDefinition.setObject(o);
    }

    /**
     * 功能描述：注册对象的属性信息
     *
     * @param c                   class对象
     * @param o                   实例化的对象
     * @param nettyBeanDefinition netty的bean的信息
     */
    protected void registerNettyFieldDefinition(Class c, Object o, NettyBeanDefinition nettyBeanDefinition) throws IllegalAccessException {
        Field[] sf = c.getDeclaredFields();
        Map<String, NettyFieldDefinition> fieldMap = new HashMap<>(sf.length);
        for (Field f : sf) {
            NettyFieldDefinition nettyFieldDefinition = new NettyFieldDefinition();
            nettyFieldDefinition.setFieldAnnotation(f.getAnnotations());
            nettyFieldDefinition.setFieldName(f.getName());
            nettyFieldDefinition.setFieldType(f.getType());
            Annotation a = f.getAnnotation(Autowired.class);
            if (a != null) {
                Object fieldObject = SpringConfigTool.getBean(f.getName());
                if (fieldObject != null) {
                    f.setAccessible(true);
                    f.set(o, fieldObject);
                }
            }
            nettyFieldDefinition.setF(f);
            fieldMap.put(f.getName(), nettyFieldDefinition);
        }
        nettyBeanDefinition.setFieldMap(fieldMap);
    }


}
