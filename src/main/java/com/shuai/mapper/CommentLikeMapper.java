package com.shuai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shuai.pojo.po.CommentLike;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Author: fengxin
 * @CreateTime: 2023-08-11  11:37
 * @Description: 访问 comment_like 表
 */
@Mapper
public interface CommentLikeMapper extends BaseMapper<CommentLike> {

    // 通过 postId 删除评论点赞记录
    void deleteByPostId(@Param("postId") String postId);

    // 通过父评论id，删除父评论和子评论的点赞记录
    void deleteByCommentId(String commentId);
}
