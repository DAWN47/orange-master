package com.shuai.pojo.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: fengxin
 * @CreateTime: 2023-04-20  20:54
 * @Description: 浏览器发送给服务器的 websocket数据
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatRecordBo {

    // 来自from用户发来的信息
    private Long fromId;

    // 发送给指定toId的用户对象
    private Long toId;

    // 消息内容
    private String content;

    // 消息类型
    private String messageType;
}
