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

    /**
     * 当前方法所处类的bean的名称
     */
    private String beanName;


    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public List<Annotation> getMethodAnnotation() {
        return methodAnnotation;
    }

    public void setMethodAnnotation(List<Annotation> methodAnnotation) {
        this.methodAnnotation = methodAnnotation;
    }

    public Class[] getParameterTypesClass() {
        return parameterTypesClass;
    }

    public void setParameterTypesClass(Class[] parameterTypesClass) {
        this.parameterTypesClass = parameterTypesClass;
    }

    public Class getReturnClass() {
        return returnClass;
    }

    public void setReturnClass(Class returnClass) {
        this.returnClass = returnClass;
    }
}
