package com.shuai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shuai.common.Constants;
import com.shuai.controller.ChatEndpoint;
import com.shuai.handler.UserThreadLocal;
import com.shuai.mapper.CommentLikeMapper;
import com.shuai.mapper.CommentMapper;
import com.shuai.mapper.PostMapper;
import com.shuai.mapper.UserMapper;
import com.shuai.pojo.po.Inform;
import com.shuai.pojo.po.Comment;
import com.shuai.pojo.po.CommentLike;
import com.shuai.pojo.po.Post;
import com.shuai.pojo.po.User;
import com.shuai.pojo.vo.CommentVo;
import com.shuai.pojo.vo.InformVo;
import com.shuai.service.CommentService;
import com.shuai.util.Result;
import com.shuai.util.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Author: fengxin
 * @CreateTime: 2023-08-11  09:40
 * @Description: TODO
 */
@Slf4j
@Service
@Transactional
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private CommentLikeMapper commentLikeMapper;

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public Result makeComment(Comment comment) {
        /* 查询一下对应 post */
        Post post = postMapper.selectById(comment.getPostId());
        // 0. 拿到当前用户id，即评论者 再拿到昵称头像
        Long userId = UserThreadLocal.get().getId();
        User userInfo = userMapper.getBriefInfo(userId);
        // 1. 设置为评论者
        comment.setUserId(userId);
        // 2. 拼接评论id（评论时间+用户id）
        String commentId = TimeUtil.getNowTimeString() + userId;
        comment.setId(commentId);
        // 3. 拿到评论时间并设置
        comment.setCreateTime(TimeUtil.getNowTime());
        // 4. 向数据库插入数据
        int insert = commentMapper.insert(comment);
        if (insert > 0) {
            // 5. 判断 comment.parentId 是否为 null,进行通知
            String parentId = comment.getParentId();
            if (!Objects.equals(parentId,null)) {
                // 5.0 通过父评论id 查询到父评论
                Comment commentInfo = commentMapper.selectById(parentId);
                // 5.1 通知父评论的发布者，有人回复了他的评论
                // 5.1.1 组装信息(fromId, fromNickname, fromAvatar, toId, objectId, sonObjectId, firstImage, content)
                InformVo informVo = new InformVo(
                        userId,
                        userInfo.getNickname(),
                        userInfo.getAvatar(),
                        post.getAuthorId(),
                        comment.getPostId(),
                        commentInfo.getId(),
                        post.getPostFirstPicture(),
                        Constants.REPLY_COMMENT + comment.getContent(),
                        TimeUtil.getNowTime()
                );
                // 5.1.2 调用 websocket 发送消息
                ChatEndpoint.sendInfo(informVo);
            }
            // 5.2 通知帖子的发布者，有人评论了他的帖子
            // 5.1.1 组装信息(fromId, fromNickname, fromAvatar, toId, objectId, sonObjectId, firstImage, content)
            InformVo informVo = new InformVo(
                    userId,
                    userInfo.getNickname(),
                    userInfo.getAvatar(),
                    post.getAuthorId(),
                    comment.getPostId(),
                    commentId,
                    post.getPostFirstPicture(),
                    Constants.COMMENT_POST,
                    TimeUtil.getNowTime()
            );
            // 5.1.2 调用 websocket 发送消息
            ChatEndpoint.sendInfo(informVo);
            return Result.success("评论发布成功！");
        }else {
            return Result.fail("评论发布失败！请联系服务器");
        }
    }

    @Override
    public Result deleteComment(String commentId) {
        // 0. 拿到当前用户id
        Long userId = UserThreadLocal.get().getId();
        // 1. 通过commentId查询数据库此条记录
        Comment comment = commentMapper.selectById(commentId);
        // 2. 判断该评论是否存在，判断是否有删除权限（只有本人才能删除自己的评论）
        if (Objects.equals(comment,null)) {
            return Result.fail("删除失败！该评论不存在");
        }
        if (!Objects.equals(userId,comment.getUserId())) {
            return Result.fail("删除失败！只有自己才能删除自己的评论");
        }
        // 3. 删除该评论及其子评论的点赞记录
        commentLikeMapper.deleteByCommentId(commentId);
        // 3. 删除此条评论及其子评论
        int delete = commentMapper.deleteComment(commentId);
        if (delete > 0) {
            return Result.success("评论及其子评论删除成功！");
        }else {
            return Result.fail("评论删除失败！请联系服务器");
        }
    }

    @Override
    public ArrayList<CommentVo> getCommentList(String postId) {
        ArrayList<CommentVo> commentVoList = new ArrayList<>();
        // 0. 拿到当前用户id
        Long userId = UserThreadLocal.get().getId();
        // 1. 通过帖子id从数据库查询所有的评论
        List<Comment> commentList = commentMapper.selectList(
                new LambdaQueryWrapper<Comment>()
                        .eq(Comment::getPostId, postId));
        // 2. 标记自己的评论 并且 查询每条评论的点赞数
        for (Comment comment :commentList) {
            // 2.0 comment属性值赋值给commentVo
            CommentVo commentVo = new CommentVo();
            BeanUtils.copyProperties(comment,commentVo);
            commentVo.setCommentId(comment.getId());
            // 2.1 标记是否是自己的评论
            commentVo.setOwn(Objects.equals(comment.getUserId(), userId));
            // 2.2 通过commentId查询单条评论的点赞量，并记录
            Long likes = commentLikeMapper.selectCount(
                    new LambdaQueryWrapper<CommentLike>()
                            .eq(CommentLike::getCommentId, comment.getId())
                            .eq(CommentLike::getDeleted,0));
            commentVo.setLikes(likes);
            // 2.3 查询评论的发表人的userId，nickname，avatar
            User user = userMapper.getBriefInfo(comment.getUserId());
            commentVo.setNickname(user.getNickname());
            commentVo.setAvatar(user.getAvatar());
            // 2.4 拼接返回值，加入集合中
            commentVoList.add(commentVo);
        }
        return commentVoList;
    }
}
