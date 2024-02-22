package com.shuai.pojo.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: fengxin
 * @CreateTime: 2023-08-07  11:56
 * @Description: 购物车表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cart {

    // 购物车id（用户id + 时间 + 商品id）
    private String id;

    // 商品的id
    private String goodId;

    // 商品的数量
    private Integer goodNumber;

    // 所属用户的id
    private Long userId;

    // 加入购物车的时间
    private String addTime;

}
