package com.shuai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shuai.common.Constants;
import com.shuai.controller.ChatEndpoint;
import com.shuai.handler.UserThreadLocal;
import com.shuai.mapper.*;
import com.shuai.pojo.po.Inform;
import com.shuai.pojo.po.*;
import com.shuai.pojo.vo.InformVo;
import com.shuai.pojo.vo.ReviewVo;
import com.shuai.service.ReviewService;
import com.shuai.util.Result;
import com.shuai.util.TimeUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Author: fengxin
 * @CreateTime: 2023-08-14  08:50
 * @Description: TODO
 */
@Service
@Transactional
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewMapper reviewMapper;

    @Autowired
    private ReviewLikeMapper reviewLikeMapper;

    @Autowired
    private GoodMapper goodMapper;

    @Autowired
    private UserMapper userMapper;



    @Override
    public Result makeReview(Review review) {
        /* 查询一下对应 good */
        Good good = goodMapper.selectById(review.getGoodId());
        // 0. 拿到当前用户id，即评价者 再拿到昵称头像
        Long userId = UserThreadLocal.get().getId();
        User userInfo = userMapper.getBriefInfo(userId);
        // 1. 设置为评价者
        review.setUserId(userId);
        // 2. 拼接评价id（评价时间+用户id）
        String reviewId = TimeUtil.getNowTimeString() + userId;
        review.setId(reviewId);
        // 3. 拿到评价时间并设置
        review.setCreateTime(TimeUtil.getNowTime());
        // 4. 根据星级，按评价等级分类
        Integer star = review.getStar();
        if (star > 5 || star < 1) {
            return Result.fail("评价失败！评价星级错误");
        }
        if (star == 1 || star == 2) review.setStarLevel("差");
        if (star == 3) review.setStarLevel("中");
        if (star == 4 || star == 5) review.setStarLevel("好");
        // 5. 向数据库插入数据
        int insert = reviewMapper.insert(review);

        if (insert > 0) {
            // 5. 判断 review.parentId 是否为 null,进行通知
            String parentId = review.getParentId();

            if (!Objects.equals(parentId,null)) {
                // 5.0 拿到父评价信息
                Review reviewInfo = reviewMapper.selectById(parentId);
                // 5.1 通知父评价的发布者，有人回复了他的评价
                // 5.1.1 组装信息(fromId, fromNickname, fromAvatar, toId, objectId, sonObjectId, firstImage, content)
                InformVo informVo = new InformVo(
                        userId,
                        userInfo.getNickname(),
                        userInfo.getAvatar(),
                        reviewInfo.getUserId(),
                        review.getGoodId(),
                        reviewInfo.getId(),
                        good.getGoodFirstPicture(),
                        Constants.REPLY_COMMENT + review.getContent(),
                        TimeUtil.getNowTime()
                );
                // 5.1.2 调用 websocket 发送消息
                ChatEndpoint.sendInfo(informVo);
            }
            // 5.2 通知商家，有人评价了他的商品
            // 5.2.1 组装信息(fromId, fromNickname, fromAvatar, toId, objectId, sonObjectId, firstImage, content)
            InformVo informVo = new InformVo(
                    userId,
                    userInfo.getNickname(),
                    userInfo.getAvatar(),
                    good.getOperatorId(),
                    good.getId(),
                    reviewId,
                    good.getGoodFirstPicture(),
                    Constants.COMMENT_GOOD + review.getContent(),
                    TimeUtil.getNowTime()
            );
            // 5.2.2 调用 websocket 发送消息
            ChatEndpoint.sendInfo(informVo);
            return Result.success("评价发布成功！");
        }else {
            return Result.fail("评价发布失败！请联系服务器");
        }

    }

    @Override
    public Result deleteReview(String reviewId) {
        // 0. 拿到当前用户id
        Long userId = UserThreadLocal.get().getId();
        // 1. 通过 reviewId 查询数据库此条记录
        Review review = reviewMapper.selectById(reviewId);
        // 2. 判断该评价是否存在，判断是否有删除权限（只有本人才能删除自己的评价）
        if (Objects.equals(review,null)) {
            return Result.fail("删除失败！该评价不存在");
        }
        if (!Objects.equals(userId,review.getUserId())) {
            return Result.fail("删除失败！只有自己才能删除自己的评价");
        }
        // 3. 删除该评价及其子评价的点赞记录
        reviewLikeMapper.deleteByReviewId(reviewId);
        // 4. 删除此条评价及其子评价
        int delete = reviewMapper.deleteReview(reviewId);
        if (delete > 0) {
            return Result.success("评价及其子评价删除成功！");
        }else {
            return Result.fail("评价删除失败！请联系服务器");
        }
    }

    @Override
    public ArrayList<ReviewVo> getReviewList(String goodId, String starLevel) {
        ArrayList<ReviewVo> reviewVoList = new ArrayList<>();
        // 0. 拿到当前用户id 并查询 昵称、头像
        Long userId = UserThreadLocal.get().getId();
        // 1. 通过商品id从数据库查询所有的评价（需先判断 starLevel 是否为 ""）
        LambdaQueryWrapper<Review> queryWrapper =
                new LambdaQueryWrapper<Review>().eq(Review::getGoodId, goodId);
        if (!Objects.equals(starLevel,"")) {
            queryWrapper.eq(Review::getStarLevel,starLevel);
        }
        List<Review> ReviewList = reviewMapper.selectList(queryWrapper);
        // 2. 标记自己的评价 并且 查询每条评价的点赞数
        for (Review review :ReviewList) {
            // 2.0 review属性值赋值给reviewVo
            ReviewVo reviewVo = new ReviewVo();
            BeanUtils.copyProperties(review,reviewVo);
            reviewVo.setReviewId(review.getId());
            // 2.1 标记是否是自己的评价
            reviewVo.setOwn(Objects.equals(review.getUserId(), userId));
            // 2.2 通过ReviewId查询单条评价的点赞量，并记录
            Long likes = reviewLikeMapper.selectCount(
                    new LambdaQueryWrapper<ReviewLike>()
                            .eq(ReviewLike::getReviewId, review.getId())
                            .eq(ReviewLike::getDeleted,0));
            reviewVo.setLikes(likes);
            // 2.3 查询评价的发表人的userId，nickname，avatar
            User user = userMapper.getBriefInfo(review.getUserId());
            reviewVo.setNickname(user.getNickname());
            reviewVo.setAvatar(user.getAvatar());
            // 2.4 拼接返回值，加入集合中
            reviewVoList.add(reviewVo);
        }
        return reviewVoList;
    }





}
