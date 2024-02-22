package com.shuai.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: fengxin
 * @CreateTime: 2023-04-20  20:57
 * @Description: 服务器发送给浏览器的 websocket数据
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultMessage {

    // 消息类型
    private String informType;

    // 发送者id
    private Long fromId;

    // 消息内容
    private Object message;
}
