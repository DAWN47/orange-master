package com.shuai.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shuai.handler.UserThreadLocal;
import com.shuai.mapper.UserMapper;
import com.shuai.pojo.po.*;
import com.shuai.pojo.vo.PostVo;
import com.shuai.service.*;
import com.shuai.service.impl.PostServiceImpl;
import com.shuai.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: fengxin
 * @CreateTime: 2023-08-08  19:31
 * @Description: ”我的“面板 接口
 */
@Slf4j
@Transactional
@RestController
@RequestMapping("/my")
public class MyController {

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @Autowired
    private GoodService goodService;

    @Autowired
    private ConcernService concernService;

    @Autowired
    private CollectService collectService;


    /**
     * @description: 获取我的帖子列表（传入他人id亦可访问他人帖子列表）
     * @author: fengxin
     * @date: 2023/8/9 8:38
     * @param: [otherId]
     * @return: 我的帖子列表信息（或他人）
     **/
    @GetMapping("/getPostList")
    public Result getMyPostList(@RequestParam(defaultValue = "0", name = "otherId")Long otherId) {
        return postService.getMyPostList(otherId);
    }

    /**
     * @description: 获取我的关注列表
     * @author: fengxin
     * @date: 2023/8/9 11:47
     * @param: []
     * @return: 一个集合{userId,nickname,avatar,description,concerned}
     * 注：concerned是对方是否关注自己
     **/
    @GetMapping("/getConcernList")
    public Result getConcernList() {
        return concernService.getConcernList();
    }

    /**
     * @description: 获取我的粉丝列表
     * @author: fengxin
     * @date: 2023/8/9 12:25
     * @param: []
     * @return: 一个集合{userId,nickname,avatar,description,concerned}
     * 注：concerned是自己是否关注对方
     **/
    @GetMapping("/getFansList")
    public Result getFansList() {
        return concernService.getFansList();
    }

    /**
     * @description: 获取我的帖子收藏列表
     * @author: fengxin
     * @date: 2023/8/14 21:08
     * @param: []
     * @return: 收藏帖子列表
     **/
    @GetMapping("/getPostCollect")
    public Result getPostCollect() {
        ArrayList<PostVo> postVoList = new ArrayList<>();
        // 0. 拿到当前用户id
        Long userId = UserThreadLocal.get().getId();
        // 1. 通过用户id查询帖子收藏的记录
        List<PostCollect> postCollects = collectService.getPostCollect(userId);
        // 2. 通过 postId 查询到帖子信息
        for (PostCollect postCollect :postCollects) {
            // 2.1 通过 postId 直接拿到帖子简略信息
            PostVo postVo = postService.brief(postCollect.getPostId());
            // 2.2 加入集合
            postVoList.add(postVo);
        }
        return Result.success("收藏帖子列表",postVoList);
    }

    /**
     * @description: 获取我的商品收藏列表
     * @author: fengxin
     * @date: 2023/8/14 21:09
     * @param: []
     * @return: 商品收藏列表
     **/
    @GetMapping("/getGoodCollect")
    public Result getGoodCollect() {
        ArrayList<Good> goodList = new ArrayList<>();
        // 0. 拿到当前用户id
        Long userId = UserThreadLocal.get().getId();
        // 1. 通过用户id查询商品收藏的记录
        List<GoodCollect> goodCollects = collectService.getGoodCollect(userId);
        // 2. 通过 goodId 查询到商品信息
        for (GoodCollect goodCollect :goodCollects) {
            // 2.1 通过 postId 直接拿到商品信息
            Good good = goodService.getById(goodCollect.getGoodId());
            // 2.2 加入集合
            goodList.add(good);
        }
        return Result.success("收藏商品列表",goodList);
    }


    /**
     * @description: 修改个人信息
     * @author: fengxin
     * @date: 2023/8/12 11:58
     * @param: [user] = {nickname, avatar, sex, phone, idCard, address, birthday, description}
     * @return: 是否成功
     **/
    @PutMapping("/updateInfo")
    public Result updateInfo(@RequestBody User user) {
        return userService.updateInfo(user);
    }

    /**
     * @description: 获取我的个人信息
     * @author: fengxin
     * @date: 2023/9/14 8:34
     * @param: []
     * @return: 我的个人信息
     **/
    @GetMapping("/info")
    public Result info() {
        // 0. 拿到当前用户id
        Long userId = UserThreadLocal.get().getId();
        // 1. 查询个人信息
        User userInfo = userService.getById(userId);
        return Result.success("获取我的个人信息",userInfo);
    }





}
