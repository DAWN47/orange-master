package com.shuai.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shuai.pojo.po.Order;
import com.shuai.pojo.vo.OrderPaymentVO;
import com.shuai.pojo.vo.OrderVo;
import com.shuai.util.Result;

/**
 * @Author: fengxin
 * @CreateTime: 2023-08-16  10:12
 * @Description: TODO
 */
public interface OrderService extends IService<Order> {

    // 获取我的订单列表
    Result orderList(Page<Order> page);

    // 下单：购买商品
    Result buy(OrderVo orderVo);

    // 订单支付
    OrderPaymentVO payment(OrderVo orderVo) throws Exception;

    // 支付成功，修改订单状态
    void paySuccess(String outTradeNo);

}
