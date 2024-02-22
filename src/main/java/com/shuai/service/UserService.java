package com.shuai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shuai.pojo.bo.WxAuth;
import com.shuai.pojo.po.User;
import com.shuai.pojo.vo.UserVo;
import com.shuai.util.Result;

/**
 * @Author: fengxin
 * @CreateTime: 2023-04-19  19:01
 * @Description: 用户服务类接口
 */
public interface UserService extends IService<User> {

    /*
     * @description: 通过用户名+密码登录
     * @author: fengxin
     * @date: 2023/4/20 10:39
     * @param: [uservo]
     * @return: com.improve.shell.util.Result
     **/
    @Deprecated/* 标识：已经弃用 */
    Result loginByUser(UserVo uservo);

    // 账号密码登录（新）
    Result accountLogin(UserVo uservo);

    Result adminLogin(UserVo userVo);

    /*
     * 注册用户名
     * @param: [uservo]
     * @return:
     **/
    Result register(UserVo uservo);

    // 获取sessionId
    Result getSessionId(String code);

    // 微信用户登录
    Result authLogin(WxAuth wxAuth);

    // 获取用户信息(刷新token)
    Result userinfo(boolean refresh);

    // 用户登出
    Result logout();

    // 修改个人信息
    Result updateInfo(User user);

    Result banById(Long id);

    Result unlockById(Long target);

    Result userToBusiness(Long id);

    Result businessToUser(Long id);

    Result userToExpert(Long id);

    Result expertToUser(Long id);

    Result userToAdmin(Long id);

    Result adminToUser(Long id);

    Result banUserPost(Long id);

    Result unlockUserPost(Long id);
}
