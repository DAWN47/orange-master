package com.shuai.util;

import com.shuai.pojo.vo.UserVo;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author: fengxin
 * @CreateTime: 2023-09-14  10:34
 * @Description: 自定义权限管理（待补）
 */
@Component("authority")
public class AuthorityUtil {

    public boolean hasAuthority(String authority){
        //获取当前用户的权限
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserVo userVo = (UserVo) authentication.getPrincipal();
        List<String> permissions = userVo.getPermissions();
        //判断用户权限集合中是否存在authority
        return permissions.contains(authority);
    }
}
