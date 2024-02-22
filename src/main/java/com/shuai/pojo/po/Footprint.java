package com.shuai.pojo.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: fengxin
 * @CreateTime: 2023-08-07  12:24
 * @Description: 足迹表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Footprint {

    // 主键（用户id + 时间）
    private String id;

    // 用户id
    private Long userId;

    // 浏览的帖子id
    private String postId;

    // 浏览的商品id
    private String goodId;

    // 浏览时间
    private String browseTime;

    // 是否是帖子
    private Integer post;

    // 是否是商品
    private Integer good;
}
