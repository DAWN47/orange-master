package com.shuai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shuai.handler.UserThreadLocal;
import com.shuai.mapper.ChatRecordMapper;
import com.shuai.mapper.UserMapper;
import com.shuai.pojo.po.ChatRecord;
import com.shuai.pojo.po.User;
import com.shuai.pojo.vo.ChatRecordVo;
import com.shuai.service.ChatRecordService;
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
 * @CreateTime: 2023-04-24  21:14
 * @Description: TODO
 */
@Service
@Transactional
public class ChatRecordServiceImpl extends ServiceImpl<ChatRecordMapper, ChatRecord> implements ChatRecordService {

    @Autowired
    private ChatRecordMapper chatRecordMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<ChatRecord> getChatList() {
        // 0. 拿到当前用户id
        Long userId = UserThreadLocal.get().getId();
        // 2. 查询数据库和所有人的聊天记录
        return chatRecordMapper.selectList(
                new LambdaQueryWrapper<ChatRecord>()
                .orderByDesc(ChatRecord::getSendTime)
                .eq(ChatRecord::getSenderId,userId)
                .or().eq(ChatRecord::getReceiverId,userId));
    }

    @Override
    public List<ChatRecordVo> getNeedChatList(List<ChatRecord> chatList, String needRole) {
        // 0. 拿到当前用户id
        Long userId = UserThreadLocal.get().getId();
        // 1. 创建一个map集合：key放 所需要角色 的id，value放最新一条聊天记录
        HashMap<Long, ChatRecordVo> result = new HashMap<>();
        // 2. 遍历聊天记录
        for (ChatRecord chatRecord :chatList) {
            // 3. 得到和当前用户聊天用户的id，记录为toId（toId和userId必然不相等）
            Long senderId = chatRecord.getSenderId();
            Long receiverId = chatRecord.getReceiverId();
            Long toId = Objects.equals(senderId, userId) ? receiverId : senderId;
            // 4. 通过查询 toId 的身份
            User user = userMapper.selectById(toId);
            String role = user.getRole();
            // 5. 筛选出和 所需要角色 的聊天记录，然后判断这个key是否存在 result map集合中
            if (Objects.equals(role,needRole) && !result.containsKey(toId)) {
                // 6. 如果该 key 在 result 集合中不存在：则加入 result 集合
                // 6.1 构建返回前端的聊天记录 chatRecordVo
                ChatRecordVo chatRecordVo = new ChatRecordVo();
                BeanUtils.copyProperties(chatRecord,chatRecordVo);
                chatRecordVo.setToId(toId);
                chatRecordVo.setToNickname(user.getNickname());
                chatRecordVo.setToAvatar(user.getAvatar());
                // 6.2 加入 result 集合
                result.put(toId,chatRecordVo);
            }
        }
        // 7. 为了便于前端处理渲染数据：把Map集合转换为List集合
        return new ArrayList<>(result.values());
    }

    @Override
    public Result getChatRecord(Long chatObjectId) {
        HashMap<String, Object> chatRecord = new HashMap<>();
        // 0. 拿到当前用户id
        Long userId = UserThreadLocal.get().getId();
        // 1. 查询数据库：userId 和 chatObjectId 的聊天记录
        List<ChatRecord> chatRecordList = chatRecordMapper.selectList(
                new LambdaQueryWrapper<ChatRecord>()
                        .eq(ChatRecord::getSenderId, userId)
                        .eq(ChatRecord::getReceiverId, chatObjectId)
                        .or()
                        .eq(ChatRecord::getReceiverId, userId)
                        .eq(ChatRecord::getSenderId, chatObjectId)
                        .orderByAsc(ChatRecord::getSendTime));
        // 2. 通过 chatObjectId 查询该用户的昵称、头像
        User chatObject = userMapper.getBriefInfo(chatObjectId);
        chatRecord.put("chatRecord",chatRecordList);
        chatRecord.put("nickname",chatObject.getNickname());
        chatRecord.put("avatar",chatObject.getAvatar());
        // 3. 和聊天对象聊天的消息全部标记已读
        ChatRecord chatRecordUpdate = new ChatRecord();
        chatRecordUpdate.setRead(1);
        chatRecordMapper.update(chatRecordUpdate,
                new LambdaQueryWrapper<ChatRecord>()
                .eq(ChatRecord::getSenderId,chatObjectId)
                .eq(ChatRecord::getReceiverId,userId));

        return Result.success("获取和某人的聊天记录",chatRecord);
    }
}
