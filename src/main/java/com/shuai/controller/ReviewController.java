package com.shuai.controller;

import com.shuai.pojo.po.Review;
import com.shuai.pojo.vo.ReviewVo;
import com.shuai.service.ReviewService;
import com.shuai.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

/**
 * @Author: fengxin
 * @CreateTime: 2023-08-14  08:37
 * @Description: 商品评价管理器
 */
@Slf4j
@RestController
@Transactional
@RequestMapping("/review")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    /**
     * @description: 商品下发布评价
     * @author: fengxin
     * @date: 2023/8/14 9:49
     * @param: [review] = {goodId, star, content, parentId}
     * @return: 是否成功
     **/
    @PreAuthorize("hasAuthority('/review/add')")
    @PostMapping("/add")
    public Result makeReview(@RequestBody Review review) {
        log.info("传过来的 review ==>{}",review);
        return reviewService.makeReview(review);
    }

//    /**
//     * @description: 修改自己的评价
//     * @author: fengxin
//     * @date: 2023/8/11 10:37
//     * @param: [commentId]
//     * @return: com.shuai.util.Result
//     **/
//    @PutMapping("/update")
//    public Result updateComment(@RequestParam("commentId") String commentId) {
//        log.info("传过来的commentId==>{}",commentId);
//        return commentService.updateComment(commentId);
//    }

    /**
     * @description: 删除自己的评价
     * @author: fengxin
     * @date: 2023/8/14 9:40
     * @param: [reviewId]
     * @return: 是否删除成功
     **/
    @DeleteMapping("/delete")
    public Result deleteReview(@RequestBody ReviewVo reviewVo) {
        log.info("传过来的 reviewId ==>{}",reviewVo.getReviewId());
        return reviewService.deleteReview(reviewVo.getReviewId());
    }

    /**
     * @description: 查询指定商品下的评价列表（也可按好中差查询）
     * @author: fengxin
     * @date: 2023/8/14 11:18
     * @param: [goodId，starLevel]
     * @return: 评价列表 ,每一条数据：{}
     **/
    @GetMapping("/select")
    public Result getReviewList(@RequestParam("goodId") String goodId, @RequestParam(defaultValue = "",name = "starLevel") String starLevel) {
        log.info("传过来的==>{},{}",goodId,starLevel);
        ArrayList<ReviewVo> reviewList = reviewService.getReviewList(goodId, starLevel);
        return Result.success("评价列表",reviewList);
    }
}
