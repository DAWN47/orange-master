package com.shuai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shuai.handler.UserThreadLocal;
import com.shuai.mapper.AddressMapper;
import com.shuai.pojo.po.Address;
import com.shuai.service.AddressService;
import com.shuai.util.Result;
import com.shuai.util.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Author: fengxin
 * @CreateTime: 2023-09-08  18:40
 * @Description: TODO
 */
@Service
public class AddressServiceImpl extends ServiceImpl<AddressMapper, Address> implements AddressService {

    @Autowired
    private AddressMapper addressMapper;

    @Override
    public Result addressAdd(Address address) {
        // 0. 拿到当前用户id
        Long userId = UserThreadLocal.get().getId();
        // 1. 判断：是否为第一个收货地址？
        List<Address> addressList = addressMapper.selectList(new LambdaQueryWrapper<Address>().eq(Address::getUserId, userId));
        if (Objects.equals(addressList,new ArrayList<Address>())) {
            // 1.1 是第一个收货地址，设置为默认地址
            address.setAddressDefault(1);
        }else {
            // 1.2 不是第一个收货地址
            address.setAddressDefault(0);
        }
        // 2. 赋值构建新增对象 address
        address.setId(userId + TimeUtil.getNowTimeString());
        address.setUserId(userId);
        // 3. 新增
        addressMapper.insert(address);
        return Result.success("新增收货地址成功！");
    }

    @Override
    public Result setDefaultAddress(String addressId) {
        // 0. 拿到当前用户id
        Long userId = UserThreadLocal.get().getId();
        // 1. 查询指定收货地址是否存在
        Address address = addressMapper.selectById(addressId);
        if (Objects.equals(address,null)) {
            return Result.fail("指定收货地址不存在！");
        }
        // 2. 查询当前用户的默认收货地址
        Address addressDefault = addressMapper.selectOne(
                new LambdaQueryWrapper<Address>()
                        .eq(Address::getUserId, userId)
                        .eq(Address::getAddressDefault, 1));
        // 3. 判断：当前默认地址和将设置的默认地址是否同一个?
        if (Objects.equals(addressDefault.getId(),address.getId())) {
            // 3.1 同一个：
            return Result.success("无需修改!");
        }
        // 3.2 不同：
        // 4. 修改默认地址
        addressDefault.setAddressDefault(0);
        address.setAddressDefault(1);
        // 5. 修改、存入数据库
        addressMapper.updateById(addressDefault);
        addressMapper.updateById(address);
        return Result.success("设置默认收货地址成功！");
    }


}
