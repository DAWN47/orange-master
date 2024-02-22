package com.shuai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shuai.handler.UserThreadLocal;
import com.shuai.mapper.*;
import com.shuai.pojo.po.*;
import com.shuai.pojo.vo.GoodVo;
import com.shuai.service.CollectService;
import com.shuai.service.GoodService;
import com.shuai.util.Result;
import com.shuai.util.TimeUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * @Author: fengxin
 * @CreateTime: 2023-08-12  20:36
 * @Description: TODO
 */
@Service
@Transactional
public class GoodServiceImpl extends ServiceImpl<GoodMapper, Good> implements GoodService {

    @Autowired
    private GoodMapper goodMapper;

    @Autowired
    private GoodImageMapper goodImageMapper;

    @Autowired
    private GoodCollectMapper goodCollectMapper;

    @Autowired
    private FootprintMapper footprintMapper;

    @Autowired
    private ReviewLikeMapper reviewLikeMapper;

    @Autowired
    private ReviewMapper reviewMapper;

    @Autowired
    private CollectService collectService;


    @Override
    public Result add(GoodVo goodVo) {
        // 0. 拿到当前用户id
        Long userId = UserThreadLocal.get().getId();
        // 1. 从 goodVo 中提取出 good
        Good good = getGood(goodVo, userId);
        good.setGoodSales(0);
        good.setId(userId + TimeUtil.getNowTimeString());
        // 2. 向 good 表这插入数据
        goodMapper.insert(good);
        // 3. 向 good_image 数据库插入数据
        int i = 0;
        String goodId = good.getId();
        for (String url : goodVo.getImages()) {
            String id = goodId + i++;
            goodImageMapper.insert(new GoodImage(id, goodId, url));
        }
        return Result.success("商品上架成功");
    }

    @Override
    public Result put(GoodVo goodVo) {
        // 0. 拿到当前用户id
        Long userId = UserThreadLocal.get().getId();
        // 1. 查询要修改的上架商品信息（通过 goodId）
        String goodId = goodVo.getGoodId();
        Good goodInfo = goodMapper.selectById(goodId);
        // 2. 判断数据库中是否有该上架商品信息 判断是否有修改权限
        if (Objects.equals(goodInfo,null)) {
            return Result.fail("修改失败，指定上架商品不存在！");
        }
        // 2.1 拥有：赋值
        Good good = getGood(goodVo, userId);
        if (!Objects.equals(goodVo.getOperatorId(),good.getOperatorId())) {
            return Result.fail("修改失败，该用户没有修改权限！");
        }
        good.setId(goodInfo.getId());
        // 2.2 修改上架商品信息
        goodMapper.updateById(good);
        // 2.3 删除原上架商品图集信息
        goodImageMapper.delete(new LambdaQueryWrapper<GoodImage>().eq(GoodImage::getGoodId,goodId));
        // 2.4 新增新的图集信息
        int i = 0;
        for (String url : goodVo.getImages()) {
            String id = goodId + i++;
            goodImageMapper.insert(new GoodImage(id, goodId, url));
        }
        return Result.success("修改上架商品成功");
    }


    /* 从 goodVo 中提取出 good */
    private Good getGood(GoodVo goodVo,Long userId) {
        Good good = new Good();
        // 1. 赋值（goodName, goodDescription, goodPrice, goodType）
        BeanUtils.copyProperties(goodVo,good);
        // 2. 设置 goodFirstPicture operatorId createTime
        good.setGoodFirstPicture(goodVo.getImages().get(0));
        good.setOperatorId(userId);
        good.setCreateTime(TimeUtil.getNowTime());
        return good;
    }


    @Override
    public IPage<Good> recommendationList(List<String> searchHistory,Page<Good> page) {
        // 1. 分页查询商品列表(按关键词=》用户历史搜索记录  搜索)
        LambdaQueryWrapper<Good> queryWrapper = new LambdaQueryWrapper<>();
        // 2. 构建条件
        for (String keyword :searchHistory) {
            queryWrapper.like(Good::getGoodName,keyword).or().like(Good::getGoodDescription,keyword);
        }
        // 3. 查询数据库
        Page<Good> goodPage = goodMapper.selectPage(page, queryWrapper);
        // 4. 如果没有搜索到任何商品，则 => 所有商品列表
        if (Objects.equals(goodPage.getRecords(),null)) {
            goodPage = goodMapper.selectPage(page, null);
        }
        return goodPage;
    }

    @Override
    public IPage<Good> search(String keyword, Integer price, Integer sales, Page<Good> page) {
        // 1. 分页查询商品列表(按关键词搜索、价格、销量)
        LambdaQueryWrapper<Good> queryWrapper = new LambdaQueryWrapper<>();
        // 1.1 关键词
        queryWrapper.like(Good::getGoodName,keyword).or().like(Good::getGoodDescription,keyword);
        // 1.2 价格
        if (price == 1) {
            queryWrapper.orderByAsc(Good::getGoodPrice);
        }else if (price == -1) {
            queryWrapper.orderByDesc(Good::getGoodPrice);
        }
        // 1.3 销量
        else if (sales == -1) {
            queryWrapper.orderByDesc(Good::getGoodSales);
        }
        // 1.4 查询 并 返回
        return goodMapper.selectPage(page, queryWrapper);
    }

    @Override
    public IPage<Good> sales(Page<Good> page) {
        // 1. 分页查询商品列表(按销量)
        return goodMapper.selectPage(page, new LambdaQueryWrapper<Good>().orderByDesc(Good::getGoodSales));
    }



    @Override
    public Result details(String goodId) {
        GoodVo goodVo = new GoodVo();
        // 0. 拿到当前用户id
        Long userId = UserThreadLocal.get().getId();
        // 1.通过商品id查询指定商品
        Good good = goodMapper.selectById(goodId);
        if (Objects.equals(good,null)) {
            return Result.fail("指定 goodId 商品不存在！");
        }
        // 2.通过商品id查询该商品图片集,并打包进goodVo
        List<GoodImage> goodImages = goodImageMapper.selectList(
                new LambdaQueryWrapper<GoodImage>()
                        .eq(GoodImage::getGoodId, goodId));
        List<String> images = new ArrayList<>();
        for (GoodImage goodImage :goodImages) {
            images.add(goodImage.getGoodPicture());
        }
        // 3. 给 goodVo 赋值
        BeanUtils.copyProperties(good,goodVo);
        goodVo.setGoodId(goodId);
        goodVo.setImages(images);

        // 4. 查询对该篇帖子的收藏情况
        GoodCollect goodCollectInfo = goodCollectMapper.selectOne(
                new LambdaQueryWrapper<GoodCollect>()
                        .eq(GoodCollect::getUserId, userId)
                        .eq(GoodCollect::getGoodId, goodId)
                        .eq(GoodCollect::getDeleted, 0));
        boolean collect;
        collect = !Objects.equals(goodCollectInfo, null);
        // 8.拼接返回信息
        HashMap<String, Object> goodDetails = new HashMap<>();
        goodDetails.put("goodInfo",goodVo);
        goodDetails.put("collect",collect);

        // 9.将此次访问存入我的足迹（商品篇）
        String id = userId + TimeUtil.getNowTimeString();
        Footprint footprint = new Footprint(
                id,userId,null ,goodId,TimeUtil.getNowTime(),0,1);
        footprintMapper.insert(footprint);
        return  Result.success("获取商品详情信息", goodDetails);
    }

    @Override
    public Result deleteGood(String goodId) {
        // 0. 拿到当前用户id
        Long userId = UserThreadLocal.get().getId();
        // 1. 通过 goodId 查询数据库,拿到指定帖子信息
        Good good = goodMapper.selectById(goodId);
        // 1.1 判断商品是否存在
        if (Objects.equals(good,null)) {
            return Result.fail("下架失败，指定商品不存在！");
        }
        // 1.2 检查是否有删除权限（只有自己可以删除）
        if (!Objects.equals(userId,good.getOperatorId())) {
            return Result.fail("非法删除！只可下架自己的商品");
        }
        // 2.删除指定商品及其下的评价，删除收藏、图集
        // 2.1 删除该商品·下所有评价的点赞记录
        reviewLikeMapper.deleteByGoodId(goodId);
        // 2.2 删除该商品下所有的评价
        reviewMapper.delete(
                new LambdaQueryWrapper<Review>()
                        .eq(Review::getGoodId, goodId));
        // 2.4 删除该商品的所有收藏记录
        goodCollectMapper.delete(
                new LambdaQueryWrapper<GoodCollect>()
                        .eq(GoodCollect::getGoodId,goodId));
        // 2.5 删除该商品的图片集
        goodImageMapper.delete(
                new LambdaQueryWrapper<GoodImage>()
                        .eq(GoodImage::getGoodId,goodId));
        // 2.6 删除该帖子记录
        goodMapper.deleteById(goodId);
        return Result.success("指定商品已下架");
    }

    @Override
    public Result forceDeleteGood(String goodId) {
        // 1. 通过 goodId 查询数据库,拿到指定帖子信息
        Good good = goodMapper.selectById(goodId);
        // 1.1 判断商品是否存在
        if (Objects.equals(good,null)) {
            return Result.fail("下架失败，指定商品不存在！");
        }
        // 2.删除指定商品及其下的评价，删除收藏、图集
        // 2.1 删除该商品·下所有评价的点赞记录
        reviewLikeMapper.deleteByGoodId(goodId);
        // 2.2 删除该商品下所有的评价
        reviewMapper.delete(
                new LambdaQueryWrapper<Review>()
                        .eq(Review::getGoodId, goodId));
        // 2.4 删除该商品的所有收藏记录
        goodCollectMapper.delete(
                new LambdaQueryWrapper<GoodCollect>()
                        .eq(GoodCollect::getGoodId,goodId));
        // 2.5 删除该商品的图片集
        goodImageMapper.delete(
                new LambdaQueryWrapper<GoodImage>()
                        .eq(GoodImage::getGoodId,goodId));
        // 2.6 删除该帖子记录
        goodMapper.deleteById(goodId);
        return Result.success("指定商品已下架");
    }

}
