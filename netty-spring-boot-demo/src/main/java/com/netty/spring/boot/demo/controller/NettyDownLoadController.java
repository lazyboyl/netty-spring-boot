package com.netty.spring.boot.demo.controller;

import com.netty.spring.boot.core.annotation.NettyController;
import com.netty.spring.boot.core.annotation.NettyRequestMapping;
import com.netty.spring.boot.core.annotation.NettyRequestMethod;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * @author linzf
 * @since 2020/7/9
 * 类描述：
 */
@NettyController
@NettyRequestMapping("download")
public class NettyDownLoadController {

    @NettyRequestMapping(path = "downLoadInfo", method = NettyRequestMethod.POST)
    public void downLoadInfo(ChannelHandlerContext ctx, FullHttpRequest request, String fileName) {
        System.out.println("fileName=>" + fileName);
        File file = new File("D:\\openLib.rar");
        try {
            final RandomAccessFile raf = new RandomAccessFile(file, "r");
            long fileLength = raf.length();
            HttpResponse response = new DefaultHttpResponse(HTTP_1_1, HttpResponseStatus.OK);
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, fileLength);
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/octet-stream");
            response.headers().add(HttpHeaderNames.CONTENT_DISPOSITION, String.format("attachment; filename=\"%s\"", file.getName()));
            ctx.write(response);
            ChannelFuture sendFileFuture = ctx.write(new DefaultFileRegion(raf.getChannel(), 0, fileLength), ctx.newProgressivePromise());
            sendFileFuture.addListener(new ChannelProgressiveFutureListener() {
                @Override
                public void operationComplete(ChannelProgressiveFuture future)
                        throws Exception {
                    System.out.println(file.getName());
                }

                @Override
                public void operationProgressed(ChannelProgressiveFuture future,
                                                long progress, long total) throws Exception {
                    if (total < 0) {
                        System.out.println(file.getName() + "----");
                    } else {
                        System.out.println(file.getName() + "----" + "----" + total);
                    }
                }
            });
            ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println(file.getName() + "===" + e.getMessage());
        }
    }

}
