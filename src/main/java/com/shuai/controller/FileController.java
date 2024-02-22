package com.shuai.controller;

import com.shuai.handler.UserThreadLocal;
import com.shuai.pojo.vo.WxAvatarVo;
import com.shuai.util.EqualUtil;
import com.shuai.util.QiniuUtils;
import com.shuai.util.Result;
import com.shuai.util.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.Map;
import java.util.Objects;

/**
 * @Author: fengxin
 * @CreateTime: 2022-11-29  19:49
 * @Description: 文件工具控制器：用于文件上传、短信验证、邮箱发送...等
 */
@Slf4j
@Transactional
@RestController
@RequestMapping("/file")
public class FileController {

    @Autowired
    private QiniuUtils qiniuUtils;


    /**
     * @description: 上传文件
     * @author: fengxin
     * @date: 2023/8/27 17:25
     * @param: [file]
     * @return: 文件的部分 url（需后端提供拼接）
     **/
    @PostMapping("/upload")
    public Result upload(@RequestBody MultipartFile file) throws IOException {
        log.info("传过来的file==>{}",file);
        if (Objects.equals(file,null)) {
            return Result.fail("上传的图片不能为空！");
        }
        //根据文件输入流原文件名获取url
        String url = qiniuUtils.upload(file.getInputStream(), file.getOriginalFilename());
        if (EqualUtil.objectIsEmpty(url)) {
            return Result.fail("文件上传失败！");
        }
        log.info("文件上传成功==>{}",url);
        return Result.success("文件上传成功",url);
    }

    /**
     * @description: 微信用户上传微信头像
     * @author: fengxin
     * @date: 2023/9/21 18:01
     * @param: [wxAvatarVo]
     * @return: 文件的部分 url（需后端提供拼接）
     **/
    @PostMapping("/uploadWx")
    public Result uploadWx(@RequestBody Map<String,Object> data) throws IOException {
        log.info("传过来的数据 ==>{}",data);
        if (Objects.equals(data,null)) {
            return Result.fail("上传的图片资源不能为空！");
        }
        // 获取base64编码的数据
        String avatarUrl = data.get("avatarUrl").toString();
        String avatarUrlBase64 = avatarUrl.split(",")[1];

        // 将其解码成字节数组
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] bytes = decoder.decode(avatarUrlBase64);

        // 上传七牛云
        String url = qiniuUtils.upload(bytes,"Wx-" + UserThreadLocal.get().getId() + "-" + TimeUtil.getNowTimeString());
        if (EqualUtil.objectIsEmpty(url)) {
            return Result.fail("文件上传失败！");
        }

        log.info("文件上传成功==>{}",url);
        return Result.success("文件上传成功",url);
    }





    /**
     * @description: 上传文件(一次性最多五张)
     * @author: fengxin
     * @date: 2023/8/28 22:20
     * @param: [files]
     * @return: 文件的部分 url（多个）
     **/
    @PostMapping("/uploadMany")
    public Result uploadMany(@RequestBody MultipartFile[] files) throws IOException {
        log.info("传过来的file==>{}", (Object) files);
        String[] urls = new String[5];
        if (Objects.equals(files,null)) {
            return Result.fail("上传的图片不能为空！");
        }
        if (files.length > 5) {
            return Result.fail("上传图片一次最多五张！");
        }
        int i = 0;
        for (MultipartFile file :files) {
            //根据文件输入流原文件名获取url
            String url = qiniuUtils.upload(file.getInputStream(), file.getOriginalFilename());
            urls[i++] = url;
        }
        log.info("文件上传成功==>{}", (Object) urls);
        return Result.success("文件上传成功",urls);
    }

    @DeleteMapping("/delete")
    public Result delete(@RequestBody MultipartFile file) throws IOException{
        log.info("传过来的file==>{}",file);
        //根据原文件名称删除七牛云的文件
        qiniuUtils.delete(file.getOriginalFilename());
        return Result.success("删除成功");
    }
}