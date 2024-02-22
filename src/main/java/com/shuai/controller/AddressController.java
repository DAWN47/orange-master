package com.shuai.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shuai.handler.UserThreadLocal;
import com.shuai.pojo.po.Address;
import com.shuai.service.AddressService;
import com.shuai.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: fengxin
 * @CreateTime: 2023-09-08  18:47
 * @Description: 收货地址管理器
 */
@Slf4j
@RestController
@Transactional
@RequestMapping("/address")
public class AddressController {

    @Autowired
    private AddressService addressService;

    /**
     * @description: 查询收货地址信息列表
     * @author: fengxin
     * @date: 2023/9/8 18:46
     * @param: []
     * @return: 收货地址列表
     **/
    @GetMapping("/list")
    public Result addressList() {
        // 0. 拿到当前用户id
        Long userId = UserThreadLocal.get().getId();
        // 1. 查询地址信息列表
        List<Address> list = addressService.list(new LambdaQueryWrapper<Address>().eq(Address::getUserId, userId));
        return Result.success("收货地址列表",list);
    }

    /**
     * @description: 新增收货地址
     * @author: fengxin
     * @date: 2023/9/8 19:10
     * @param: [address]
     * @return: 是否成功
     **/
    @PostMapping("/add")
    public Result addressAdd(@RequestBody Address address) {
        return addressService.addressAdd(address);
    }

    /**
     * @description: 修改收货地址
     * @author: fengxin
     * @date: 2023/9/8 19:14
     * @param: [address]
     * @return: 是否成功
     **/
    @PutMapping("/update")
    public Result addressUpdate(@RequestBody Address address) {
        if (addressService.updateById(address)) {
            return Result.success("收货地址修改成功!");
        }else {
            return Result.fail("收货地址修改出错，联系后台！");
        }
    }

    /**
     * @description: 删除收货地址
     * @author: fengxin
     * @date: 2023/9/8 19:16
     * @param: [address] = {id}
     * @return: 是否成功
     **/
    @DeleteMapping("/delete")
    public Result addressDelete(@RequestBody Address address) {
        if (addressService.removeById(address.getId())) {
            return Result.success("收货地址删除成功!");
        }else {
            return Result.fail("收货地址删除出错，联系后台！");
        }
    }

    /**
     * @description: 设置默认收货地址
     * @author: fengxin
     * @date: 2023/9/8 19:33
     * @param: [address]
     * @return: 是否成功
     **/
    @PutMapping("/setDefault")
    public Result setDefaultAddress(@RequestBody Address address) {
        return addressService.setDefaultAddress(address.getId());
    }

    /**
     * @description: 查询默认收货地址
     * @author: fengxin
     * @date: 2023/9/8 19:36
     * @param: []
     * @return: 默认收货地址
     **/
    @GetMapping("/default")
    public Result defaultAddress() {
        // 0. 拿到当前用户id
        Long userId = UserThreadLocal.get().getId();
        // 1. 查询默认收货地址信息
        Address address = addressService.getOne(
                new LambdaQueryWrapper<Address>()
                        .eq(Address::getUserId, userId)
                        .eq(Address::getAddressDefault, 1));
        return Result.success("默认收货地址",address);
    }
}
