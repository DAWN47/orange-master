package com.shuai.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shuai.common.RedisKey;
import com.shuai.handler.UserThreadLocal;
import com.shuai.pojo.po.Footprint;
import com.shuai.pojo.po.Post;
import com.shuai.pojo.vo.PostVo;
import com.shuai.service.CommentService;
import com.shuai.service.FootprintService;
import com.shuai.service.PostService;
import com.shuai.service.SearchHistoryService;
import com.shuai.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * @Author: fengxin
 * @CreateTime: 2023-08-07  14:53
 * @Description: 帖子（论坛）管理
 */
@Slf4j
@RestController
@Transactional
@RequestMapping("/post")
public class PostController {

    @Value("${project.pageSize}")
    private String pageSize;

    @Autowired
    private PostService postService;

    @Autowired
    private FootprintService footprintService;

    @Autowired
    private CommentService commentService;
    
    @Autowired
    private SearchHistoryService searchHistoryService;


    /**
     * @description: 发布帖子
     * @author: fengxin
     * @date: 2023/8/8 9:14
     * @param: [postVo] = {postTitle, postContent, postType, images }
     * @return: 是否成功
     **/
    @PreAuthorize("hasAuthority('/post/add')")
    @PostMapping("/add")
    public Result add(@RequestBody PostVo postVo) {
        log.info("调用/post/add接口，传入：{}", postVo);
        return postService.add(postVo);
    }

    /**
     * @description: 删除指定帖子，及其相关数据
     * @author: fengxin
     * @date: 2023/8/12 12:47
     * @param: [postId]
     * @return: 是否成功
     **/
    @DeleteMapping("/delete")
    public Result delete(@RequestBody PostVo postVo) {
        log.info("传入：{}",postVo.getPostId());
        // 0. 拿到当前用户id
        Long userId = UserThreadLocal.get().getId();
        // 1. 通过 postId 查询数据库,拿到指定帖子信息
        Post post = postService.getById(postVo.getPostId());
        // 1.1 判断帖子是否存在
        if (Objects.equals(post,null)) {
            return Result.fail("删除失败，指定帖子不存在！");
        }
        // 1.2 检查是否有删除权限（只有自己可以删除）
        if (!Objects.equals(userId,post.getAuthorId())) {
            return Result.fail("非法删除！只可删除自己的帖子");
        }
        // 3. 删除指定帖子
        return postService.delete(postVo.getPostId());
    }

    /**
     * @description: 修改帖子
     * @author: fengxin
     * @date: 2023/8/8 10:44
     * @param: [postVo] = {postId, postTitle, postContent, postType, images }
     * @return: 是否成功
     **/
    @PreAuthorize("hasAuthority('/post/update')")
    @PutMapping("/update")
    public Result put(@RequestBody PostVo postVo) {
        log.info("调用/post/update接口，传入：{}", postVo);
        return postService.put(postVo);
    }


    /**
     * @description: 最热帖子列表（可传入关键词）
     * @author: fengxin
     * @date: 2023/8/8 9:06
     * @param: [title, current]
     * @return: 帖子列表
     **/
    @GetMapping("/hottestList")
    public Result hottestList(@RequestParam(defaultValue = "",name = "title")String title,
                              @RequestParam(defaultValue = "1", name = "current") Integer current) {
        log.info("current==>{}",current);
        // 1. 获取最热帖子列表
        IPage<PostVo> page = postService.hottestList(title, new Page<Post>(current, Long.parseLong(pageSize)));
        // 2. 如果 title 不为 ”“ ，存入搜索记录（redis）
        if (!Objects.equals(title,"")) {
            Long userId = UserThreadLocal.get().getId();
            Boolean aBoolean = searchHistoryService.addSearchHistory(RedisKey.POST_SEARCH + userId, title);
            if (!aBoolean) return Result.fail("Redis出现异常，联系后台");
        }
        return Result.success("最热帖子列表",page);
    }


    /**
     * @description: 最新帖子列表（可传入关键词）
     * @author: fengxin
     * @date: 2023/8/8 9:06
     * @param: [title, current]
     * @return: 帖子列表（包括列表长度）
     **/
    @GetMapping("/latestList")
    public Result latestList(@RequestParam(defaultValue = "",name = "title")String title,
                             @RequestParam(defaultValue = "1", name = "current") Integer current) {
        log.info("current==>{}",current);
        // 1. 获取最新帖子列表
        IPage<PostVo> page = postService.latestList(title, new Page<>(current, Long.parseLong(pageSize)));
        // 2. 如果 title 不为 ”“ ，存入搜索记录（redis）
        if (!Objects.equals(title,"")) {
            Long userId = UserThreadLocal.get().getId();
            Boolean aBoolean = searchHistoryService.addSearchHistory(RedisKey.POST_SEARCH + userId, title);
            if (!aBoolean) return Result.fail("Redis出现异常，联系后台");
        }
        return Result.success("最新帖子列表",page);
    }

    /**
     * @description: 获取指定帖子详情信息
     * @author: fengxin
     * @date: 2023/8/8 19:43
     * @param: [postId]
     * @return: 帖子的详情信息，和作者的关注情况，对该篇帖子的点赞、收藏情况
     **/
    @GetMapping("/details")
    public Result details(@RequestParam("postId") String postId) {
        log.info("postId==>{}",postId);
        // 1. 查询指定帖子详情信息（不包含评论信息）
        HashMap<String, Object> details = postService.details(postId);
//        // 2. 查询指定帖子的评论信息
//        ArrayList<CommentVo> commentList = commentService.getCommentList(postId);
//        // 3. 加入评论信息
//        details.put("commentList",commentList);
        return Result.success("获取帖子详情信息",details);
    }

    /**
     * @description: 获取我的足迹（帖子篇）
     * @author: fengxin
     * @date: 2023/8/9 15:32
     * @param: []
     * @return: 一个帖子信息列表
     **/
    @GetMapping("/getPostFootprint")
    public Result getPostFootprint() {
        ArrayList<PostVo> postVoList = new ArrayList<>();
        // 0. 拿到当前用户id
        Long userId = UserThreadLocal.get().getId();
        // 1. 通过用户id 查询帖子浏览记录
        List<Footprint> postFootprint = footprintService.list(
                new LambdaQueryWrapper<Footprint>()
                        .eq(Footprint::getUserId, userId)
                        .eq(Footprint::getPost, 1));
        // 2. 通过postId查询帖子的简要信息
        for (Footprint footprint :postFootprint) {
            PostVo postVo = postService.brief(footprint.getPostId());
            postVoList.add(postVo);
        }
        return Result.success("帖子浏览记录",postVoList);
    }




}
