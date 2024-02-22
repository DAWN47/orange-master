package com.shuai.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shuai.mapper.OrderDetailMapper;
import com.shuai.pojo.po.OrderDetail;
import com.shuai.service.OrderDetailService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Author: fengxin
 * @CreateTime: 2023-09-09  11:12
 * @Description: TODO
 */
@Service
@Transactional
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
