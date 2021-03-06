package com.netty.spring.boot.core.factory;

import com.netty.spring.boot.core.beans.NettyBeanDefinition;
import com.netty.spring.boot.core.beans.NettyMethodDefinition;
import org.springframework.core.env.Environment;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author linzf
 * @since 2020/7/8
 * 类描述：
 */
public class NettyControllerAwareBeanFactory extends NettyDefaultBeanFactory {

    private NettyControllerAwareBeanFactory() {
        super();
    }

    public static NettyControllerAwareBeanFactory getInstance() {
        return new NettyControllerAwareBeanFactory();
    }

    private List<NettyBeanDefinition> nettyBeanDefinitionList;

    /**
     * 功能描述： 获取排序以后的类
     *
     * @return
     */
    public List<NettyBeanDefinition> getNettyBeanDefinitionList() {
        if (nettyBeanDefinitionList == null) {
            List<NettyBeanDefinition> nettyBeanDefinitions = new ArrayList<>();
            getNettyBeanDefinitionMap().forEach((k, v) -> {
                v.getMethodMap().forEach((k1, v1) -> {
                    String[] k1s = k1.split("\\.");
                    if (k1s[k1s.length - 1].equals("awareLevel")) {
                        try {
                            v.setLevel((Integer) v1.getMethod().invoke(v.getObject()));
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                        nettyBeanDefinitions.add(v);
                    }
                });
            });
            nettyBeanDefinitionList = nettyBeanDefinitions.stream().sorted(Comparator.comparing(NettyBeanDefinition::getLevel)).collect(Collectors.toList());
        }
        return nettyBeanDefinitionList;
    }

    /**
     * 功能描述： 注册netty的bean的类的扫描
     *
     * @param c 需要进行处理的类的对象
     */
    @Override
    protected void registerNettyBeanDefinition(Class c, Environment environment) throws IllegalAccessException, InstantiationException {
        NettyBeanDefinition nettyBeanDefinition = new NettyBeanDefinition();
        nettyBeanDefinition.setClassName(c.getName());
        // 获取类上的注解的集合
        nettyBeanDefinition.setClassAnnotation(c.getAnnotations());
        Object o = c.newInstance();
        // 初始化类上的方法
        registerNettyMethodDefinition(c, nettyBeanDefinition);
        // 初始化类上的属性
        registerNettyFieldDefinition(c, o, nettyBeanDefinition,environment);
        // 实例化类
        nettyBeanDefinition.setObject(o);
        nettyBeanDefinitionSetAdd(c.getName());
        nettyBeanDefinitionMapPut(c.getName(), nettyBeanDefinition);
    }

    /**
     * 功能描述： 注册对象的方法信息
     *
     * @param c                   class对象
     * @param nettyBeanDefinition netty的bean的信息
     */
    protected void registerNettyMethodDefinition(Class c, NettyBeanDefinition nettyBeanDefinition) {
        Method[] methods = c.getDeclaredMethods();
        Map<String, NettyMethodDefinition> methodMap = new HashMap<>(methods.length);
        for (Method m : methods) {
            NettyMethodDefinition nettyMethodDefinition = new NettyMethodDefinition();
            nettyMethodDefinition.setMethod(m);
            nettyMethodDefinition.setMethodAnnotation(m.getAnnotations());
            nettyMethodDefinition.setParameterTypesClass(m.getParameterTypes());
            nettyMethodDefinition.setReturnClass(m.getReturnType());
            nettyMethodDefinition.setMethodName(m.getName());
            nettyMethodDefinition.setBeanName(c.getName());
            nettyMethodDefinition.setParameters(m.getParameters());
            methodMap.put(c.getName() + "." + m.getName(), nettyMethodDefinition);
        }
        nettyBeanDefinition.setMethodMap(methodMap);
    }

}
