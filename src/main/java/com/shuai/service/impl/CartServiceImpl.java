package com.shuai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shuai.handler.UserThreadLocal;
import com.shuai.mapper.CartMapper;
import com.shuai.mapper.GoodMapper;
import com.shuai.pojo.po.Cart;
import com.shuai.pojo.po.Good;
import com.shuai.pojo.vo.CartVo;
import com.shuai.service.CartService;
import com.shuai.util.Result;
import com.shuai.util.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Author: fengxin
 * @CreateTime: 2023-08-14  16:01
 * @Description: TODO
 */

@Service
@Transactional
public class CartServiceImpl implements CartService {

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private GoodMapper goodMapper;

    @Override
    public Result addCart(String goodId, Integer goodNumber) {
        Cart cart = new Cart();
        // 0. 拿到当前用户id
        Long userId = UserThreadLocal.get().getId();
        // 1. 构一条购物车信息
        cart.setId(userId + TimeUtil.getNowTimeString() + goodId);
        cart.setGoodId(goodId);
        cart.setGoodNumber(goodNumber);
        cart.setUserId(userId);
        cart.setAddTime(TimeUtil.getNowTime());
        // 2. 通过查询数据库，判断购物车内是否已有该商品
        Cart cartInfo = cartMapper.selectOne(new LambdaQueryWrapper<Cart>().eq(Cart::getGoodId, goodId).eq(Cart::getUserId,userId));
        // 2.1 判断购物车内是否已有该商品
        if (Objects.equals(cartInfo,null)) {
            // 2.1.1 购物车未包含该商品
            cartMapper.insert(cart);
        }else {
            // 2.1.2 购物车已经包含了该商品
            cartInfo.setGoodNumber(goodNumber + cartInfo.getGoodNumber());
            cartMapper.updateById(cartInfo);
        }
        return Result.success("添加购物车成功！");

    }

    @Override
    public Result getCartList() {
        // 0. 拿到当前用户id
        Long userId = UserThreadLocal.get().getId();
        // 1. 通过用户id查询购物车列表（单条数据包括：购物车id、商品id、商品数量、用户id、创建时间）
        List<Cart> cartList = cartMapper.selectList(new LambdaQueryWrapper<Cart>().eq(Cart::getUserId, userId));
        // 2. 通过商品id查询指定商品的id、名称、首图、单价
        List<CartVo> cartVos = new ArrayList<>();
        for (Cart cart : cartList) {
            // 2.1 拿到商品id
            String goodId = cart.getGoodId();
            // 2.2 通过商品id查询指定商品的id、名称、单价
            Good good = goodMapper.selectById(goodId);
            // 2.3 填充返回前端信息：商品id、商品名称、商品首图、商品单价、商品数量
            CartVo cartVo = new CartVo(good);
            cartVo.setGoodNumber(cart.getGoodNumber());
//            cartVos.add(cartVo);

            // 2.4 根据前端需要（方便数据处理），按照商品数量将一条数据分为“商品数量”条数据发送给前端
            int count = cartVo.getGoodNumber();
            cartVo.setGoodNumber(1);
            while (count > 1) {
                cartVos.add(cartVo);
                count--;
            }
            cartVos.add(cartVo);
        }
        return Result.success("我的购物车列表",cartVos);
    }

    @Override
    public Result updateGoodNumber(String goodId, Integer goodNumber) {
        // 0. 拿到当前用户id
        Long userId = UserThreadLocal.get().getId();
        // 1. 构建修改信息
        Cart cart = new Cart();
        cart.setGoodNumber(goodNumber);
        // 2. 修改
        cartMapper.update(cart,
                new LambdaQueryWrapper<Cart>()
                        .eq(Cart::getUserId,userId)
                        .eq(Cart::getGoodId,goodId));
        return Result.success("购物车中商品的数量修改成功");
    }

    @Override
    public Result deleteCart(String goodId) {
        // 0. 拿到当前用户id
        Long userId = UserThreadLocal.get().getId();
        // 1. 构建条件，删除指定购物车商品
        cartMapper.delete(new LambdaQueryWrapper<Cart>()
                .eq(Cart::getUserId,userId)
                .eq(Cart::getGoodId,goodId));
        return Result.success("删除购物车指定商品成功");
    }
}
