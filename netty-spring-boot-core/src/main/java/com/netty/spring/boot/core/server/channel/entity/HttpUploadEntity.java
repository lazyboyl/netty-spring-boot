package com.netty.spring.boot.core.server.channel.entity;

import io.netty.handler.codec.http.HttpObject;

public class HttpUploadEntity {

    private HttpObject obj;

    public HttpUploadEntity(HttpObject obj){
        this.obj = obj;
    }

    public HttpObject getObj() {
        return obj;
    }

    public void setObj(HttpObject obj) {
        this.obj = obj;
    }
}
