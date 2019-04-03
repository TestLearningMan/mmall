package com.mmall.util;

import ch.qos.logback.classic.Logger;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;


public class FTPUtil {
    private static Logger logger=(Logger) LoggerFactory.getLogger(FTPUtil.class);
    private static String ftpIp=PropertiesUtil.getProperty("ftp.server.ip");
    private static String ftpUser=PropertiesUtil.getProperty("ftp.user");
    private static String ftpPassword=PropertiesUtil.getProperty("ftp.pass");

    private String ip;
    private int port;
    private String user;
    private String psw;
    private static FTPClient ftpClient;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPsw() {
        return psw;
    }

    public void setPsw(String psw) {
        this.psw = psw;
    }

    public FTPClient getFtpClient() {
        return ftpClient;
    }

    public void setFtpClient(FTPClient ftpClient) {
        this.ftpClient = ftpClient;
    }

    public FTPUtil(String ip, int port, String user, String psw) {
        this.ip = ip;
        this.port = port;
        this.user = user;
        this.psw = psw;
    }

    public static boolean uploadFile(List<File> fileList) throws IOException {
        FTPUtil ftpUtil=new FTPUtil(ftpIp,21,ftpUser,ftpPassword);
        logger.info("开始上传");
        boolean result=ftpUtil.uploadFile("image",fileList);
        logger.info("上传完成");
        return result;
    }

    private boolean uploadFile(String remotePath, List<File> fileList) throws IOException {
        boolean uploaded=true;
        FileInputStream fis=null;
        //连接FTP服务器
        if (connectServer(this.ip,this.port,this.user,this.psw)){
            try {
                ftpClient.changeWorkingDirectory(remotePath);
                ftpClient.setControlEncoding("UTF-8");
                ftpClient.setBufferSize(1024);
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                //打开FTP被动模式
                ftpClient.enterLocalPassiveMode();
                //将文件转变成InputStream,然后存储起来
                for (File fileItem:fileList){
                    fis=new FileInputStream(fileItem);
                    ftpClient.storeFile(fileItem.getName(),fis);
                }
            } catch (IOException e) {
                logger.error("上传文件异常",e);
                uploaded=false;
            }finally {
                //无论是否上传成功，都需要关闭流和连接
                fis.close();
                ftpClient.disconnect();
            }
        }
        return uploaded;
    }
    private static boolean connectServer(String ip, int port, String user,String psw){
        boolean isSuccess=false;
        ftpClient=new FTPClient();
        try {
            ftpClient.connect(ip);
            isSuccess = ftpClient.login(user,ftpPassword);
        } catch (IOException e) {
            logger.error("连接FTP服务器异常",e);
        }
        return isSuccess;
    }

}
