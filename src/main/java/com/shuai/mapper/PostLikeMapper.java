package com.shuai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shuai.pojo.po.PostLike;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Author: fengxin
 * @CreateTime: 2023-08-08  11:56
 * @Description: 访问 post_like 数据库
 */
@Mapper
public interface PostLikeMapper extends BaseMapper<PostLike> {


}
