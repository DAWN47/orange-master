package com.shuai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shuai.pojo.po.Address;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: fengxin
 * @CreateTime: 2023-09-08  18:41
 * @Description: 访问 address 表
 */
@Mapper
public interface AddressMapper extends BaseMapper<Address> {
}
