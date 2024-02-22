package com.shuai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shuai.pojo.po.OrderDetail;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: fengxin
 * @CreateTime: 2023-09-09  10:28
 * @Description: 访问 order_detail 表  《订单明细表》
 */
@Mapper
public interface OrderDetailMapper extends BaseMapper<OrderDetail> {
}
