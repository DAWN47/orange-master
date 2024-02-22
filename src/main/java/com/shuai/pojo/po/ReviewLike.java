package com.shuai.pojo.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: fengxin
 * @CreateTime: 2023-08-11  15:51
 * @Description: 商品评价点赞表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewLike {

    // 用户id + 商品id
    private String id;

    // 用户 id
    private Long userId;

    // 商品 id
    private String reviewId;

    // 逻辑删除(1：已删除；0：未删除)
    private Integer deleted;

    // 点赞时间
    private String likeTime;
}
