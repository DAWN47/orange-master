package com.shuai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shuai.pojo.po.Order;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: fengxin
 * @CreateTime: 2023-08-16  10:06
 * @Description: 访问 order 表
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {

}
