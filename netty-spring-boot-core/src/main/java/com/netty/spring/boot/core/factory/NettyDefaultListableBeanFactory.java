package com.netty.spring.boot.core.factory;

import com.netty.spring.boot.core.annotation.NettyRequestMapping;
import com.netty.spring.boot.core.annotation.NettyRequestMethod;
import com.netty.spring.boot.core.beans.NettyBeanDefinition;
import com.netty.spring.boot.core.beans.NettyFieldDefinition;
import com.netty.spring.boot.core.beans.NettyMethodDefinition;
import com.netty.spring.boot.core.factory.impl.DefaultNettySingletonBeanRegistry;
import com.netty.spring.boot.core.util.SpringConfigTool;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
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
     * 定义当前已经初始化的bean的名称
     */
    private final Set<String> nettyBeanDefinitionSet = new LinkedHashSet<>();

    /**
     * 定义当前已经初始化的接口的名称
     */
    private final Set<String> nettyMethodDefinitionSet = new LinkedHashSet<>();

    /**
     * 定义当前已经初始化的类的属性使用类的全称加上类的属性名称
     */
    private final Set<String> nettyFieldDefinitionSet = new LinkedHashSet<>();

    /**
     * 功能描述： 根据bean的名称来获取bean信息
     *
     * @param name bean的全称
     * @return
     */
    public NettyBeanDefinition getNettyBeanDefinition(String name) {
        return nettyBeanDefinitionMap.get(name);
    }

    /**
     * 功能描述： 根据uri来获取响应的method
     *
     * @param uri 响应地址
     * @return
     */
    public NettyMethodDefinition getNettyMethodDefinition(String uri) {
        return nettyMethodDefinitionMap.get(uri);
    }

    /**
     * 功能描述：将class的注册到系统中
     *
     * @param c 类的路径
     */
    public void registerBean(Class c) throws InstantiationException, IllegalAccessException {
        if (!beanIsInit(c.getName())) {
            registerNettyBeanDefinition(c);
        }
    }

    /**
     * 功能描述： 注册netty的bean的类的扫描
     *
     * @param c 需要进行处理的类的对象
     */
    protected void registerNettyBeanDefinition(Class c) throws IllegalAccessException, InstantiationException {
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
        registerNettyFieldDefinition(c, o, nettyBeanDefinition);
        // 实例化类
        nettyBeanDefinition.setObject(o);
        nettyBeanDefinitionSet.add(c.getName());
        nettyBeanDefinitionMap.put(c.getName(), nettyBeanDefinition);
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
                    nettyMethodDefinitionSet.add(s + p);
                    nettyMethodDefinitionMap.put(s + p, nettyMethodDefinition);
                }
            }
        } else {
            for (String p : actionPath) {
                if (methodIsInit(p)) {
                    throw new RuntimeException(c.getName() + "类的" + nettyMethodDefinition.getMethod().getName() + "方法上存在重复定义的响应地址！");
                }
                nettyMethodDefinitionSet.add(p);
                nettyMethodDefinitionMap.put(p, nettyMethodDefinition);
            }
        }
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
        String name = "";
        for (Field f : sf) {
            name = f.getName();
            if (fieldIsInit(name)) {
                continue;
            }
            nettyFieldDefinitionSet.add(c.getName() + "." + name);
            NettyFieldDefinition nettyFieldDefinition = new NettyFieldDefinition();
            nettyFieldDefinition.setFieldAnnotation(f.getAnnotations());
            nettyFieldDefinition.setFieldName(name);
            nettyFieldDefinition.setFieldType(f.getType());
            nettyFieldDefinition.setF(f);
            fieldMap.put(f.getName(), nettyFieldDefinition);
            Annotation a = f.getAnnotation(Autowired.class);
            if (a == null) {
                continue;
            }
            Object fieldObject = SpringConfigTool.getBean(name);
            if (fieldObject != null) {
                f.setAccessible(true);
                f.set(o, fieldObject);
            }
        }
        nettyBeanDefinition.setFieldMap(fieldMap);
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

    /**
     * 功能描述： 判断当前的bean是否已经初始化过
     *
     * @param name bean的名称
     * @return true:已经存在；false：不存在
     */
    protected Boolean beanIsInit(String name) {
        return nettyBeanDefinitionSet.contains(name);
    }

    /**
     * 功能描述： 判断当前响应的方法是否已经存在
     *
     * @param url 响应的url
     * @return true:已经存在；false：不存在
     */
    protected Boolean methodIsInit(String url) {
        return nettyMethodDefinitionSet.contains(url);
    }

    /**
     * 功能描述： 判断当前的属性是否已经初始化过了
     *
     * @param fieldName 属性名称
     * @return true:已经初始化过；false：未初始化
     */
    protected Boolean fieldIsInit(String fieldName) {
        return nettyFieldDefinitionSet.contains(fieldName);
    }


}
