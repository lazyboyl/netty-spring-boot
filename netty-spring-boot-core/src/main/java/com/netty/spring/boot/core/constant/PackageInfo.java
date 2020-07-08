package com.netty.spring.boot.core.constant;

/**
 * 类描述： 包的路径信息
 *
 * @author linzef
 * @since 2020-07-08
 */
public enum PackageInfo {

    NettyControllerAwareAwareLevel("com.netty.spring.boot.core.aware.NettyControllerAware.awareLevel");

    private String path;

    PackageInfo(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
