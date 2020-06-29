package com.netty.spring.boot.core.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author linzf
 * @since 2020/6/19
 * 类描述：
 */
@Component
public class SpringConfigTool implements ApplicationContextAware {// extends ApplicationObjectSupport{

    private static ApplicationContext ac = null;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext)throws BeansException {
        ac = applicationContext;
    }

    public static Object getBean(String beanName) {
        return ac.getBean(beanName);
    }
}
