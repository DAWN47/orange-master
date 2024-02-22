package com.shuai.service.impl;

import com.shuai.mapper.UserRoleMapper;
import com.shuai.service.UserRoleService;
import com.shuai.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @projectName: orange-master
 * @package: com.shuai.service.impl
 * @className: UserRoleServiceImpl
 * @author: Eric
 * @description: TODO
 * @date: 2024/2/20 21:35
 * @version: 1.0
 */
@Service
@Transactional
public class UserRoleServiceImpl implements UserRoleService {

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Override
    public Result registerUser(Long id) {
        Integer integer = userRoleMapper.registerUser(id);
        if(integer==0){
            return Result.fail("注册角色失败");
        }
        return Result.success("注册角色成功");
    }
}
