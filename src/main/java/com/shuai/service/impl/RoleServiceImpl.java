package com.shuai.service.impl;

import com.shuai.mapper.RoleMapper;
import com.shuai.service.RoleService;
import com.shuai.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RoleServiceImpl implements RoleService {

    @Autowired
    RoleMapper roleMapper;
    @Override
    public Result banRoleById(Long id) {
        if(roleMapper.banRoleById(id)==0){
            return Result.fail("角色封禁失败,该角色不存在或已被禁用！！！");
        }
        return Result.success("角色封禁成功");
    }

    @Override
    public Result unlockRoleById(Long id) {
        if(roleMapper.unlockRoleById(id)==0){
            return Result.fail("角色解禁失败,该角色不存在或状态正常！！！");
        }
        return Result.success("角色解禁成功");
    }
}
