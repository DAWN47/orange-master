package com.shuai.service;

import com.shuai.util.Result;

import java.util.List;

/**
 * @Author: fengxin
 * @CreateTime: 2023-08-14  16:00
 * @Description: TODO
 */
public interface CartService {

    // 添加购物车
    Result addCart(String goodId, Integer goodNumber);

    // 获取购物车列表
    Result getCartList();

    // 修改指定商品在购物车中的数量
    Result updateGoodNumber(String goodId, Integer goodNumber);

    // 删除购物车中指定商品
    Result deleteCart(String goodId);

//    // 清空购物车
//    String deleteShoppingCarAll(Long userId);


}
