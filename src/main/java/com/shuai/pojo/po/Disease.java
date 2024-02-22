package com.shuai.pojo.po;

/**
 * @Author: fengxin
 * @CreateTime: 2023-10-23  14:43
 * @Description: 疾病表
 */
public class Disease {

    // 自增主键
    private Long id;

    // 疾病名称
    private String diseaseName;

    // 疾病描述
    private String diseaseDescription;

    // 疾病病因
    private String diseaseCause;

    // 疾病建议
    private String diseaseSuggestion;

    // 疾病图例
    private String diseaseFirstPicture;

    // 逻辑删除
    private int deleted;
    

}
