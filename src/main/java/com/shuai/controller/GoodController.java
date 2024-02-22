package com.shuai.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shuai.common.RedisKey;
import com.shuai.handler.UserThreadLocal;
import com.shuai.pojo.po.Footprint;
import com.shuai.pojo.po.Good;
import com.shuai.pojo.vo.GoodVo;
import com.shuai.service.FootprintService;
import com.shuai.service.GoodService;
import com.shuai.service.SearchHistoryService;
import com.shuai.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * @Author: fengxin
 * @CreateTime: 2023-08-11  09:18
 * @Description: 商品（商城）管理器
 */
@Slf4j
@RestController
@Transactional
@RequestMapping("/good")
public class GoodController {

    @Value("${project.pageSize}")
    private String pageSize;

    @Autowired
    private GoodService goodService;

    @Autowired
    private FootprintService footprintService;

    @Autowired
    private SearchHistoryService searchHistoryService;


    /**
     * @description: 商城首页推荐
     * @author: fengxin
     * @date: 2023/8/23 18:35
     * @param: [current]
     * @return: 推荐列表
     **/
    @GetMapping("/recommendationList")
    public Result recommendationList(@RequestParam(defaultValue = "1", name = "current") Integer current) {
        // 0. 拿到当前用户id
        Long userId = UserThreadLocal.get().getId();
        // 1. 拿到当前用户的商城搜索记录
        List<String> searchHistory = searchHistoryService.getSearchHistory(RedisKey.GOOD_SEARCH + userId);
        // 2. 依据用户的搜索记录获取推荐列表
        IPage<Good> page =  goodService.recommendationList(searchHistory,new Page<Good>(current,Long.parseLong(pageSize)));
        return Result.success("商品推荐列表",page);
    }

    /**
     * @description: 商城搜索
     * @author: fengxin
     * @date: 2023/8/13 12:57
     * @param: [keyword, price, sales] 注：1：升序 / 0：默认 / -1：降序
     * @return: 商品列表
     **/
    @GetMapping("/search")
    public Result search(@RequestParam(defaultValue = "",name = "keyword")String keyword,
                         @RequestParam(defaultValue = "0" ,name = "price")Integer price,
                         @RequestParam(defaultValue = "0" ,name = "sales")Integer sales,
                         @RequestParam(defaultValue = "1", name = "current") Integer current) {
        // 1. 判断 keyword 是否为 ""
        if (Objects.equals(keyword,"")) {
            return Result.fail("搜索关键词不能为 空");
        }
        // 2. 获取搜索列表
        IPage<Good> page =  goodService.search(keyword, price, sales, new Page<Good>(current,Long.parseLong(pageSize)));
        // 3. 向添加 Redis 中添加搜索记录
        Boolean aBoolean = searchHistoryService.addSearchHistory(RedisKey.GOOD_SEARCH + UserThreadLocal.get().getId(), keyword);
        if (!aBoolean) {
            return Result.fail("Redis出现异常，联系后台");
        }
        // 4. 成功
        return Result.success("搜索商品列表",page);
    }

    /**
     * @description: 商城首页销量
     * @author: fengxin
     * @date: 2023/8/13 15:38
     * @param: [current]
     * @return: 商品列表
     **/
    @GetMapping("/sales")
    public Result sales(@RequestParam(defaultValue = "1", name = "current") Integer current) {
        // 依据商品的销售量获取商品列表
        IPage<Good> page =  goodService.sales(new Page<Good>(current,Long.parseLong(pageSize)));
        return Result.success("商城首页销量列表",page);
    }

    /**
     * @description: 获取指定商品的详情信息
     * @author: fengxin
     * @date: 2023/8/13 19:33
     * @param: [goodId]
     * @return: 商品详情
     **/
    @GetMapping("/details")
    public Result details(@RequestParam("goodId") String goodId) {
        log.info("goodId==>{}", goodId);
        return goodService.details(goodId);
    }

    /**
     * @description: 获取商品的浏览记录
     * @author: fengxin
     * @date: 2023/8/14 12:01
     * @param: []
     * @return: 商品的浏览记录
     **/
    @GetMapping("/getGoodFootprint")
    public Result getGoodFootprint() {
        // 0. 拿到当前用户id
        Long userId = UserThreadLocal.get().getId();
        // 1. 通过用户id 查询商品浏览记录
        List<Footprint> goodFootprint = footprintService.list(
                new LambdaQueryWrapper<Footprint>()
                        .eq(Footprint::getUserId, userId)
                        .eq(Footprint::getGood, 1));
        return Result.success("商品浏览记录",goodFootprint);
    }

    /**
     * @description: 操作员添加商品
     * @author: fengxin
     * @date: 2023/8/13 10:37
     * @param: [goodVo]
     * @return: 是否成功
     **/
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('/good/add')")
    public Result addGood(@RequestBody GoodVo goodVo) {
        log.info("传入：{}",goodVo);
        return goodService.add(goodVo);
    }

    /**
     * @description: 操作员修改上架商品
     * @author: fengxin
     * @date: 2023/8/13 10:44
     * @param: [goodVo]
     * @return: 是否成功
     **/
    @PutMapping("/update")
    @PreAuthorize("hasAuthority('/good/update')")
    public Result put(@RequestBody GoodVo goodVo) {
        log.info("传入：{}", goodVo);
        return goodService.put(goodVo);
    }


    /**
     * @description: 下架商品下架指定商品
     * @author: fengxin
     * @date: 2023/8/14 14:47
     * @param: [goodId]
     * @return: 是否成功
     **/
    @DeleteMapping("/delete")
    @PreAuthorize("hasAuthority('/good/delete')")
    public Result deleteGood(@RequestBody GoodVo goodVo) {
        log.info("传入：{}",goodVo);
        return goodService.deleteGood(goodVo.getGoodId());
    }
}
