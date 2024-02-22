package com.shuai.pojo.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: fengxin
 * @CreateTime: 2023-08-07  11:02
 * @Description: 帖子图集表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostImage {

    // 主键（帖子id + index）
    private String id;

    // 帖子 id
    private String postId;

    // 帖子图片url
    private String postPicture;

}
