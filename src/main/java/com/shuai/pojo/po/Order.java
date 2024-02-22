package com.shuai.pojo.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: fengxin
 * @CreateTime: 2023-08-04  12:14
 * @Description: 订单表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("`order`")
public class Order {

    // 主键（用户id + 时间）
    private String id;

    // 用户id
    private Long userId;

    // 收货信息
    private String addressInfo;

    // 订单号
    private String orderNo;

    // 订单状态
    private String orderStatus;

    // 订单总金额
    private float orderAmount;

    // 订单创建时间
    private String createTime;

    // 付款方式
    private String payMethod;

    // 订单付款时间
    private String payTime;

}
