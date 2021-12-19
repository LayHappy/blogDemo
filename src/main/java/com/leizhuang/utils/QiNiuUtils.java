package com.leizhuang.utils;

import com.alibaba.fastjson.JSON;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * @author LeiZhuang
 * @date 2021/12/19 17:16
 */
@Component
public class QiNiuUtils {
    //...生成上传凭证，然后准备上传
    @Value("${qiniu.accessKey}")
    private String accessKey;
    @Value("${qiniu.secretKey}")
    private String secretKey;
    @Value("${qiniu.bucket}")
    private String bucket;
    public static final String url = "http://r4cuii689.hn-bkt.clouddn.com";//测试域名，可使用30个自然日

    public boolean upload(MultipartFile file, String fileName) {    //构造一个带指定 Region 对象的配置类
        Configuration cfg = new Configuration(Region.region2());
//...其他参数参考类注释
        UploadManager uploadManager = new UploadManager(cfg);

//默认不指定key的情况下，以文件内容的hash值作为文件名


        try {
            byte[] uploadBytes =file.getBytes();
            Auth auth = Auth.create(accessKey, secretKey);
            String upToken = auth.uploadToken(bucket);

                Response response = uploadManager.put(uploadBytes, fileName, upToken);
                //解析上传成功的结果
                DefaultPutRet putRet = JSON.parseObject(response.bodyString(), DefaultPutRet.class);
               /* System.out.println(putRet.key);
                System.out.println(putRet.hash);*/
                return  true;

        }  catch (IOException e) {
            e.printStackTrace();
        }
        return false;
   }

}
