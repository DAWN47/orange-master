package com.shuai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shuai.pojo.po.ChatRecord;
import com.shuai.pojo.vo.ChatRecordVo;
import com.shuai.util.Result;

import java.util.HashMap;
import java.util.List;

public interface ChatRecordService extends IService<ChatRecord> {


    // 获取自己所有的聊天记录
    List<ChatRecord> getChatList();

    // 获取和 所需要角色 聊天的消息列表（只有和每个聊天对象 消息的最新一条）
    List<ChatRecordVo> getNeedChatList(List<ChatRecord> chatList,String needRole);

    // 获取和具体某人的所有聊天记录
    Result getChatRecord(Long chatObjectId);
}
