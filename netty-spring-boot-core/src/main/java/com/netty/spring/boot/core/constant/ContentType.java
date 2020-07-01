package com.netty.spring.boot.core.constant;

/**
 * 类描述： 表单提交请求的类型
 *
 * @author linzef
 * @since 2020-07-01
 */
public enum ContentType {

    JSON("application/json"),
    FILE("multipart/form-data");

    private String type;

    ContentType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
