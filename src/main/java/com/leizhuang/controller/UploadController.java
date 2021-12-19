package com.leizhuang.controller;

import com.leizhuang.utils.QiNiuUtils;
import com.leizhuang.vo.Result;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * @author LeiZhuang
 * @date 2021/12/19 17:03
 */
@RestController
@RequestMapping("upload")
public class UploadController {
    @Autowired
    private QiNiuUtils qiNiuUtils;

    @PostMapping
    public Result upload(@RequestParam("image") MultipartFile file) {
//        原始文件名称
        String originalFilename = file.getOriginalFilename();

//唯一的文件名称
        String fileName = UUID.randomUUID().toString() + "." + StringUtils.substringAfterLast(originalFilename, ".");
//上传文件到？--》七牛云，图片服务器
        boolean upload = qiNiuUtils.upload(file, fileName);
        if (upload){
            return Result.success(QiNiuUtils.url+"/"+fileName);
        }
        return Result.fail(70000,"图片上传失败");
    }
}
