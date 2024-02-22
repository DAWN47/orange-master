package com.shuai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shuai.pojo.po.ReviewLike;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: fengxin
 * @CreateTime: 2023-08-11  15:56
 * @Description: TODO
 */
@Mapper
public interface ReviewLikeMapper extends BaseMapper<ReviewLike> {

    // 通过父评价id，删除父评价和子评价的点赞记录 
    void deleteByReviewId(String reviewId);

    // 通过 goodId 删除评价点赞记录
    void deleteByGoodId(String goodId);
}
