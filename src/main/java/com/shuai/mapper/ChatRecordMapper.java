package com.shuai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shuai.pojo.po.ChatRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ChatRecordMapper extends BaseMapper<ChatRecord> {

    // 通过当前用户id搜索获取该用户的所有聊天记录
    List<ChatRecord> getChatRecordById(@Param("id") Long id);

    // 通过当前用户id获取该用户所有和商家的聊天记录
    List<ChatRecord> getGoodChatList(@Param("userId") Long userId);
}
