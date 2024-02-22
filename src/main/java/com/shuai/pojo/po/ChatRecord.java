package com.shuai.pojo.po;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: fengxin
 * @CreateTime: 2023-08-04  12:14
 * @Description: 对应聊天记录表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatRecord {

    // 主键id：用户id+时间+对象id
    private String id;

    // 发送者id
    private Long senderId;

    // 接收者id
    private Long receiverId;

    // 消息类型
    private String messageType;

    // 消息内容
    private String content;

    // 是否已读（1已读 0未读）
    @TableField("`read`")
    private int read;

    // 发送时间
    private String sendTime;
}
