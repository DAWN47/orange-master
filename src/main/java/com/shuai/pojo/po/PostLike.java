package com.shuai.pojo.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @Author: fengxin
 * @CreateTime: 2023-08-07  11:08
 * @Description: 帖子点赞表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostLike {

    // 用户id + 帖子id
    private String id;

    // 用户 id
    private Long userId;

    // 帖子 id
    private String postId;

    // 逻辑删除(1：已删除；0：未删除)
    private Integer deleted;

    // 点赞时间
    private String likeTime;

}
