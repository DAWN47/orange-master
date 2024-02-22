package com.shuai.service;

import java.util.List;

/**
 * @Author: fengxin
 * @CreateTime: 2023-08-23  11:26
 * @Description: TODO
 */
public interface SearchHistoryService {

    // 添加搜索历史记录
    Boolean addSearchHistory(String userKey, String keyword);

    // 获取搜索记录列表
    List<String> getSearchHistory(String userKey);

    // 清空搜索历史记录
    Boolean clearSearchHistory(String userKey);

    // 删除指定的搜索历史记录
    Boolean deleteSearchHistory(String userKey, String keyword);
}
