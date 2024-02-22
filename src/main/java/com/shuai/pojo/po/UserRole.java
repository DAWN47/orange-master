package com.shuai.pojo.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: fengxin
 * @CreateTime: 2023-09-14  11:21
 * @Description: user 和 role 关系表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRole {

    // 用户id
    private Long userId;

    // 角色id
    private Long roleId;

}
