package com.shuai.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: fengxin
 * @CreateTime: 2023-08-07  15:21
 * @Description: 论坛帖子信息 给前端
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostVo {

    // 帖子id（主键：发帖人id + 发布时间）
    private String postId;

    // 作者 id
    private Long authorId;

    // 帖子标题
    private String postTitle;

    // 帖子内容
    private String postContent;

    // 帖子类型（求助贴、科普贴、随手贴...）
    private String postType;

    // 发布时间
    private String postReleaseTime;

    // 首图
    private String postFirstPicture;

    // 点赞量
    private Integer likes;

    // 收藏量
    private Integer collections;

    // 评论数
    private Integer comments;

    // 作者昵称
    private String nickname;

    // 作者头像
    private String avatar;

    // 帖子图片集
    private List<String> images = new ArrayList<>();

}
