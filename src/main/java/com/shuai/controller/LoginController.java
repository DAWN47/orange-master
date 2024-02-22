package com.shuai.controller;

import com.shuai.pojo.bo.WxAuth;
import com.shuai.pojo.vo.UserVo;
import com.shuai.service.UserRoleService;
import com.shuai.service.UserService;
import com.shuai.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: fengxin
 * @CreateTime: 2023-04-19  18:51
 * @Description: 处理登录注册
 */
@Slf4j
@RestController
@Transactional
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRoleService userRoleService;

    /*
     * @description: 通过用户名+密码登录
     * @author: fengxin
     * @date: 2023/4/20 10:39
     * @param: [uservo]：{username,password}
     * @return: com.improve.shell.util.Result
     **/
    @Deprecated// 标识：已经弃用
    @PostMapping("/loginByUser")
    public Result login(@RequestBody UserVo uservo){
        log.info("传过来的uservo==>{}",uservo);
        return userService.loginByUser(uservo);
    }

    /**
     * @description: 账号密码登录（新）
     * @author: fengxin
     * @date: 2023/9/13 21:28
     * @param: [uservo]
     * @return: com.shuai.util.Result
     **/
    @PostMapping("/accountLogin")
    public Result accountLogin(@RequestBody UserVo uservo){
//        log.info("传过来的uservo==>{}",uservo);
        return userService.accountLogin(uservo);
    }





    /* 注册用户名+并设置密码
     * @description:
     * @author: fengxin
     * @date: 2023/4/20 10:38
     * @param: [uservo]：{username,password}
     * @return: com.improve.shell.util.Result
     **/
    @PostMapping("/registerUser")
    public Result registerUser(@RequestBody UserVo uservo){
        log.info("传过来的uservo==>{}",uservo);
        return userService.register(uservo);
    }

    // --------------------下面进行微信登录接口----------------

    /**
     * 拿到 code生成一个 sessionId返回
     * @param: [code]
     * @return: sessionId
     **/
    @GetMapping("/getSessionId")
    public Result getSessionId(@RequestParam String code){
        log.info("接收到code==>{}",code);
        return userService.getSessionId(code);
    }

    /**
     * 通过微信解密参数 对用户信息进行解密  生成 token
     * @param: [wxAuth]
     * @return: 用户微信基本信息 + token
     **/
    @PostMapping("/authLogin")
    public Result authLogin(@RequestBody WxAuth wxAuth){
        log.info("获得三个参数WxAuth==>{}",wxAuth);
        Result result = userService.authLogin(wxAuth);
        log.info("{}",result);
        return result;
    }

    /**
     * 刷新 token，获取用户基本信息
     * @param refresh
     * @return 用户微信基本信息 + token
     */
    @GetMapping("/userInfo")
    public Result userinfo(@RequestParam("refresh") boolean refresh){
        return userService.userinfo(refresh);
    }

    /**
     * @description: 退出登录
     * @author: fengxin
     * @date: 2023/8/6 19:04
     * @param:
     * @return: 是否成功
     **/
    @DeleteMapping("/logout")
    public Result logout() {
        return userService.logout();
    }


}
