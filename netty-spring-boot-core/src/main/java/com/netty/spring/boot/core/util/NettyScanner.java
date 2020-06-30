package com.netty.spring.boot.core.util;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

/**
 * @author linzf
 * @since 2020/6/30
 * 类描述： 用于扫描netty注解的类
 */
public class NettyScanner {


    /**
     * 功能描述： 找到被某个注解所注解的类
     *
     * @param packageName     需要扫描的包的路径
     * @param annotationClass 注解类
     * @param <A>
     * @return
     * @throws Exception
     */
    public <A extends Annotation> Set<Class<?>> getAnnotationClasses(String packageName, Class<A> annotationClass) throws Exception {
        //找用了annotationClass注解的类
        Set<Class<?>> controllers = new HashSet<>();
        Set<Class<?>> clsList = getClasses(packageName);
        if (clsList != null && clsList.size() > 0) {
            for (Class<?> cls : clsList) {
                if (cls.getAnnotation(annotationClass) != null) {
                    controllers.add(cls);
                }
            }
        }
        return controllers;
    }

    /**
     * 从包package中获取所有的Class
     *
     * @param packageName 需要扫描的包的名称
     * @return 返回扫描成功的类
     */
    protected Set<Class<?>> getClasses(String packageName) {
        Set<Class<?>> classes = new HashSet<>();
        // 获取包的名字 并进行替换
        String packageDirName = packageName.replace('.', '/');
        // 定义一个枚举的集合 并进行循环来处理这个目录下的things
        Enumeration<URL> dirs;
        try {
            dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
            // 循环迭代下去
            while (dirs.hasMoreElements()) {
                // 获取下一个元素
                URL url = dirs.nextElement();
                // 得到协议的名称
                String protocol = url.getProtocol();
                // 如果是以文件的形式保存在服务器上
                if ("file".equals(protocol)) {
                    // 获取包的物理路径
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                    addClass(classes, filePath, packageName);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return classes;
    }

    protected void addClass(Set<Class<?>> classes, String filePath, String packageName) throws Exception {
        File[] files = new File(filePath).listFiles();
        for (File f : files) {
            if (f.isDirectory()) {
                String path = f.getPath();
                addClass(classes, f.getPath(), packageName + "." + path.split("\\\\")[path.split("\\\\").length - 1]);
            } else if (f.getName().endsWith(".class")) {
                String fileName = f.getName();
                if (f.isFile()) {
                    String classsName = fileName.substring(0, fileName.lastIndexOf("."));
                    if (!packageName.isEmpty()) {
                        classsName = packageName + "." + classsName;
                    }
                    doAddClass(classes, classsName);
                }
            }
        }
    }

    protected void doAddClass(Set<Class<?>> classes, final String classsName) throws Exception {
        ClassLoader classLoader = new ClassLoader() {
            @Override
            public Class<?> loadClass(String name) throws ClassNotFoundException {
                return super.loadClass(name);
            }
        };
        classes.add(classLoader.loadClass(classsName));
    }


}
