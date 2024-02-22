package com.shuai.pojo.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: fengxin
 * @CreateTime: 2023-08-07  11:47
 * @Description: 帖子评论表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comment {

    // 评论 id（评论时间+用户id）
    private String id;

    // 帖子 id
    private String postId;

    // 评论内容
    private String content;

    // 评论者id
    private Long userId;

    // 父评论id
    private String parentId;

    // 评论创建时间
    private String createTime;

}
