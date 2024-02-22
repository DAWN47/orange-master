package com.shuai.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: fengxin
 * @CreateTime: 2023-08-13  08:16
 * @Description: 商品信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoodVo {

    // 商品id：操作员id + 时间
    private String GoodId;

    // 商品名称
    private String goodName;

    // 商品描述
    private String goodDescription;

    // 商品单价
    private float goodPrice;

    // 商品首图
    private String goodFirstPicture;

    // 商品类型
    private String goodType;

    // 操作员id
    private Long operatorId;

    // 商品销量
    private Integer goodSales;

    // 上架时间
    private String createTime;

    // 收藏量
    private Integer collections;

    // 评价数
    private Integer reviews;

    // 商品图片集
    private List<String> images = new ArrayList<>();


}
