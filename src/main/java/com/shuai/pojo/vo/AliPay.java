package com.shuai.pojo.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class AliPay implements Serializable {

    // 商品名称
    private String subject;

    // 商品编号
    private String traceNo;

    // 商品金额
    private String totalAmount;

}
