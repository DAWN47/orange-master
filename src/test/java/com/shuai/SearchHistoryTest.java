package com.shuai;

import java.util.List;

import com.shuai.common.Constants;
import com.shuai.common.RedisKey;
import com.shuai.service.SearchHistoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @Author: fengxin
 * @CreateTime: 2023-08-23  11:02
 * @Description: TODO
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SearchHistoryTest {

    @Autowired
    private SearchHistoryService searchHistoryService;

//    @Autowired
//    private RedisTemplate<String,Object> redisTemplate;
//
//    public void addSearchHistory(String userId, String keyword) {
//        redisTemplate.opsForList().leftPush(userId, keyword);
//    }
//
//    public List<Object> getSearchHistory(String userId) {
//        List<Object> searchHistory = redisTemplate.opsForList().range(userId, 0, -1);
//        return searchHistory;
//    }
//
//    public void clearSearchHistory(String userId) {
//        redisTemplate.delete(userId);
//    }

    @Test
    public void main() {
        // 创建搜索历史管理器实例

        // 添加搜索历史记录
        long userId = 2L;
        searchHistoryService.addSearchHistory(RedisKey.GOOD_SEARCH + userId, "Java1");
//        searchHistoryService.addSearchHistory(RedisKey.GOOD_SEARCH + userId, "testDelete");
//        searchHistoryService.addSearchHistory(RedisKey.GOOD_SEARCH + userId, "Spring Boot1");
//        searchHistoryService.addSearchHistory(RedisKey.GOOD_SEARCH + userId, "Java3");
//        searchHistoryService.addSearchHistory(RedisKey.GOOD_SEARCH + userId, "Redis3");
//        searchHistoryService.addSearchHistory(RedisKey.GOOD_SEARCH + userId, "Spring Boot3");
//        searchHistoryService.addSearchHistory(RedisKey.GOOD_SEARCH + userId, "Java4");
//        searchHistoryService.addSearchHistory(RedisKey.GOOD_SEARCH + userId, "Redis4");
//        searchHistoryService.addSearchHistory(RedisKey.GOOD_SEARCH + userId, "Spring Boot4");

        // 获取搜索历史记录
        List<String> searchHistory = searchHistoryService.getSearchHistory(RedisKey.GOOD_SEARCH + userId);
        System.out.println("Search History for User " + userId + ":");
        for (Object keyword : searchHistory) {
            System.out.println(keyword);
        }

//        // 清除搜索历史记录
//        searchHistoryService.clearSearchHistory(userId);
//        System.out.println("Search History Cleared for User " + userId);
    }
}