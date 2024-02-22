package com.shuai.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: fengxin
 * @CreateTime: 2023-09-08  00:16
 * @Description: 用于接收搜索历史的前端参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchVo {

    // 关键词
    private String keyword;

    // 类型：商城 or 论坛
    private String type = "商城";

}
