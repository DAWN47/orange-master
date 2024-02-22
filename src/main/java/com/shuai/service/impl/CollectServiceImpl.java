package com.shuai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shuai.common.Constants;
import com.shuai.controller.ChatEndpoint;
import com.shuai.handler.UserThreadLocal;
import com.shuai.mapper.*;
import com.shuai.pojo.po.Inform;
import com.shuai.pojo.po.*;
import com.shuai.pojo.vo.InformVo;
import com.shuai.service.CollectService;
import com.shuai.util.Result;
import com.shuai.util.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * @Author: fengxin
 * @CreateTime: 2023-08-11  16:19
 * @Description: TODO
 */
@Service
@Transactional
public class CollectServiceImpl implements CollectService {

    @Autowired
    private PostCollectMapper postCollectMapper;

    @Autowired
    private GoodCollectMapper goodCollectMapper;

    @Autowired
    private GoodMapper goodMapper;

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public Result collectPost(String postId) {
        // 0. 拿到当前用户id 并查询到 用户信息
        Long userId = UserThreadLocal.get().getId();
        User userInfo = userMapper.getBriefInfo(userId);
        // 1. 通过 postId 查询对应帖子 并 确实帖子是否存在
        Post post = postMapper.selectById(postId);
        if (Objects.equals(post,null)) {
            return Result.fail("收藏失败！该帖子不存在");
        }
        // 2. 查询数据库是否已有收藏记录（deleted可能是1 or 0）
        PostCollect postCollectInfo = postCollectMapper.selectOne(
                new LambdaQueryWrapper<PostCollect>()
                        .eq(PostCollect::getUserId, userId)
                        .eq(PostCollect::getPostId, postId));
        // 3. 无记录：添加新记录，有记录：deleted为0则变1、为1则变0
        if (Objects.equals(postCollectInfo,null)) {
            // 3.1 收藏对应帖子（新增插入数据）
            String id = userId + postId;
            PostCollect postCollect =
                    new PostCollect(id,userId,postId,0, TimeUtil.getNowTime());
            int insert = postCollectMapper.insert(postCollect);
            if (insert > 0){
                // 4. 给帖子作者发送某人收藏了他的帖子 的 在线系统通知
                // 4.1 组装信息(fromId, fromNickname, fromAvatar, toId, objectId, sonObjectId, firstImage, content, informTime)
                InformVo informVo = new InformVo(
                        userId,
                        userInfo.getNickname(),
                        userInfo.getAvatar(),
                        post.getAuthorId(),
                        postId,
                        null,
                        post.getPostFirstPicture(),
                        Constants.COLLECT_POST,
                        TimeUtil.getNowTime()
                );
                // 4.2 调用 websocket 发送消息
                ChatEndpoint.sendInfo(informVo);
                return Result.success("收藏帖子成功！");
            }else {
                return Result.fail("收藏帖子失败，联系后台");
            }
        }else {
            // 3.2 修改记录，deleted为0则变1、为1则变0
            if(postCollectInfo.getDeleted() > 0) {
                postCollectInfo.setDeleted(0);
                // 注：需填入新的收藏时间
                postCollectInfo.setCollectTime(TimeUtil.getNowTime());
                postCollectMapper.updateById(postCollectInfo);
                return Result.success("收藏帖子成功！");
            }else {
                postCollectInfo.setDeleted(1);
                postCollectMapper.updateById(postCollectInfo);
                // 4. 给帖子作者发送某人收藏了他的帖子 的 在线系统通知
                // 4.1 组装信息(fromId, fromNickname, fromAvatar, toId, objectId, sonObjectId, firstImage, content, informTime)
                InformVo informVo = new InformVo(
                        userId,
                        userInfo.getNickname(),
                        userInfo.getAvatar(),
                        post.getAuthorId(),
                        postId,
                        null,
                        post.getPostFirstPicture(),
                        Constants.COLLECT_POST,
                        TimeUtil.getNowTime()
                );
                // 4.2 调用 websocket 发送消息
                ChatEndpoint.sendInfo(informVo);
                return Result.success("取消收藏帖子成功！");
            }
        }
    }

    @Override
    public Result collectGood(String goodId) {
        // 0. 拿到当前用户id、用户信息
        Long userId = UserThreadLocal.get().getId();
        User userInfo = userMapper.getBriefInfo(userId);
        // 1. 通过 goodId 查询对应商品 并 确实商品是否存在
        Good good = goodMapper.selectById(goodId);
        if (Objects.equals(good,null)) {
            return Result.fail("收藏失败！该商品不存在");
        }
        // 2. 查询数据库是否已有收藏记录（deleted可能是1 or 0）
        GoodCollect goodCollectInfo = goodCollectMapper.selectOne(
                new LambdaQueryWrapper<GoodCollect>()
                        .eq(GoodCollect::getUserId, userId)
                        .eq(GoodCollect::getGoodId, goodId));
        // 3. 无记录：添加新记录，有记录：deleted为0则变1、为1则变0
        if (Objects.equals(goodCollectInfo,null)) {
            // 3.1 收藏对应商品（新增插入数据）
            String id = userId + goodId;
            GoodCollect goodCollect =
                    new GoodCollect(id,userId,goodId,0, TimeUtil.getNowTime());
            int insert = goodCollectMapper.insert(goodCollect);
            if (insert > 0){
                // 4. 给商品上架人（商家）发送某人收藏了他的商品 的 在线系统通知
                // 4.1 组装信息(fromId, fromNickname, fromAvatar, toId, objectId, sonObjectId, firstImage, content, informTime)
//                ChatEndpoint.sendInfo(  按业务考虑要不要解开
//                        new InformVo(userId,userInfo.getNickname(),userInfo.getAvatar(),good.getOperatorId(),goodId,null,
//                                good.getGoodFirstPicture(), Constants.COLLECT_POST,TimeUtil.getNowTime()));
                return Result.success("收藏商品成功！");
            }else {
                return Result.fail("收藏商品失败，联系后台");
            }
        }else {
            // 3.2 修改记录，deleted为0则变1、为1则变0
            if(goodCollectInfo.getDeleted() > 0) {
                goodCollectInfo.setDeleted(0);
                // 注：需填入新的收藏时间
                goodCollectInfo.setCollectTime(TimeUtil.getNowTime());
                goodCollectMapper.updateById(goodCollectInfo);
                return Result.success("收藏商品成功！");
            }else {
                goodCollectInfo.setDeleted(1);
                goodCollectMapper.updateById(goodCollectInfo);
                // 4. 给商品上架人（商家）发送某人收藏了他的商品 的 在线系统通知
                // 4.1 组装信息(fromId, fromNickname, fromAvatar, toId, objectId, sonObjectId, firstImage, content, informTime)
//                ChatEndpoint.sendInfo(   按业务考虑要不要解开
//                        new InformVo(userId,userInfo.getNickname(),userInfo.getAvatar(),good.getOperatorId(),goodId,null,
//                                good.getGoodFirstPicture(), Constants.COLLECT_POST,TimeUtil.getNowTime()));
                return Result.success("取消收藏商品成功！");
            }
        }
    }

    @Override
    public List<PostCollect> getPostCollect(Long userId) {
        return postCollectMapper.selectList(
                new LambdaQueryWrapper<PostCollect>().eq(PostCollect::getUserId, userId));
    }

    @Override
    public List<GoodCollect> getGoodCollect(Long userId) {
        return goodCollectMapper.selectList(
                new LambdaQueryWrapper<GoodCollect>().eq(GoodCollect::getUserId, userId));
    }


}
