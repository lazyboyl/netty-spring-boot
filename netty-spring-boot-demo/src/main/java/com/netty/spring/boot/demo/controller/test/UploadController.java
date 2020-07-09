package com.netty.spring.boot.demo.controller.test;

import com.netty.spring.boot.core.annotation.NettyController;
import com.netty.spring.boot.core.annotation.NettyRequestMapping;
import com.netty.spring.boot.core.annotation.NettyRequestMethod;
import com.netty.spring.boot.core.server.entity.NettyFile;

import java.io.IOException;

/**
 * @author linzf
 * @since 2020/7/2
 * 类描述：
 */
@NettyController
public class UploadController {

    @NettyRequestMapping(path = "upload",  method = NettyRequestMethod.POST)
    public String upload(NettyFile nettyFile,String fileName)  {
        System.out.println("----" + fileName);
        try{
            nettyFile.transferTo("f:\\");
        }catch (Exception e){
            e.printStackTrace();
        }
        return "adasdasd";
    }

    @NettyRequestMapping(path = "upload1",  method = NettyRequestMethod.POST)
    public String upload1(NettyFile nettyFile,String token)  {
        System.out.println("----" + token);
        try{
            nettyFile.transferTo("f:\\");
        }catch (Exception e){
            e.printStackTrace();
        }
        return "adasdasd";
    }

}
