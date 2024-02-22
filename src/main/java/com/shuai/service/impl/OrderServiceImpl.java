package com.shuai.service.impl;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shuai.common.Constants;
import com.shuai.handler.UserThreadLocal;
import com.shuai.mapper.*;
import com.shuai.pojo.po.*;
import com.shuai.pojo.vo.OrderPaymentVO;
import com.shuai.pojo.vo.OrderVo;
import com.shuai.service.OrderService;
import com.shuai.util.Result;
import com.shuai.util.TimeUtil;
import com.shuai.util.WeChatPayUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Author: fengxin
 * @CreateTime: 2023-08-16  10:12
 * @Description: TODO
 */
@Service
@Transactional
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Autowired
    private AddressMapper addressMapper;

    @Autowired
    private GoodMapper goodMapper;

    @Autowired
    private WeChatPayUtil weChatPayUtil;

    @Autowired
    private UserMapper userMapper;

    @Override
    public Result orderList(Page<Order> page) {
        List<OrderVo> records = new ArrayList<>();
        // 0. 拿到当前用户id
        Long userId = UserThreadLocal.get().getId();
        // 1. 通过用户id查询 order 表
        Page<Order> orderPage = orderMapper.selectPage(page,
                new LambdaQueryWrapper<Order>()
                        .eq(Order::getUserId,userId));
        // 2. 通过订单id，查询订单明细表每一条订单对应的多个商品的商品标题、描述、单价、数量、首图、单商品总价
        for (Order order :orderPage.getRecords()) {
            // 2.1 查询数据库：拿到d商品信息
            List<OrderDetail> orderDetails = orderDetailMapper.selectList(
                    new LambdaQueryWrapper<OrderDetail>()
                            .eq(OrderDetail::getOrderId, order.getId()));
            // 2.2 order 赋值给 orderVo
            OrderVo orderVo = new OrderVo();
            BeanUtils.copyProperties(order,orderVo);
            orderVo.setOrderId(order.getId());
            // 2.3 orderDetails 赋值给 orderVo
            orderVo.setOrderDetails(orderDetails);
            // 3. 加入 records 中，返回前端
            records.add(orderVo);
        }
        Page<OrderVo> orderVoPage = new Page<>();
        // 4. 给返回值 orderVoPage 赋值
        orderVoPage.setRecords(records);
        BeanUtils.copyProperties(orderPage,orderVoPage,"records");
        return Result.success("我的订单列表（分页）",orderVoPage);
    }

    @Override
    public Result buy(OrderVo orderVo) {
        Order order = new Order();
        // 0. 拿到当前用户id
        Long userId = UserThreadLocal.get().getId();
        // 1. 通过地址id查询拼接收货信息
        Address address = addressMapper.selectById(orderVo.getAddressId());
        if (Objects.equals(address,null)) {
            return Result.fail("收货地址为空!");
        }
        order.setAddressInfo(address.getAddressName() + "，" + address.getAddressPhone() + "，" + address.getAddressArea() + address.getAddressDetail());
        // 2. 随机生成订单号（使用了 Hutool 工具包中的IdUtil类，调用IdUtil.getSnowflake().nextIdStr()可以生成一个Snowflake算法生成的唯一ID）
        String orderNo = IdUtil.getSnowflake().nextIdStr();
        order.setOrderNo(orderNo);
        // 3. 构造新增 order 并 新增到数据库
        order.setId(userId + TimeUtil.getNowTimeString());
        order.setUserId(userId);
        order.setOrderStatus(Constants.WAIT_PAY);
        order.setCreateTime(TimeUtil.getNowTime());
        orderMapper.insert(order);
        // 4. 通过 goodId 查询订单商品信息 并 计算出订单总金额
        Good good = new Good();
        float orderAmount = 0;
        int index = 0; // 防止主键重复
        for (OrderDetail detail : orderVo.getOrderDetails()) {
            if (Objects.equals(detail,null)) {
                return Result.fail("购买商品列表为空！");
            }
            String goodId = detail.getGoodId();
            good = goodMapper.selectById(goodId);
            // 5. 构建新增订单明细 orderDetail 并 新增到数据库
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(good,orderDetail);
            orderDetail.setId(userId + TimeUtil.getNowTimeString() + index++);
            orderDetail.setGoodId(goodId);
            orderDetail.setGoodNumber(detail.getGoodNumber());
            orderDetail.setGoodAmount(good.getGoodPrice() * detail.getGoodNumber());
            orderDetail.setOrderId(order.getId());
            orderDetailMapper.insert(orderDetail);
            orderAmount += orderDetail.getGoodAmount();
        }
        // 6. 更新订单信息：写入总金额
        order.setOrderAmount(orderAmount);
        orderMapper.updateById(order);
        // 7. 构建一个 url：前端可直接访问这个url，调用后端支付宝支付接口
        try {
            // 注意：中文需转化为 UTF-8 格式，不然调用此url拿不到中文参数
            String subjectParam = URLEncoder.encode("" + good.getGoodName() + good.getGoodDescription(), "UTF-8");
//            System.out.println("subjectParam = " + subjectParam);
            String payUrl = "http://localhost:5901/alipay/pay?subject=" + subjectParam + "&traceNo=" + orderNo + "&totalAmount=" + orderAmount;
            return Result.success("下单成功！请调用后端支付宝接口", payUrl);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return Result.fail("构造支付宝接口异常！");
    }

    @Override
    public OrderPaymentVO payment(OrderVo orderVo) throws Exception {
        // 1. 当前登录用户id
        Long userId = UserThreadLocal.get().getId();
        User user = userMapper.selectById(userId);

        // 2. 调用微信支付接口，生成预支付交易单
        JSONObject jsonObject = weChatPayUtil.pay(
                orderVo.getOrderNo(), //商户订单号
                new BigDecimal(0.01), //支付金额，单位 元
                "橘手之劳商品", //商品描述
                user.getOpenid() //微信用户的openid
        );

        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
            throw new Exception("该订单已支付");
        }

        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        vo.setPackageStr(jsonObject.getString("package"));

        return vo;
    }


    @Override
    public void paySuccess(String outTradeNo) {
        // 1. 根据订单号查询订单
        Order order = orderMapper.selectOne(new LambdaQueryWrapper<Order>().eq(Order::getOrderNo,outTradeNo));
        // 2. 根据订单id更新订单的状态、支付方式、结账时间
        order.setOrderStatus(Constants.COMPLETE_PAY);
        order.setPayMethod(Constants.WECHAT_PAY);
        order.setPayTime(TimeUtil.getNowTime());
        orderMapper.updateById(order);
    }


}
