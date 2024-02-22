package com.shuai.pojo.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: fengxin
 * @CreateTime: 2023-08-07  11:02
 * @Description: 商品图集表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoodImage {

    // 主键（商品 id + index）
    private String id;

    // 商品 id
    private String goodId;

    // 商品图片url
    private String goodPicture;

}
