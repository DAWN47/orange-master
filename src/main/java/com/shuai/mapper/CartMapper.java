package com.shuai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shuai.pojo.po.Cart;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: fengxin
 * @CreateTime: 2023-08-14  15:59
 * @Description: 访问 cart 表
 */
@Mapper
public interface CartMapper extends BaseMapper<Cart> {

}
