package com.shuai.util;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author: fengxin
 * @CreateTime: 2022-11-29  15:33
 * @Description: 七牛云文件上传工具类
 */
@Component
@Slf4j
public class QiniuUtils {

    @Value("${qiniu.accessKey}")
    private String accessKey;
    @Value("${qiniu.secretKey}")
    private String secretKey;
    @Value("${qiniu.bucket}")
    private String bucket;


    /**
     * 根据文件路径上传文件
     *
     * @param: [filepath, fileName]
     * @return: java.lang.String
     **/
    public String upload(String filepath, String fileName) {
        Configuration configuration = new Configuration(Region.regionCnEast2());
        UploadManager uploadManager = new UploadManager(configuration);
        Auth auth = Auth.create(accessKey, secretKey);
        String uploadToken = auth.uploadToken(bucket);
        String name = this.genName(fileName);
        try {
            Response response = uploadManager.put(filepath, name, uploadToken);
            return name;
        } catch (QiniuException e) {
            Response r = e.response;
            try {
                log.error("文件上传失败==>{}", r.bodyString());
            } catch (QiniuException ex) {
                //ignore
            }
            return null;
        }
    }

    /**
     * 根据字节数组上传文件
     *
     * @param: [bytes, fileName]
     * @return: java.lang.String
     **/
    public String upload(byte[] bytes, String fileName) {
        Configuration configuration = new Configuration(Region.regionCnEast2());
        UploadManager uploadManager = new UploadManager(configuration);
        Auth auth = Auth.create(accessKey, secretKey);
        String uploadToken = auth.uploadToken(bucket);
        String name = this.genName(fileName);
        try {
            Response response = uploadManager.put(bytes, name, uploadToken);
            return name;
        } catch (QiniuException e) {
            Response r = e.response;
            try {
                log.error("文件上传失败==>{}", r.bodyString());
            } catch (QiniuException ex) {
                //ignore
            }
            return null;
        }
    }

    /**
     * 根据文件输入流上传文件
     *
     * @param: [stream, fileName]
     * @return: java.lang.String
     **/
    public String upload(InputStream stream, String fileName) {
        Configuration configuration = new Configuration(Region.regionCnEast2());
        UploadManager uploadManager = new UploadManager(configuration);
        Auth auth = Auth.create(accessKey, secretKey);
        String uploadToken = auth.uploadToken(bucket);
        String name = this.genName(fileName);
        try {
            Response response = uploadManager.put(stream, name, uploadToken, null, null);
            //解析上传成功的结果
            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
//            log.info("解析文件上传成功的结果：{}", putRet);
            return name;
        } catch (QiniuException e) {
            Response r = e.response;
            try {
                log.error("文件上传失败==>{}", r.bodyString());
            } catch (QiniuException ex) {
                //ignore
            }
            return null;
        }
    }

    /**
     * 根据文件名删除文件
     *
     * @param: [fileName]
     * @return: void
     **/
    public void delete(String fileName) {
        //构造一个指定的 Region 对象的配置类
        Configuration configuration = new Configuration(Region.regionCnEast2());
        Auth auth = Auth.create(accessKey, secretKey);
        BucketManager bucketManager = new BucketManager(auth, configuration);
        try {
            bucketManager.delete(bucket, fileName);
            log.error("删除文件成功");
        } catch (QiniuException e) {
            //如果遇到异常，说明删除失败
            log.error("删除失败==>code {}", e.code());
            log.error(e.response.toString());
        }
    }


    /**
     * 根据文件名生成时间文件名称
     *
     * @param: [fileName]
     * @return: java.lang.String
     **/
    public String genName(String fileName) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String format = simpleDateFormat.format(new Date());
        return (format + fileName);
    }


    public String updateWx(String tempUrl) {
        try {
            URL url = new URL(tempUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = connection.getInputStream();
                return upload(inputStream, "wx" + TimeUtil.getNowTimeString());
            }
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
