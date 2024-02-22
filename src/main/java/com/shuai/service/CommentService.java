package com.shuai.service;

import com.shuai.pojo.po.Comment;
import com.shuai.pojo.vo.CommentVo;
import com.shuai.util.Result;

import java.util.ArrayList;

/**
 * @Author: fengxin
 * @CreateTime: 2023-08-11  09:39
 * @Description: TODO
 */
public interface CommentService {

    // 发布评论
    Result makeComment(Comment comment);

    // 删除评论
    Result deleteComment(String commentId);

    // 获取评论列表
    ArrayList<CommentVo> getCommentList(String postId);
}
