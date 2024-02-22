package com.shuai.service.impl;

import com.shuai.service.SearchHistoryService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class SearchHistoryServiceImpl implements SearchHistoryService {

    @Resource(name = "SearchRedisTemplate")
    private RedisTemplate<String,String> redisTemplate;

    @Value("${project.searchSize}")
    private Long searchSize;

    @Override
    public Boolean addSearchHistory(String userKey, String keyword) {
        // 1. 在列表左侧（首部）添加
        // 1.1 先判断是否已有此搜索记录
        if (isValueExists(userKey, keyword)) {
            // 有此记录：
            redisTemplate.opsForList().remove(userKey,0,keyword);
        }
        // 1.2 在列表左侧（首部）添加
        redisTemplate.opsForList().leftPush(userKey, keyword);
        // 2. 限制搜索记录的数量为 searchSize
        redisTemplate.opsForList().trim(userKey, 0, searchSize - 1);
        return true;
    }

    @Override
    public List<String> getSearchHistory(String userKey) {
        return redisTemplate.opsForList().range(userKey, 0, -1);
    }

    @Override
    public Boolean clearSearchHistory(String userKey) {
        return redisTemplate.delete(userKey);
    }

    @Override
    public Boolean deleteSearchHistory(String userKey, String keyword) {
        // 1. 删除指定的搜索记录
        // 1.1 先判断是否已有此搜索记录
        if (isValueExists(userKey, keyword)) {
            // 1.2 有此记录：删除
            redisTemplate.opsForList().remove(userKey,0,keyword);
            return true;
        }
        return false;
    }

    /* 判断指定 key 中是否包含 value */
    private boolean isValueExists(String key, String value) {
        List<String> elements = redisTemplate.opsForList().range(key, 0, -1);
        if (elements != null) {
            for (String element : elements) {
                if (element.equals(value)) {
                    return true;
                }
            }
        }
        return false;
    }
}