package com.shuai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shuai.pojo.po.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Author: fengxin
 * @CreateTime: 2023-08-08  12:08
 * @Description: 对应数据库 comment 表
 */
@Mapper
public interface CommentMapper extends BaseMapper<Comment> {

    // 删除评论及其子评论
    int deleteComment(@Param("commentId") String commentId);
}
