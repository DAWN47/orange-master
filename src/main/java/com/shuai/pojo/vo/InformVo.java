package com.shuai.pojo.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: fengxin
 * @CreateTime: 2023-08-15  17:25
 * @Description: 返回给前端的系统消息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InformVo {

    // 通知id
    private String informId;

    // 发送者 id
    private Long fromId;


    // 发送者昵称
    private String fromNickname;

    // 发送者头像
    private String fromAvatar;


    // 接收者 id
    private Long toId;

    // 对象 id(帖子id或商品id)
    private String objectId;

    // 子对象 id(评论id或评价id)
    private String subObjectId;

    // 首图
    private String firstImage;

    // 消息内容
    private String content;

    // 是否已读（1已读 0未读）
    private int read;

    // 通知时间
    private String informTime;

    public InformVo(Long fromId, String fromNickname, String fromAvatar, Long toId, String objectId, String subObjectId, String firstImage, String content, String informTime) {
        this.fromId = fromId;
        this.fromNickname = fromNickname;
        this.fromAvatar = fromAvatar;
        this.toId = toId;
        this.objectId = objectId;
        this.subObjectId = subObjectId;
        this.firstImage = firstImage;
        this.content = content;
        this.informTime = informTime;
    }
}
