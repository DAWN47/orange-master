package com.shuai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shuai.handler.UserThreadLocal;
import com.shuai.mapper.*;
import com.shuai.pojo.po.*;
import com.shuai.pojo.vo.PostVo;
import com.shuai.service.PostService;
import com.shuai.util.Result;
import com.shuai.util.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.*;

/**
 * @Author: fengxin
 * @CreateTime: 2023-08-07  15:53
 * @Description: TODO
 */
@Slf4j
@Service
@Transactional
public class PostServiceImpl extends ServiceImpl<PostMapper, Post> implements PostService {

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private PostImageMapper postImageMapper;

    @Autowired
    private PostLikeMapper postLikeMapper;

    @Autowired
    private PostCollectMapper postCollectMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private CommentLikeMapper commentLikeMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ConcernMapper concernMapper;

    @Autowired
    private FootprintMapper footprintMapper;

    @Override
    public Result add(PostVo postVo) {
        // 1. 拿到当前用户id
        Long userId = UserThreadLocal.get().getId();
        // 2. 拿到当前时间数串
        String nowTimeString = TimeUtil.getNowTimeString();
        // 3. 拼接 postId （主键：发帖人id + 发布时间）
        String postId = userId + nowTimeString;
        // 4. 赋值，拿到post
        Post post = getPost(postVo, userId, postId);
        // 5. 向post数据库插入数据
        postMapper.insert(post);
        // 6. 向post_image数据库插入数据
        int i = 0;
        for (String url : postVo.getImages()) {
            String id = postId + i++;
            postImageMapper.insert(new PostImage(id, postId, url));
        }
        return Result.success("发布成功");
    }

    @Override
    public Result put(PostVo postVo) {
        // 0. 拿到当前用户id
        Long userId = UserThreadLocal.get().getId();
        // 1. 查询要修改的帖子信息（通过postId）
        String postId = postVo.getPostId();
        Post postInfo = postMapper.selectById(postId);
        // 2. 判断数据库中是否有该帖子信息 判断是否有修改权限
        if (Objects.equals(postInfo,null)) {
            return Result.fail("修改失败，指定帖子不存在！");
        }
        // 2.1 拥有：赋值
        Post post = getPost(postVo, userId, postId);
        if (!Objects.equals(postVo.getAuthorId(),post.getAuthorId())) {
            return Result.fail("修改失败，该用户没有修改权限！");
        }
        // 2.2 修改帖子信息
        postMapper.updateById(post);
        // 2.3 删除原帖子图集信息
        postImageMapper.delete(new QueryWrapper<PostImage>().eq("post_id",postId));
        // 2.4 新增新的图集信息
        int i = 0;
        for (String url : postVo.getImages()) {
            String id = postId + i++;
            postImageMapper.insert(new PostImage(id, postId, url));
        }
        return Result.success("修改成功");
    }

    /* 赋值 post */
    private Post getPost(PostVo postVo, Long userId, String postId) {
        Post post = new Post();
        // 1. 赋值（postTitle, postContent, postType）
        BeanUtils.copyProperties(postVo,post);
        // 2. 继续赋值（postId，userId，postReleaseTime，postFirstPicture）
        post.setId(postId);
        post.setAuthorId(userId);
        post.setPostReleaseTime(TimeUtil.getNowTime());
        // 注意：判断图集是否为空
        if (!Objects.equals(postVo.getImages(),new ArrayList<>())) {
            post.setPostFirstPicture(postVo.getImages().get(0));
        }
        log.info("查看帖子数据post：{}",post);
        return post;
    }

    @Override
    public IPage<PostVo> hottestList(String title, Page<Post> page) {
        List<PostVo> records = new ArrayList<>();
        LambdaQueryWrapper<Post> queryWrapper = new LambdaQueryWrapper<>();
        // 0. 判断 title 是否为 null
        if (!Objects.equals(title,"")) {
            queryWrapper
                    .like(Post::getPostTitle,title)
                    .or().like(Post::getPostContent,title);
        }
        // 1. 分页查询帖子列表
        IPage<Post> postPage = postMapper.selectPage(page, queryWrapper);
        // 2.查询帖子列表每一条帖子的点赞量、收藏量、评论数
        for (Post post :postPage.getRecords()) {
            // 2.1 拿到帖子id、作者id
            String postId = post.getId();
            Long userId = post.getAuthorId();
            // 3. 查询每条帖子的额外信息并加入records中
            records.add(selectExtra(post, postId, userId));
        }
        // 4. 将帖子列表按点赞量排序
        records.sort(new Comparator<PostVo>() {
            @Override
            public int compare(PostVo o1, PostVo o2) {
                return o2.getLikes() - o1.getLikes();
            }
        });

        Page<PostVo> postVoPage = new Page<>();
        // 5. 给返回值postVoPage赋值
        postVoPage.setRecords(records);
        BeanUtils.copyProperties(postPage,postVoPage,"records");
        return postVoPage;
    }

    @Override
    public IPage<PostVo> latestList(String title, Page<Post> page) {
        List<PostVo> records = new ArrayList<>();
        LambdaQueryWrapper<Post> queryWrapper = new LambdaQueryWrapper<>();
        // 0. 判断 title 是否为 null
        if (!Objects.equals(title,"")) {
            queryWrapper
                    .like(Post::getPostTitle,title)
                    .or().like(Post::getPostContent,title);
        }
        // 1. 分页查询帖子列表(按时间排序)
        queryWrapper.orderByDesc(Post::getPostReleaseTime);
        IPage<Post> postPage = postMapper.selectPage(page, queryWrapper);
        // 2.查询帖子列表每一条帖子的点赞量、收藏量、评论数
        for (Post post :postPage.getRecords()) {
            // 2.1 拿到帖子id、作者id
            String postId = post.getId();
            Long userId = post.getAuthorId();
            // 3. 查询每条帖子的额外信息并加入records中
            records.add(selectExtra(post, postId, userId));
        }
        Page<PostVo> postVoPage = new Page<>();
        // 4. 给返回值postVoPage赋值
        postVoPage.setRecords(records);
        BeanUtils.copyProperties(postPage,postVoPage,"records");
        return postVoPage;
    }

    @Override
    public HashMap<String, Object> details(String postId) {
        // 0. 拿到当前用户id
        Long userId = UserThreadLocal.get().getId();
        // 1.通过帖子id查询指定帖子
        Post post = postMapper.selectById(postId);
        // 2.拿到作者id
        Long authorId = post.getAuthorId();
        // 3.查询帖子的额外信息
        PostVo postVo = selectExtra(post, postId, authorId);
        // 4.通过帖子id查询该帖子图片集,并打包进postVo
        List<PostImage> postImages = postImageMapper.selectList(
                new LambdaQueryWrapper<PostImage>()
                        .eq(PostImage::getPostId, postId));
        List<String> images = new ArrayList<>();
        for (PostImage postImage :postImages) {
            images.add(postImage.getPostPicture());
        }
        postVo.setImages(images);
        // 5.查询和作者的关注情况
        Concern concernInfo = concernMapper.selectOne(
                new LambdaQueryWrapper<Concern>()
                        .eq(Concern::getUserId, userId)
                        .eq(Concern::getConcernedId, authorId)
                        .eq(Concern::getDeleted,0));
        int concern = -1;
        if (!Objects.equals(userId,authorId)) {  // 是否当前用户就是作者（则无法构成关注关系）
            if (Objects.equals(concernInfo,null)) {  // 是否关注
                concern = 0;  // 未关注
            }else {
                concern = 1;  // 已关注
            }
        }
        // 6.查询对该篇帖子的点赞情况
        PostLike postLikeInfo = postLikeMapper.selectOne(
                new LambdaQueryWrapper<PostLike>()
                        .eq(PostLike::getUserId, userId)
                        .eq(PostLike::getPostId, postId)
                        .eq(PostLike::getDeleted,0));
        boolean like;
        like = !Objects.equals(postLikeInfo, null);
        // 7.查询对该篇帖子的收藏情况
        PostCollect postCollectInfo = postCollectMapper.selectOne(
                new LambdaQueryWrapper<PostCollect>()
                        .eq(PostCollect::getUserId, userId)
                        .eq(PostCollect::getPostId, postId)
                        .eq(PostCollect::getDeleted,0));
        boolean collect;
        collect = !Objects.equals(postCollectInfo, null);
        // 8.拼接返回信息
        HashMap<String, Object> postDetails = new HashMap<>();
        postDetails.put("postInfo",postVo);
        postDetails.put("concern",concern);
        postDetails.put("like",like);
        postDetails.put("collect",collect);

        // 9.将此次访问存入我的足迹（帖子篇）
        String id = userId + TimeUtil.getNowTimeString();
        Footprint footprint = new Footprint(
                id,userId,postId,null,TimeUtil.getNowTime(),1,0);
        footprintMapper.insert(footprint);

        return postDetails;
    }

    @Override
    public PostVo brief(String postId) {
        // 0. 拿到当前用户id
        Long userId = UserThreadLocal.get().getId();
        // 1. 通过postId查询对应数据库
        Post post = postMapper.selectById(postId);
        // 2. 通过postId查询帖子的简要信息,并返回
        return selectExtra(post, postId, userId);
    }

    @Override
    public Result getMyPostList(Long otherId) {
        String constant = "我的";
        List<PostVo> postVoList = new ArrayList<>();
        // 0. 拿到当前用户id
        Long userId = UserThreadLocal.get().getId();
        // 0.1 判断有没有传入他人id
        if (otherId > 0L) {
            userId = otherId;
            constant = "他人";
        }
        // 1. 通过用户id查询他所有的帖子
        List<Post> postList = postMapper.selectList(
                new LambdaQueryWrapper<Post>()
                        .orderByDesc(Post::getPostReleaseTime)
                        .eq(Post::getAuthorId, userId));
        // 2.查询帖子列表每一条帖子的点赞量、收藏量、评论数
        for (Post post :postList) {
            // 2.1 拿到帖子id
            String postId = post.getId();
            // 3. 查询每条帖子的额外信息并加入records中
            postVoList.add(selectExtra(post, postId, userId));
        }
        return Result.success("获得"+constant+"帖子列表",postVoList);
    }

    @Override
    public Result delete(String postId) {
        // 1.删除指定帖子及其下的评论,删除点赞、收藏、图集
        // 1.1 删除该帖子下所有评论的点赞记录
        commentLikeMapper.deleteByPostId(postId);
        // 1.2 删除该帖子下所有的评论
        commentMapper.delete(
                new LambdaQueryWrapper<Comment>()
                        .eq(Comment::getPostId, postId));
        // 1.3 删除该帖子的所有点赞记录
        postLikeMapper.delete(
                new LambdaQueryWrapper<PostLike>()
                        .eq(PostLike::getPostId, postId));
        // 1.4 删除该帖的所有收藏记录
        postCollectMapper.delete(
                new LambdaQueryWrapper<PostCollect>()
                        .eq(PostCollect::getPostId,postId));
        // 1.5 删除该帖子的图片集
        postImageMapper.delete(
                new LambdaQueryWrapper<PostImage>()
                        .eq(PostImage::getPostId,postId));
        // 1.6 删除该帖子记录
        postMapper.deleteById(postId);
        return Result.success("已删除指定帖子");
    }


    /* 查询指定帖子的附加信息（点赞量、收藏量、评论数、作者头像、作者昵称） */
    private PostVo selectExtra(Post post, String postId, Long userId) {
        // 1. 查询点赞量
        LambdaQueryWrapper<PostLike> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(PostLike::getPostId, postId).eq(PostLike::getDeleted,0);
        Integer likes = postLikeMapper.selectList(queryWrapper1).size();
        // 2. 查询收藏量
        LambdaQueryWrapper<PostCollect> queryWrapper2 = new LambdaQueryWrapper<>();
        queryWrapper2.eq(PostCollect::getPostId, postId).eq(PostCollect::getDeleted,0);
        Integer collections = postCollectMapper.selectList(queryWrapper2).size();
        // 3. 查询评论数
        LambdaQueryWrapper<Comment> queryWrapper3 = new LambdaQueryWrapper<>();
        queryWrapper3.eq(Comment::getPostId, postId);
        Integer comments = commentMapper.selectList(queryWrapper3).size();
        log.info("点赞数：{}，收藏数：{}，评论数：{}",likes,collections,comments);
        // 4. 查询作者的昵称、头像
        LambdaQueryWrapper<User> queryWrapper4 = new LambdaQueryWrapper<>();
        queryWrapper4.eq(User::getId, userId);
        User userInfo = userMapper.selectOne(queryWrapper4);
        // 5. 赋值
        PostVo postVo = new PostVo();
        // 5.1 赋值（userId，postTitle，postContent，postType,postReleaseTime,postFirstPicture）
        BeanUtils.copyProperties(post, postVo);
        // 5.2 赋值（帖子id，作者头像，作者昵称、点赞量、收藏量、评论数）
        postVo.setPostId(postId);
        postVo.setAvatar(userInfo.getAvatar());
        postVo.setNickname(userInfo.getNickname());
        postVo.setLikes(likes);
        postVo.setCollections(collections);
        postVo.setComments(comments);
        // 6.返回
        return postVo;
    }




}
