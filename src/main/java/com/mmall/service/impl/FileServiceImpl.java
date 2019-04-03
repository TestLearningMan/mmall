package com.mmall.service.impl;


import com.google.common.collect.Lists;
import com.mmall.service.IFileService;
import com.mmall.util.FTPUtil;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service("iFileService")
public class FileServiceImpl implements IFileService {
    private Logger logger= LoggerFactory.getLogger(FileServiceImpl.class);
    public String upload(MultipartFile file,String path){
        String fileName=file.getOriginalFilename();
        String fileExtensionName=fileName.substring(fileName.lastIndexOf(".")+1);
        String uploadFileName= UUID.randomUUID().toString()+"."+fileExtensionName;
        logger.info("开始上传文件,上传文件的文件名:{},上传文件的路径:{}，新文件名:{}",fileName,path,uploadFileName);
        File fileDir=new File(path);
        //文件夹不存在或者有同名文件，而不是文件夹时，创建文件夹
        if (!fileDir.exists()||!fileDir.isDirectory()){
            //赋予可写权限
            fileDir.setWritable(true);
            //mkdirs会创建多重文件夹.mkdir只创建当前目录下的文件夹
            fileDir.mkdirs();
        }

        //在指定目录下创建文件
        File targetFile=new File(path,uploadFileName);
        try {
            //将上传的文件file的内容传至创建的文件中
            file.transferTo(targetFile);
            //文件已经上传成功了
            //将targetFile上传到我们的FTP服务器上
            FTPUtil.uploadFile(Lists.newArrayList(targetFile));
            //上传完成后，删除upload下面的文件
            targetFile.delete();
        } catch (IOException e) {
            logger.error("文件上传异常",e);
            return null;
        }

        return targetFile.getName();
    }
}