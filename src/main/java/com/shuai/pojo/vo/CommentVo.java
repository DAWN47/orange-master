package com.shuai.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: fengxin
 * @CreateTime: 2023-08-11  11:28
 * @Description: TODO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentVo {

    // 评论 id（评论时间+用户id）
    private String commentId;

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


    // 点赞数
    private Long likes;

    // 是否是自己的评论
    private boolean own;

    // 昵称
    private String nickname;

    // 头像
    private String avatar;


}
