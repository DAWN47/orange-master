package com.shuai.pojo.po;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: fengxin
 * @CreateTime: 2023-08-11  18:54
 * @Description: 系统通知消息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Inform {

    // 主键（发送者id + 时间 + 接收者id）
    private String id;

    // 发送者（来自） id
    private Long fromId;

    // 接收者（给予） id
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
    @TableField("`read`")
    private int read;

    // 通知时间
    private String informTime;

}
