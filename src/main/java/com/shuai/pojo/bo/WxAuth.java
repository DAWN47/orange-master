package com.shuai.pojo.bo;

import lombok.Data;

/**
 * @Author: fengxin
 * @CreateTime: 2023-07-29  20:49
 * @Description: TODO
 */
@Data
public class WxAuth {
    //微信传递的加密数据，后端解密
    private String encryptedData;

    //微信传递解密算法初始向量
    private String iv;

    //第一步传递前端的sessionId
    private String sessionId;
}
