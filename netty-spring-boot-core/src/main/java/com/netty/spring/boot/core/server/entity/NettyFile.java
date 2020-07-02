package com.netty.spring.boot.core.server.entity;

import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.multipart.FileUpload;

import java.io.*;

/**
 * @author linzf
 * @since 2020/7/2
 * 类描述：
 */
public class NettyFile {

    public NettyFile() {
        super();
    }

    public NettyFile(FileUpload fileUpload,HttpHeaders headers) {
        this.fileUpload = fileUpload;
        if(fileUpload!=null){
            this.fileName = fileUpload.getFilename();
        }
        this.headers = headers;
    }

    /**
     * 上传的文件的存储对象
     */
    private FileUpload fileUpload;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件的流
     */
    private InputStream inputStream;

    /**
     * 请求头信息
     */
    private HttpHeaders headers;

    public HttpHeaders getHeaders() {
        return headers;
    }

    public void setHeaders(HttpHeaders headers) {
        this.headers = headers;
    }

    public FileUpload getFileUpload() {
        return fileUpload;
    }

    public void setFileUpload(FileUpload fileUpload) {
        this.fileUpload = fileUpload;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void transferTo(String path) throws IOException {
        this.inputStream = new FileInputStream(fileUpload.getFile());
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(path + fileUpload.getFilename());
            byte[] b = new byte[1024];
            // 写入数据
            while ((inputStream.read(b)) != -1) {
                fos.write(b);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }


    }


}
