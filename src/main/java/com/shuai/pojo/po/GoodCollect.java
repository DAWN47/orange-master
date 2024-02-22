package com.shuai.pojo.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: fengxin
 * @CreateTime: 2023-08-13  19:21
 * @Description: 商品收藏表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoodCollect {

    // 用户id + 商品id
    private String id;

    // 用户 id
    private Long userId;

    // 商品 id
    private String goodId;

    // 逻辑删除(1：已删除；0：未删除)
    private Integer deleted;

    // 收藏时间
    private String collectTime;
}
