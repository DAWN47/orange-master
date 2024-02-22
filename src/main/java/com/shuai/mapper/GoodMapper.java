package com.shuai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shuai.pojo.po.Good;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: fengxin
 * @CreateTime: 2023-08-11  19:46
 * @Description: 访问 good 表
 */
@Mapper
public interface GoodMapper extends BaseMapper<Good> {

}
