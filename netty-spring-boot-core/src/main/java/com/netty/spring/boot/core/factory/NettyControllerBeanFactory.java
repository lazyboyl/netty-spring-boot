package com.netty.spring.boot.core.factory;

import com.netty.spring.boot.core.annotation.NettyRequestMapping;
import com.netty.spring.boot.core.annotation.NettyRequestMethod;
import com.netty.spring.boot.core.beans.NettyBeanDefinition;
import com.netty.spring.boot.core.beans.NettyMethodDefinition;
import org.springframework.core.env.Environment;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author linzf
 * @since 2020/7/7
 * 类描述：
 */
public class NettyControllerBeanFactory extends NettyDefaultBeanFactory {

    private NettyControllerBeanFactory(){
        super();
    }

    public static NettyControllerBeanFactory getInstance(){
        return new NettyControllerBeanFactory();
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
        // 注册对象的响应的路径
        registerNettyBeanDefinitionMappingPath(c, nettyBeanDefinition);
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
     * 功能描述： 注册对象的响应的路径
     *
     * @param c                   class对象
     * @param nettyBeanDefinition netty的bean的信息
     */
    protected void registerNettyBeanDefinitionMappingPath(Class c, NettyBeanDefinition nettyBeanDefinition) {
        Annotation a = c.getAnnotation(NettyRequestMapping.class);
        if (a != null) {
            NettyRequestMapping nrm = (NettyRequestMapping) a;
            String[] value = nrm.value();
            String[] path = nrm.path();
            if (value.length > 0 && path.length > 0) {
                throw new RuntimeException(c.getName() + "类上不允许同时存在value和path的定义。");
            }
            if (value.length > 0) {
                nettyBeanDefinition.setMappingPath(value);
            }
            if (path.length > 0) {
                nettyBeanDefinition.setMappingPath(path);
            }
        }
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
            NettyRequestMapping a = m.getAnnotation(NettyRequestMapping.class);
            if (a == null) {
                continue;
            }
            parseNettyRequestMapping(c, nettyMethodDefinition, a, nettyBeanDefinition);
        }
        nettyBeanDefinition.setMethodMap(methodMap);
    }

    /**
     * 功能描述： 解析响应的地址
     *
     * @param c                     class对象
     * @param nettyMethodDefinition
     * @param a
     * @param nettyBeanDefinition
     */
    protected void parseNettyRequestMapping(Class c, NettyMethodDefinition nettyMethodDefinition, NettyRequestMapping a, NettyBeanDefinition nettyBeanDefinition) {
        NettyRequestMethod[] nettyRequestMethods = a.method();
        if (nettyRequestMethods.length > 0) {
            nettyMethodDefinition.setNettyRequestMethod(nettyRequestMethods[0].name());
        } else {
            nettyMethodDefinition.setNettyRequestMethod("");
        }
        String[] value = a.value();
        String[] path = a.path();
        if (value.length > 0 && path.length > 0) {
            throw new RuntimeException(c.getName() + "方法上不允许同时存在value和path的定义。");
        }
        String[] actionPath = new String[0];
        if (value.length > 0) {
            actionPath = value;
        }
        if (path.length > 0) {
            actionPath = path;
        }
        parseUrl(actionPath);
        String[] mappingPath = nettyBeanDefinition.getMappingPath();
        if (mappingPath !=null && mappingPath.length > 0) {
            parseUrl(mappingPath);
            for (String s : mappingPath) {
                for (String p : actionPath) {
                    if (methodIsInit(s + p)) {
                        throw new RuntimeException(c.getName() + "类的" + nettyMethodDefinition.getMethod().getName() + "方法上存在重复定义的响应地址！");
                    }
                    nettyMethodDefinitionSetAdd(s + p);
                    nettyMethodDefinitionMapPut(s + p, nettyMethodDefinition);
                }
            }
        } else {
            for (String p : actionPath) {
                if (methodIsInit(p)) {
                    throw new RuntimeException(c.getName() + "类的" + nettyMethodDefinition.getMethod().getName() + "方法上存在重复定义的响应地址！");
                }
                nettyMethodDefinitionSetAdd(p);
                nettyMethodDefinitionMapPut(p, nettyMethodDefinition);
            }
        }
    }

    /**
     * 功能描述： 重新处理响应的url地址，例如 /dict/add处理成为 dict/add
     *
     * @param url
     */
    protected void parseUrl(String[] url) {
        for (int i = 0; i < url.length; i++) {
            if (url[i].substring(0, 1).equals("/")) {
                url[i] = url[i].substring(1);
            }
            if (!url[i].substring(url[i].length() - 1).equals("/")) {
                url[i] = url[i] + "/";
            }
        }
    }

}
