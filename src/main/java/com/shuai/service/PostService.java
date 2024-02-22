package com.shuai.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shuai.pojo.po.Post;
import com.shuai.pojo.vo.PostVo;
import com.shuai.util.Result;

import java.util.HashMap;

public interface PostService extends IService<Post> {

    // 发布帖子
    Result add(PostVo postVo);

    // 修改帖子
    Result put(PostVo postVo);

    // 获取最热帖子列表
    IPage<PostVo> hottestList(String title, Page<Post> page);

    // 获取最新帖子列表
    IPage<PostVo> latestList(String title, Page<Post> page);

    // 获取指定帖子详情信息
    HashMap<String, Object> details(String postId);

    // 获取指定帖子的简要信息
    PostVo brief(String postId);

    // 获取我的帖子列表
    Result getMyPostList(Long otherId);

    // 删除指定帖子
    Result delete(String postId);
}
