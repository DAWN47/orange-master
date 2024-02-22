package com.shuai.pojo.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: fengxin
 * @CreateTime: 2023-09-05  17:24
 * @Description: 对应收货地址表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Address {

    // 收货地址id（用户id + 时间）
    private String id;

    // 用户id
    private Long userId;

    // 收货地区
    private String addressArea;

    // 详细地址
    private String addressDetail;

    // 收货人姓名
    private String addressName;

    // 收货人电话号码
    private String addressPhone;

    // 是否为默认地址
    private Integer addressDefault;

}
