package com.itheima.reggie.controller;

import com.itheima.reggie.common.R;
import com.itheima.reggie.utils.AlibabaOssUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping("/common")
@Slf4j
public class FileController {

    @Autowired
    private AlibabaOssUtils aliOssUtils;


    /**
     * 文件上传
     */
    @PostMapping("/upload")
    public R<String> upload(@RequestParam("file") MultipartFile file) {
        log.info("上传图片，原始文件名：{}", file.getOriginalFilename());

        // 判断文件是否为空
        if (file.isEmpty()) {
            return R.error("上传失败，请选择文件");
        }

        // 文件保存路径
//        String filePath = "D:\\ruoyi\\uploadPath\\";



        // 创建文件实例
//        File dest = new File(filePath + file.getOriginalFilename());

//        // 判断文件夹是否存在，不存在则创建
//        if (!dest.getParentFile().exists()) {
//            dest.getParentFile().mkdirs();
//        }

        try {
            // 保存文件
//            file.transferTo(dest);
            //原始文件名称
            String originalFilename = file.getOriginalFilename();
            //截取后缀
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            //UUID+extension组成新文件名字
            String objectName = UUID.randomUUID().toString() + extension;
            //文件上传请求路径
            String url = aliOssUtils.upload(file.getBytes(), objectName);
            return R.success(url);
        } catch (IOException e) {
            log.error("文件上传失败", e);
            return R.error("文件上传失败");
        }
    }

    /**
     * 文件下载
     */
    /**
     * 文件下载
     */
    @GetMapping("/download")
    public ResponseEntity<Resource> download(@RequestParam("name") String url) {

        try {
            // 创建URL资源
            Resource resource = new UrlResource(url);

            // 判断资源是否存在且可读
            if (resource.exists() && resource.isReadable()) {
                // 设置响应头
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                throw new RuntimeException("文件不存在或无法读取");
            }
        } catch (Exception e) {
            log.error("文件下载异常", e);
            return ResponseEntity.badRequest().build();
        }
    }

//    @GetMapping("/download")
//    public ResponseEntity<Resource> download(@RequestParam("name") String filename) {
//
//        try {
//            // 文件存储路径
//            Path filePath = Paths.get("D:\\ruoyi\\uploadPath\\", filename);
//
//            log.info("尝试访问的文件路径：{}", filePath.toString());
//
//            Resource resource = new UrlResource(filePath.toUri());
//
//
//            // 判断文件是否存在
//            if (resource.exists() || resource.isReadable()) {
//                // 设置响应头
//                return ResponseEntity.ok()
//                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
//                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
//                        .body(resource);
//            } else {
//                throw new RuntimeException("文件不存在或无法读取");
//            }
//        } catch (Exception e) {
//            log.error("文件下载异常", e);
//            return ResponseEntity.badRequest().build();
//        }
//    }
}
