package com.shuai.pojo.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: fengxin
 * @CreateTime: 2023-08-07  12:14
 * @Description: 商品评价表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Review {


    // 商品评论 id（评论时间 + 用户id）
    private String id;

    // 商品 id
    private String goodId;

    // 星级
    private Integer star;

    // 评价等级
    private String starLevel;

    // 评论内容
    private String content;

    // 评论者id
    private Long userId;

    // 父评论id
    private String parentId;

    // 评论创建时间
    private String createTime;

}
