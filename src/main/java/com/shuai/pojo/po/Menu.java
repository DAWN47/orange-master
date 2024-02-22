package com.shuai.pojo.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: fengxin
 * @CreateTime: 2023-09-14  11:14
 * @Description: 菜单（权限）表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Menu {

    // 自增主键（菜单id）
    private Long id;

    // 菜单（权限）名称
    private String menuName;

    // 菜单（权限）标识
    private String permission;

    // 菜单（权限）状态
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
