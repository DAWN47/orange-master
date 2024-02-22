package com.shuai.pojo.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: fengxin
 * @CreateTime: 2023-08-07  12:03
 * @Description: 关注表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Concern {

    // 主键（关注人id + 时间 + 被关注人id）
    private String id;

    // 关注人id
    private Long userId;

    // 被关注人id
    private Long concernedId;

    // 逻辑删除(1：已删除；0：未删除)
    private Integer deleted;

    // 关注时间
    private String concernTime;

}
