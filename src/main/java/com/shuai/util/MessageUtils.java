package com.shuai.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shuai.pojo.vo.ResultMessage;

/**
 * @Author: fengxin
 * @CreateTime: 2023-04-20  21:01
 * @Description: 用来封装消息的工具类
 */
public class MessageUtils {

    public static String getMessage(String informType, Long fromId, Object message) {
        try {
            // 1.对系统给客户端发送消息的数据进行封装
            ResultMessage resultMessage = new ResultMessage();
            resultMessage.setInformType(informType);
            resultMessage.setMessage(message);
            if (fromId != null) {
                resultMessage.setFromId(fromId);
            }
            // 2.转换成字符串json格式
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(resultMessage);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



}
