package com.shuai.service;

import com.shuai.util.Result;

/**
 * @Author: fengxin
 * @CreateTime: 2023-08-11  12:27
 * @Description: 完成所有的点赞相关服务
 */
public interface LikeService {

    // 通过 postId 点赞指定的帖子
    Result likePost(String postId);

    // 通过 commentId 点赞指定的评论
    Result likeComment(String commentId);

    // 通过 reviewId 点赞指定的评价
    Result likeReview(String reviewId);
}
