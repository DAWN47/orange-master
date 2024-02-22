package com.shuai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shuai.pojo.po.Menu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Author: fengxin
 * @CreateTime: 2023-09-14  11:23
 * @Description: TODO
 */
@Mapper
public interface MenuMapper extends BaseMapper<Menu> {

    List<String> getPermissions(Long userId);

}
