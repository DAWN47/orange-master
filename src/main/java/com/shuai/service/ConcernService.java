package com.shuai.service;

import com.shuai.util.Result;

/**
 * @Author: fengxin
 * @CreateTime: 2023-08-09  09:37
 * @Description: TODO
 */
public interface ConcernService {

    // 获取我的关注列表
    Result getConcernList();

    // 获取我的粉丝列表
    Result getFansList();


}
