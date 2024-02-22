package com.shuai.service;

import com.shuai.pojo.po.GoodCollect;
import com.shuai.pojo.po.PostCollect;
import com.shuai.util.Result;

import java.util.List;

/**
 * @Author: fengxin
 * @CreateTime: 2023-08-11  16:18
 * @Description: 完成所有的收藏相关服务
 */
public interface CollectService {

    // 收藏指定帖子（二次点击取消收藏）
    Result collectPost(String postId);

    // 收藏指定商品（二次点击取消收藏）
    Result collectGood(String goodId);

    // 通过用户id查询帖子收藏表记录
    List<PostCollect> getPostCollect(Long userId);

    // 通过用户id查询商品收藏表记录
    List<GoodCollect> getGoodCollect(Long userId);

}
