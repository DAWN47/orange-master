package com.shuai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shuai.pojo.po.Inform;
import com.shuai.pojo.vo.InformVo;

import java.util.List;

/**
 * @Author: fengxin
 * @CreateTime: 2023-08-15  17:14
 * @Description: TODO
 */
public interface InformService extends IService<Inform> {

    // 获取系统消息列表
    List<InformVo> getSystemChatList();
}
