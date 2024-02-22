package com.shuai.service;

import com.shuai.pojo.po.Comment;
import com.shuai.pojo.po.Review;
import com.shuai.pojo.vo.CommentVo;
import com.shuai.pojo.vo.ReviewVo;
import com.shuai.util.Result;

import java.util.ArrayList;

/**
 * @Author: fengxin
 * @CreateTime: 2023-08-14  08:44
 * @Description: TODO
 */
public interface ReviewService {

    // 发布评论
    Result makeReview(Review review);

    // 删除评论
    Result deleteReview(String reviewId);

    // 获取评论列表
    ArrayList<ReviewVo> getReviewList(String goodId, String starLevel);

}
