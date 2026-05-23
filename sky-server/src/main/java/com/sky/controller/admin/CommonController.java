package com.sky.controller.admin;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * 通用接口
 */

@RestController
@Api(tags = "通用接口")
@Slf4j
public class CommonController {
    @Value("${file.upload-dir:uploads}")
    private String uploadDir;
    @Value("${server.servlet.context-path:}")
    private String contextPath;
    @Value("${server.port:8080}")
    private String port;
    @PostMapping("/admin/common/upload")
    @ApiOperation("文件上传")
     public Result<String> upload(MultipartFile file){
        log.info("文件上传开始:{}",file);
        try{
            if(file.isEmpty()){
                return Result.error("文件上传不能为空");
            }
            //获取原始文件名
            String originalFilename=file.getOriginalFilename();
            //截取文件名后缀
            String extension=originalFilename.substring(originalFilename.lastIndexOf("."));
               String fileName=UUID.randomUUID().toString()+extension;
            //创建文件上传目录
            File uploadDirectory=new File(uploadDir);
            if(!uploadDirectory.exists()){
                uploadDirectory.mkdirs();
            }
            Path filePath= Paths.get(uploadDirectory.getAbsolutePath(),fileName);
            Files.copy(file.getInputStream(),filePath);
            //创建访问URL
            String accessPath = "http://localhost:" + port + contextPath  + uploadDir + "/" + fileName;
            log.info("文件上传成功：{}，保存路径：{}", originalFilename, filePath.toString());
            return Result.success(accessPath);
        }catch (IOException e) {
            log.error("文件上传失败：{}", e);
            return Result.error("文件上传失败:" + e.getMessage());
        }
     }
}
