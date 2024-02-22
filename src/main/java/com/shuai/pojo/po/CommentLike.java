package com.shuai.pojo.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: fengxin
 * @CreateTime: 2023-08-07  11:51
 * @Description: 评论点赞表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentLike {

    // 主键（用户id + 评论id）
    private String id;

    // 用户 id
    private Long userId;

    // 评论 id
    private String commentId;

    // 逻辑删除(1：已删除；0：未删除)
    private Integer deleted;

    // 点赞时间
    private String likeTime;
}
