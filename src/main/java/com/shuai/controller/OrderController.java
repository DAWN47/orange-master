package com.shuai.controller;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shuai.common.Constants;
import com.shuai.handler.UserThreadLocal;
import com.shuai.pojo.po.*;
import com.shuai.pojo.vo.OrderVo;
import com.shuai.service.*;
import com.shuai.util.Result;
import com.shuai.util.TimeUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Objects;

@RestController
@Transactional
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderDetailService orderDetailService;

    @Value("${project.pageSize}")
    private String pageSize;


    /**
     * @description: 获取我的订单列表（分页）
     * @author: fengxin
     * @date: 2023/8/20 16:14
     * @param: [current]
     * @return: 订单列表（分页）
     **/
    @GetMapping("/list")
    public Result getUserOrders(@RequestParam(defaultValue = "1", name = "current") Integer current) {
        return orderService.orderList(new Page<>(current, Long.parseLong(pageSize)));
    }

    /**
     * @description: 通过订单id查询订单详情
     * @author: fengxin
     * @date: 2023/8/20 16:17
     * @param: [orderId]
     * @return: 订单详情
     **/
    @GetMapping("/details")
    public Result getOrderById(@RequestParam("orderId") String orderId) {
        // 1. 通过订单id查询订单信息
        Order order = orderService.getById(orderId);
        if (Objects.equals(order,null)) {
            return Result.fail("指定订单不存在！");
        }
        // 2. 构建返回值 orderVo
        OrderVo orderVo = new OrderVo();
        BeanUtils.copyProperties(order, orderVo);
        orderVo.setOrderId(orderId);
        // 2.1 构建订单明细部分的信息
        List<OrderDetail> orderDetails = orderDetailService.list(
                new LambdaQueryWrapper<OrderDetail>()
                        .eq(OrderDetail::getOrderId, order.getId()));
        orderVo.setOrderDetails(orderDetails);
        return Result.success("订单的详细信息", orderVo);
    }


    /**
     * @description: 删除指定订单
     * @author: fengxin
     * @date: 2023/8/21 14:18
     * @param: [orderId]
     * @return: 是否成功
     **/
    @DeleteMapping("/delete")
    public Result deleteOrderById(@RequestParam("orderId") String orderId) {
        // 0. 拿到当前用户id
        Long userId = UserThreadLocal.get().getId();
        // 1. 查询改订单是否存在
        Order order = orderService.getById(orderId);
        if (Objects.equals(order, null)) {
            return Result.fail("指定订单不存在！");
        }
        // 2. 查询是否是该订单所有者（检测是否有删除权限）
        if (!Objects.equals(order.getUserId(), userId)) {
            return Result.fail("没有删除该订单的权限");
        }
        // 3. 删除订单
        boolean remove1 = orderDetailService.remove(
                new LambdaQueryWrapper<OrderDetail>()
                        .eq(OrderDetail::getOrderId, orderId));
        boolean remove2 = orderService.removeById(orderId);
        if (remove1 && remove2) {
            return Result.success("删除订单成功!");
        } else {
            return Result.fail("删除订单失败!请联系后台");
        }
    }

    /**
     * @description: 下单：购买指定商品
     * @author: fengxin
     * @date: 2023/8/21 14:35
     * @param: [orderVo] = {addressId, orderDetails = {[goodId, goodNumber],[goodId, goodNumber], ...}}
     * @return: 后端支付宝接口的 url（前端直接访问）
     **/
    @PostMapping("/buy")
    public Result buy(@RequestBody OrderVo orderVo) {
        // 构建一个 url：前端可直接访问这个url，调用后端支付宝支付接口
        return orderService.buy(orderVo);
    }




}