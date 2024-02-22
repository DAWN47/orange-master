package com.shuai.pojo.vo;

import com.shuai.pojo.po.Good;
import com.shuai.pojo.po.OrderDetail;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.FactoryBean;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderVo {

    // 主键（用户id + 时间）
    private String orderId;

    // 订单明细：商品名称 + 商品描述，商品单价，商品数量，单类商品总价，商品首图
    private List<OrderDetail> orderDetails;

    // 用户id
    private Long userId;

    // 收货地址id
    private String addressId;

    // 收货信息 = 收货人姓名, 收货人电话号码, 收货地区 + 详细地址
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
