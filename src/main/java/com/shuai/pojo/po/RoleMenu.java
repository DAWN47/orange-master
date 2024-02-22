package com.shuai.pojo.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: fengxin
 * @CreateTime: 2023-09-14  11:21
 * @Description: role 和 menu 关系表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleMenu {

    // 角色id
    private Long roleId;

    // 菜单id
    private Long menuId;

}
