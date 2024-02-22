package com.shuai.pojo.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author: fengxin
 * @CreateTime: 2023-08-04  12:14
 * @Description: 对应用户表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {

    // 主键
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    // 用户名
    private String username;

    // 密码
    private String password;

    // 昵称
    private String nickname;

    // 头像
    private String avatar;

    // 性别
    private String sex;

    // 注册时间
    private String createTime;

    //微信用户唯一标识
    private String openid;

    // 联合id
    private String wxUnionId;

    // 角色身份
    private String role;

    // 账号状态
    private String status;

    // 电话号码
    private String phone;

    // 身份证号码
    private String idCard;

    //常住地
    private String address;

    //出生日期
    private String birthday;

    //个性签名
    private String description;

}
