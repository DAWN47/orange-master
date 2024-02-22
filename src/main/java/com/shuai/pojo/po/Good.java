package com.shuai.pojo.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: fengxin
 * @CreateTime: 2023-08-04  12:14
 * @Description: 商品表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Good {

    // 商品id：操作员id + 时间
    private String id;

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



}
