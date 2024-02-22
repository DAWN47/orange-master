package com.shuai.handler;


import com.shuai.pojo.vo.UserVo;

public class UserThreadLocal {

    private static final ThreadLocal<UserVo> LOCAL = new ThreadLocal<>();

    public static void put(UserVo uservo){
        LOCAL.set(uservo);
    }

    public static UserVo get(){
        return LOCAL.get();
    }

    public static void remove(){
        LOCAL.remove();
    }
}
