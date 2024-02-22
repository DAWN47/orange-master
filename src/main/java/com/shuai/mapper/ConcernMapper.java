package com.shuai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shuai.pojo.bo.UserBo;
import com.shuai.pojo.po.Concern;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: fengxin
 * @CreateTime: 2023-08-08  20:15
 * @Description: 访问 concern 表 ，关注表
 */
@Mapper
public interface ConcernMapper extends BaseMapper<Concern> {

    // 查询我的关注的人的头像昵称等信息
    List<UserBo> getMyConcern(@Param("userId") Long userId);

    // 查询我的粉丝的人的头像昵称等信息
    List<UserBo> getMyFans(@Param("userId") Long userId);
}
