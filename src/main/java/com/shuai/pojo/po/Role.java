package com.shuai.pojo.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: fengxin
 * @CreateTime: 2023-09-14  11:19
 * @Description: 角色表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    // 自增主键（角色id）
    private Long id;

    // 角色名称
    private String roleName;

    // 角色权限字符串
    private String roleKey;

    // 角色状态（1正常 2停用）
    private int status;

    // 创建人id
    private Long createId;

    // 创建时间
    private String createTime;

    // 修订人id
    private Long updateId;

    // 修订时间
    private String updateTime;

    // 备注
    private String remark;

}
