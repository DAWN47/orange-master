package com.shuai.util;

import lombok.Data;

import java.io.Serializable;

/*前后端数据协议*/
@Data
public class Result implements Serializable {

    // 响应给前端的数据是否成功
    private Boolean flag;

    // 响应信息
    private String message;

    // 响应数据
    private Object data;

    public Result(){}

    public Result(Boolean flag) {
        this.flag = flag;
    }

    public Result(Boolean flag, Object data) {
        this.data = data;
        this.flag = flag;
    }

    public Result(Boolean flag, String msg) {
        this.flag = flag;
        this.message = msg;
    }

    public Result(String msg) {
        this.flag = false;
        this.message = msg;
    }

    public Result(Boolean flag, String message, Object data) {
        this.flag = flag;
        this.message = message;
        this.data = data;
    }

    //响应成功的结果（有返回数据）
    public static Result success(String message,Object data){
        return new Result(true,message,data);
    }

    //响应成功结果（无返回数据）
    public static Result success(String message){
        return new Result(true,message,null);
    }

    //响应失败结果
    public static Result fail(String message){
        return  new Result(false, message);
    }


}
