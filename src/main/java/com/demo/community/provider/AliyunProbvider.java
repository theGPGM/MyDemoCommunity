package com.demo.community.provider;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.UUID;

@Service
public class AliyunProbvider {

    @Value("${aliyun.accessKeyId}")
    private String accessKeyId;

    @Value("${aliyun.accesskeySecret}")
    private String accessKeySecret;

    @Value("${aliyun.endpoint}")
    private String endpoint;

    @Value("${aliyun.bucketName}")
    private String bucketName;

    public String upload(
            InputStream fileStream,
            String originalFileName
    ) throws OSSException {

        // <yourFileName>上传文件到OSS时需要指定包含文件后缀在内的完整路径，例如 abc/efg/123.jpg。
        String[] filePath = originalFileName.split("\\.");
        String generateFileName = "";
        if (filePath.length > 1) {
            generateFileName = "images/" + UUID.randomUUID().toString() + "." + filePath[filePath.length - 1];
        } else {
            //输入的文件不存在
            return null;
        }

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        // 上传内容到指定的存储空间（bucketName）并保存为指定的文件名称（objectName）。
        ossClient.putObject(bucketName, generateFileName, fileStream);

        // 关闭OSSClient。
        ossClient.shutdown();

        return generateFileName;
    }
}
