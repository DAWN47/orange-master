package com.shuai.service.impl;

import cn.hutool.core.codec.Base64;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.shuai.service.WxService;
import com.shuai.common.RedisKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;

@Service
@Slf4j
@Transactional
public class WxServiceImpl implements WxService {


    @Autowired
    private RedisTemplate<String,Object> redisTemplate;
    /**
     * 微信解密
     * @param: [encryptedData, vi, sessionId]
     * @return: java.lang.String
     **/
    @Override
    public String WxDecrypt(String encryptedData, String vi, String sessionId) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        // 开始解密
        log.info("encryptedData==>{}",encryptedData);
        log.info("vi==>{}",vi);
        log.info("sessionId==>{}",sessionId);
        // 这里通过前端传回的”sessionId“（其实是方法getSessionId()给的”uuid”），从redis中拿到真正的sessionId =》sessionKey
        String json = (String) redisTemplate.opsForValue().get(RedisKey.WX_SESSION_ID + sessionId);
        JSONObject jsonObject = JSON.parseObject(json);
        String sessionKey = (String) jsonObject.get("session_key");

        byte[] encData = Base64.decode(encryptedData);
        byte[] iv = Base64.decode(vi);
        byte[] key = Base64.decode(sessionKey);

        AlgorithmParameterSpec ivSpec = new IvParameterSpec(iv);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec keySpec = new SecretKeySpec(key,"AES");
        cipher.init(Cipher.DECRYPT_MODE,keySpec,ivSpec);
        return new String(cipher.doFinal(encData),"UTF-8");
    }
}
