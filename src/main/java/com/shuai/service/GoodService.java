package com.shuai.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shuai.pojo.po.Good;
import com.shuai.pojo.vo.GoodVo;
import com.shuai.util.Result;

import java.util.List;

/**
 * @Author: fengxin
 * @CreateTime: 2023-08-12  20:36
 * @Description: TODO
 */
public interface GoodService extends IService<Good> {

    // 上传商品
    Result add(GoodVo goodVo);

    // 修改上架信息
    Result put(GoodVo goodVo);

    // 推荐列表
    IPage<Good> recommendationList(List<String> searchHistory,Page<Good> page);

    // 商城搜索框搜索
    IPage<Good> search(String keyword, Integer price, Integer sales, Page<Good> page);

    // 获取商品的详情信息
    Result details(String goodId);

    // 下架商品
    Result deleteGood(String goodId);

    // 商城首页销量
    IPage<Good> sales(Page<Good> page);

    Result forceDeleteGood(String goodId);
}
