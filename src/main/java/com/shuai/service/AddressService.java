package com.shuai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shuai.pojo.po.Address;
import com.shuai.util.Result;

/**
 * @Author: fengxin
 * @CreateTime: 2023-09-08  18:39
 * @Description: TODO
 */
public interface AddressService extends IService<Address> {

    // 新增收货地址
    Result addressAdd(Address address);

    // 设置默认收货地址
    Result setDefaultAddress(String addressId);
}
