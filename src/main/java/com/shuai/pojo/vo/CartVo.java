package com.shuai.pojo.vo;

import com.shuai.pojo.po.Good;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: fengxin
 * @CreateTime: 2023-08-14  15:58
 * @Description: 返回给前端的购物车数据
 * */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartVo {

    // 商品id
    private String goodId;

    // 商品名称
    private String goodName;

    // 商品描述
    private String goodDescription;

    // 商品首图
    private String goodImage;

    // 商品单价
    private float goodPrice;

    // 商品数量
    private Integer goodNumber;

    public CartVo(Good good) {
        this.goodId = good.getId();
        this.goodName = good.getGoodName();
        this.goodDescription = good.getGoodDescription();
        this.goodImage = good.getGoodFirstPicture();
        this.goodPrice = good.getGoodPrice();
    }
}
