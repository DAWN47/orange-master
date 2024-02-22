package com.shuai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shuai.common.Constants;
import com.shuai.controller.ChatEndpoint;
import com.shuai.handler.UserThreadLocal;
import com.shuai.mapper.*;
import com.shuai.pojo.po.Inform;
import com.shuai.pojo.po.*;
import com.shuai.pojo.vo.InformVo;
import com.shuai.service.LikeService;
import com.shuai.util.Result;
import com.shuai.util.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * @Author: fengxin
 * @CreateTime: 2023-08-11  12:27
 * @Description: 完成所有的点赞相关服务
 */
@Service
@Transactional
public class LikeServiceImpl implements LikeService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PostLikeMapper postLikeMapper;

    @Autowired
    private CommentLikeMapper commentLikeMapper;

    @Autowired
    private ReviewLikeMapper reviewLikeMapper;

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private GoodMapper goodMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private ReviewMapper reviewMapper;


    @Override
    public Result likePost(String postId) {
        // 0. 拿到当前用户id 并查询到 昵称头像
        Long userId = UserThreadLocal.get().getId();
        User userInfo = userMapper.selectById(userId);
        // 1. 通过 postId 查询对应帖子 并 确实帖子是否存在
        Post post = postMapper.selectById(postId);
        if (Objects.equals(post,null)) {
            return Result.fail("点赞失败！该帖子不存在");
        }
        // 2. 查询数据库是否已有点赞记录（deleted可能是1 or 0）
        PostLike postLikeInfo = postLikeMapper.selectOne(
                new LambdaQueryWrapper<PostLike>()
                        .eq(PostLike::getUserId,userId)
                        .eq(PostLike::getPostId,postId));
        // 3. 无记录：添加新记录，有记录：deleted为0则变1、为1则变0
        if (Objects.equals(postLikeInfo,null)) {
            // 3.1 点赞对应帖子（新增插入数据）
            String id = userId + postId;
            PostLike postLike =
                    new PostLike(id,userId,postId,0, TimeUtil.getNowTime());
            int insert = postLikeMapper.insert(postLike);
            if (insert > 0){
                // 4. 通知帖子作者，有人点赞了他的帖子
                // 4.1 组装信息(fromId, fromNickname, fromAvatar, toId, objectId, sonObjectId, firstImage, content, informTime)
                InformVo informVo = new InformVo(
                        userId,
                        userInfo.getNickname(),
                        userInfo.getAvatar(),
                        post.getAuthorId(),
                        postId,
                        null,
                        post.getPostFirstPicture(),
                        Constants.LIKE_POST,
                        TimeUtil.getNowTime()
                );
                // 4.2 调用 websocket 发送消息
                ChatEndpoint.sendInfo(informVo);
                return Result.success("点赞帖子成功！");
            }else {
                return Result.fail("点赞帖子失败，联系后台");
            }
        }else {
            // 3.2 修改记录，deleted为0则变1、为1则变0
            if(postLikeInfo.getDeleted() > 0) {
                postLikeInfo.setDeleted(0);
                // 注：需填入新的点赞时间
                postLikeInfo.setLikeTime(TimeUtil.getNowTime());
                postLikeMapper.updateById(postLikeInfo);
                // 4. 通知帖子作者，有人点赞了他的帖子
                // 4.1 组装信息(fromId, fromNickname, fromAvatar, toId, objectId, sonObjectId, firstImage, content, informTime)
                InformVo informVo = new InformVo(
                        userId,
                        userInfo.getNickname(),
                        userInfo.getAvatar(),
                        post.getAuthorId(),
                        postId,
                        null,
                        post.getPostFirstPicture(),
                        Constants.LIKE_POST,
                        TimeUtil.getNowTime()
                );
                // 4.2 调用 websocket 发送消息
                ChatEndpoint.sendInfo(informVo);
                return Result.success("点赞帖子成功！");
            }else {
                postLikeInfo.setDeleted(1);
                postLikeMapper.updateById(postLikeInfo);
                return Result.success("取消点赞帖子成功！");
            }
        }
    }

    @Override
    public Result likeComment(String commentId) {
        // 0. 拿到当前用户id 并查询到 昵称头像
        Long userId = UserThreadLocal.get().getId();
        User userInfo = userMapper.selectById(userId);
        // 1. 通过 commentId 查询对应评论 并 确实评论是否存在
        Comment comment = commentMapper.selectById(commentId);
        if (Objects.equals(comment,null)) {
            return Result.fail("点赞失败！该评论不存在");
        }
        /* 查询一下对应 post */
        Post post = postMapper.selectById(comment.getPostId());
        // 2. 查询数据库是否已有点赞记录（deleted可能是1 or 0）
        CommentLike commentLikeInfo = commentLikeMapper.selectOne(
                new LambdaQueryWrapper<CommentLike>()
                        .eq(CommentLike::getUserId, userId)
                        .eq(CommentLike::getCommentId, commentId));
        // 3. 无记录：添加新记录，有记录：deleted为0则变1、为1则变0
        if (Objects.equals(commentLikeInfo,null)) {
            // 3.1 点赞对应评论（新增插入数据）
            String id = userId + commentId;
            CommentLike commentLike =
                    new CommentLike(id, userId, commentId, 0, TimeUtil.getNowTime());
            int insert = commentLikeMapper.insert(commentLike);
            if (insert > 0){
                // 4. 通知评论发布者，有人点赞了他的评论
                // 4.1 组装信息(fromId, fromNickname, fromAvatar, toId, objectId, sonObjectId, firstImage, content, informTime)
                InformVo informVo = new InformVo(
                        userId,
                        userInfo.getNickname(),
                        userInfo.getAvatar(),
                        comment.getUserId(),
                        comment.getPostId(),
                        commentId,
                        post.getPostFirstPicture(),
                        Constants.LIKE_COMMENT,
                        TimeUtil.getNowTime()
                );
                // 4.2 调用 websocket 发送消息
                ChatEndpoint.sendInfo(informVo);
                return Result.success("点赞评论成功！");
            }else {
                return Result.fail("点赞评论失败，联系后台");
            }
        }else {
            // 3.2 修改记录，deleted为0则变1、为1则变0
            if(commentLikeInfo.getDeleted() > 0) {
                commentLikeInfo.setDeleted(0);
                // 注：需填入新的点赞时间
                commentLikeInfo.setLikeTime(TimeUtil.getNowTime());
                commentLikeMapper.updateById(commentLikeInfo);
                // 4. 通知评论发布者，有人点赞了他的评论
                // 4.1 组装信息(fromId, fromNickname, fromAvatar, toId, objectId, sonObjectId, firstImage, content, informTime)
                InformVo informVo = new InformVo(
                        userId,
                        userInfo.getNickname(),
                        userInfo.getAvatar(),
                        comment.getUserId(),
                        comment.getPostId(),
                        commentId,
                        post.getPostFirstPicture(),
                        Constants.LIKE_COMMENT,
                        TimeUtil.getNowTime()
                );
                // 4.2 调用 websocket 发送消息
                ChatEndpoint.sendInfo(informVo);
                return Result.success("点赞评论成功！");
            }else {
                commentLikeInfo.setDeleted(1);
                commentLikeMapper.updateById(commentLikeInfo);
                return Result.success("取消点赞评论成功！");
            }
        }
    }

    @Override
    public Result likeReview(String reviewId) {
        // 0. 拿到当前用户id 并查询到 昵称头像
        Long userId = UserThreadLocal.get().getId();
        User userInfo = userMapper.selectById(userId);
        // 1. 通过 reviewId 查询对应评价 并 确实商品评价是否存在
        Review review = reviewMapper.selectById(reviewId);
        if (Objects.equals(review,null)) {
            return Result.fail("点赞失败！该商品评价不存在");
        }
        /* 查询一下对应 good */
        Good good = goodMapper.selectById(review.getGoodId());
        // 2. 查询数据库是否已有点赞记录（deleted可能是1 or 0）
        ReviewLike reviewLikeInfo = reviewLikeMapper.selectOne(
                new LambdaQueryWrapper<ReviewLike>()
                        .eq(ReviewLike::getUserId, userId)
                        .eq(ReviewLike::getReviewId, reviewId));
        // 3. 无记录：添加新记录，有记录：deleted为0则变1、为1则变0
        if (Objects.equals(reviewLikeInfo,null)) {
            // 3.1 点赞对应商品评价（新增插入数据）
            String id = userId + reviewId;
            ReviewLike reviewLike =
                    new ReviewLike(id, userId, reviewId, 0, TimeUtil.getNowTime());
            int insert = reviewLikeMapper.insert(reviewLike);
            if (insert > 0){
                // 4. 通知评价发布者，有人点赞了他的评价
                // 4.1 组装信息(fromId, fromNickname, fromAvatar, toId, objectId, sonObjectId, firstImage, content, informTime)
                InformVo informVo = new InformVo(
                        userId,
                        userInfo.getNickname(),
                        userInfo.getAvatar(),
                        review.getUserId(),
                        review.getGoodId(),
                        reviewId,
                        good.getGoodFirstPicture(),
                        Constants.LIKE_REVIEW,
                        TimeUtil.getNowTime()
                );
                // 4.2 调用 websocket 发送消息
                ChatEndpoint.sendInfo(informVo);
                return Result.success("点赞商品评价成功！");
            }else {
                return Result.fail("点赞商品评价失败，联系后台");
            }
        }else {
            // 3.2 修改记录，deleted为0则变1、为1则变0
            if(reviewLikeInfo.getDeleted() > 0) {
                reviewLikeInfo.setDeleted(0);
                // 注：需填入新的点赞时间
                reviewLikeInfo.setLikeTime(TimeUtil.getNowTime());
                reviewLikeMapper.updateById(reviewLikeInfo);
                // 4. 通知评价发布者，有人点赞了他的评价
                // 4.1 组装信息(fromId, fromNickname, fromAvatar, toId, objectId, sonObjectId, firstImage, content, informTime)
                InformVo informVo = new InformVo(
                        userId,
                        userInfo.getNickname(),
                        userInfo.getAvatar(),
                        review.getUserId(),
                        review.getGoodId(),
                        reviewId,
                        good.getGoodFirstPicture(),
                        Constants.LIKE_REVIEW,
                        TimeUtil.getNowTime()
                );
                // 4.2 调用 websocket 发送消息
                ChatEndpoint.sendInfo(informVo);
                return Result.success("点赞商品评价成功！");
            }else {
                reviewLikeInfo.setDeleted(1);
                reviewLikeMapper.updateById(reviewLikeInfo);
                return Result.success("取消点赞商品评价成功！");
            }
        }
    }
}
