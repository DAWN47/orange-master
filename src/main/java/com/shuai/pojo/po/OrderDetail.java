package com.shuai.pojo.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: fengxin
 * @CreateTime: 2023-09-09  10:01
 * @Description: 订单明细表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetail {

    // 主键（用户id + 时间 + index）
    private String id;

    // 商品id
    private String goodId;

    // 商品数量
    private Integer goodNumber;

    // 商品名称
    private String goodName;

    // 商品描述
    private String goodDescription;

    // 商品单价
    private float goodPrice;

    // 商品首图
    private String goodFirstPicture;

    // 单类商品总价
    private float goodAmount;

    // 订单id
    private String orderId;


}
