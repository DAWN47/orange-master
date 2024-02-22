package com.shuai.pojo.po;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author: fengxin
 * @CreateTime: 2023-08-07  10:58
 * @Description: 帖子表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Post implements Serializable {

    // 帖子id（主键：发帖人id + 发布时间）
    private String id;

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

}
