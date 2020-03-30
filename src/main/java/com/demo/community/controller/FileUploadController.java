package com.demo.community.controller;

import com.aliyun.oss.OSSException;
import com.demo.community.dto.FileDTO;
import com.demo.community.provider.AliyunProbvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Controller
public class FileUploadController {

    @Autowired
    private AliyunProbvider aliyunProbvider;

    @Value("${aliyun.picture.path}")
    private String picturePath;

    @RequestMapping("/file/upload")
    @ResponseBody
    public FileDTO fileUpload(
            HttpServletRequest request
    ) {
        try {
            MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
            MultipartFile file = multipartHttpServletRequest.getFile("editormd-image-file");
            String filePath = aliyunProbvider.upload(file.getInputStream(), file.getOriginalFilename());
            FileDTO fileDTO = new FileDTO();
            fileDTO.setSuccess(1);
            fileDTO.setUrl(picturePath + filePath);
            return fileDTO;
        } catch (OSSException oe) {
            FileDTO fileDTO = new FileDTO();
            fileDTO.setSuccess(0);
            fileDTO.setMessage("图片上传失败了");
            return fileDTO;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new FileDTO();
    }
}
