package com.shuai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shuai.handler.UserThreadLocal;
import com.shuai.mapper.ConcernMapper;
import com.shuai.pojo.bo.UserBo;
import com.shuai.pojo.po.Concern;
import com.shuai.service.ConcernService;
import com.shuai.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * @Author: fengxin
 * @CreateTime: 2023-08-09  09:38
 * @Description: TODO
 */
@Slf4j
@Service
@Transactional
public class ConcernServiceImpl implements ConcernService {

    @Autowired
    private ConcernMapper concernMapper;

    @Override
    public Result getConcernList() {
        // 0. 拿到当前用户id
        Long userId = UserThreadLocal.get().getId();
        // 1. 查询我的关注的人的头像昵称等信息
        List<UserBo> myConcern = concernMapper.getMyConcern(userId);
        // 2. 查询此人是否已关注自己
        for (UserBo userBo :myConcern) {
            Concern concern = concernMapper.selectOne(
                    new LambdaQueryWrapper<Concern>()
                            .eq(Concern::getUserId, userBo.getOtherId())
                            .eq(Concern::getConcernedId, userId)
                            .eq(Concern::getDeleted,0));
            // 3. 是否关注信息存入集合
            userBo.setConcerned(!Objects.equals(concern, null));
        }
        return Result.success("我的关注列表",myConcern);
    }

    @Override
    public Result getFansList() {
        // 0. 拿到当前用户id
        Long userId = UserThreadLocal.get().getId();
        // 1. 查询我的粉丝的人的头像昵称等信息
        List<UserBo> myFans = concernMapper.getMyFans(userId);
        // 2. 查询自己是否已关注此人
        for (UserBo userBo :myFans) {
            Concern concern = concernMapper.selectOne(
                    new LambdaQueryWrapper<Concern>()
                            .eq(Concern::getUserId, userId)
                            .eq(Concern::getConcernedId, userBo.getOtherId())
                            .eq(Concern::getDeleted,0));
            // 3. 是否关注信息存入集合
            userBo.setConcerned(!Objects.equals(concern, null));
        }
        return Result.success("我的粉丝列表",myFans);
    }

}
