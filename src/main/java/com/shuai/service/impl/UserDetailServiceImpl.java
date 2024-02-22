package com.shuai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shuai.mapper.MenuMapper;
import com.shuai.mapper.UserMapper;
import com.shuai.pojo.po.User;
import com.shuai.pojo.vo.UserVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: fengxin
 * @CreateTime: 2023-08-04  15:01
 * @Description: TODO
 */
@Service
public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MenuMapper menuMapper;

    /*
        1. 根据用户名去查询用户信息 以及 这个用户对应的权限信息
        2. 把对应的用户信息包括 权限信息 封装到 UsesDetails 对象中
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. 查询用户信息
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername,username);
        User user = userMapper.selectOne(queryWrapper);
        // 2. 判断该用户是否存在：
        // 2.1 如果没有查到该用户信息（即：user为null）则抛出异常
        if (user == null) {
            throw new RuntimeException("用户名或密码错误！");
        }
        // 2.2 查询到该用户：
        // 3. 赋值 userVo
        UserVo userVo = new UserVo();
        BeanUtils.copyProperties(user,userVo);

        // 4. 查询用户对应的权限信息
        List<String> list = menuMapper.getPermissions(user.getId());
        userVo.setPermissions(list);
        // 5. 返回
        return userVo;
    }
}
