package com.shuai.pojo.bo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * @Author: fengxin
 * @CreateTime: 2023-08-09  10:59
 * @Description: 列表关注的简单信息
 */
@Data
public class UserBo {

    // 此人的id
    private Long otherId;

    // 昵称
    private String nickname;

    // 头像
    private String avatar;

    // 性别
    private String sex;

    //个性签名
    private String description;

    // 对方对自己的关注情况
    private boolean concerned;

}
