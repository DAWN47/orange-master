package com.shuai.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: fengxin
 * @CreateTime: 2023-08-15  16:20
 * @Description: 传给前端的聊天记录数据
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatRecordVo {

    // 发送者id
    private Long senderId;

    // 接收者id
    private Long receiverId;

    // 消息类型
    private String messageType;

    // 消息内容
    private String content;

    // 是否已读（1已读 0未读）
    private int read;

    // 发送时间
    private String sendTime;


    // 聊天对象 id
    private Long toId;

    // 聊天对象的昵称
    private String toNickname;

    // 聊天对象的头像
    private String toAvatar;
}
