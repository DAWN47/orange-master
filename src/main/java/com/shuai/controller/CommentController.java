package com.shuai.controller;

import com.shuai.pojo.po.Comment;
import com.shuai.pojo.vo.CommentVo;
import com.shuai.service.CommentService;
import com.shuai.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Objects;

/**
 * @Author: fengxin
 * @CreateTime: 2023-08-11  09:37
 * @Description: 帖子评论管理器
 */
@Slf4j
@RestController
@Transactional
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    /**
     * @description: 帖子下发布评论
     * @author: fengxin
     * @date: 2023/8/11 9:49
     * @param: [comment] = {postId, content, parentId}
     * @return: 是否成功
     **/
    //被禁言用户无法发布
    @PreAuthorize("hasAuthority('/comment/add')")
    @PostMapping("/add")
    public Result makeComment(@RequestBody Comment comment) {
        log.info("传过来的comment==>{}",comment);
        if (Objects.equals(comment.getContent(),null)) {
            return Result.fail("评论内容不可以为null");
        }
        return commentService.makeComment(comment);
    }

//    /**
//     * @description: 修改自己的评论
//     * @author: fengxin
//     * @date: 2023/8/11 10:37
//     * @param: [commentId]
//     * @return: com.shuai.util.Result
//     **/
//    @PutMapping("/update")
//    public Result updateComment(@RequestParam("commentId") String commentId) {
//        log.info("传过来的commentId==>{}",commentId);
//        return commentService.updateComment(commentId);
//    }

    /**
     * @description: 删除自己的评论
     * @author: fengxin
     * @date: 2023/8/11 10:40
     * @param: [commentId]
     * @return: 是否删除成功
     **/
    @DeleteMapping("/delete")
    public Result deleteComment(@RequestBody CommentVo commentVo) {
        log.info("传过来的commentId==>{}",commentVo.getCommentId());
        return commentService.deleteComment(commentVo.getCommentId());
    }

    /**
     * @description: 查询指定帖子下的评论列表
     * @author: fengxin
     * @date: 2023/8/11 11:18
     * @param: [postId]
     * @return: 评论列表 ,每一条数据：{}
     **/
    @GetMapping("/select")
    public Result getCommentList(@RequestParam("postId") String postId) {
        log.info("传过来的postId==>{}",postId);
        ArrayList<CommentVo> commentList = commentService.getCommentList(postId);
        return Result.success("评论列表",commentList);
    }
}
