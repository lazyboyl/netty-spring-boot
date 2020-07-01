package com.netty.spring.boot.core.server.entity;

import java.io.Serializable;

/**
 * @author linzf
 * @since 2020/7/1
 * 类描述：
 */
public class NettyGeneralResponse implements Serializable {

    private static final long serialVersionUID = -1L;

    public NettyGeneralResponse() {
        super();
    }

    public NettyGeneralResponse(Integer code, String msg) {
        this.msg = msg;
        this.code = code;
    }


    /**
     * 返回的错误码
     */
    private Integer code;

    /**
     * 返回的错误信息
     */
    private String msg;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
