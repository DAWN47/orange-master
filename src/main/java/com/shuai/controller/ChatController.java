package com.shuai.controller;

import com.shuai.pojo.po.ChatRecord;
import com.shuai.pojo.po.Inform;
import com.shuai.pojo.vo.ChatRecordVo;
import com.shuai.pojo.vo.InformVo;
import com.shuai.service.ChatRecordService;
import com.shuai.service.InformService;
import com.shuai.util.EqualUtil;
import com.shuai.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * @Author: fengxin
 * @CreateTime: 2023-04-24  20:58
 * @Description: TODO
 */
@Slf4j
@RestController
@RequestMapping("/record")
public class ChatController {

    @Autowired
    private ChatRecordService chatRecordService;

    @Autowired
    private InformService informService;

    /**
     * @description: 获取和商家聊天的消息列表
     * @author: fengxin
     * @date: 2023/8/15 15:24
     * @param: []
     * @return: 和商家聊天的消息列表
     **/
    @GetMapping("/getGoodChatList")
    public Result getGoodChatList() {
        // 1. 获取自己所有的聊天记录
        List<ChatRecord> chatList = chatRecordService.getChatList();
        // 2. 从中分类得到和商家的聊天列表
        List<ChatRecordVo> goodChatList = chatRecordService.getNeedChatList(chatList,"商家");
        return Result.success("和商家聊天的消息列表",goodChatList);
    }

    /**
     * @description: 获取和专家聊天的消息列表
     * @author: fengxin
     * @date: 2023/8/15 16:59
     * @param: []
     * @return: 和专家聊天的消息列表
     **/
    @GetMapping("/getExpertChatList")
    public Result getExpertChatList() {
        // 1. 获取自己所有的聊天记录
        List<ChatRecord> chatList = chatRecordService.getChatList();
        // 2. 从中分类得到和专家的聊天列表
        List<ChatRecordVo> expertChatList = chatRecordService.getNeedChatList(chatList,"专家");
        return Result.success("和专家聊天的消息列表",expertChatList);
    }


    /**
     * @description: 获取系统消息列表
     * @author: fengxin
     * @date: 2023/8/15 17:21
     * @param: []
     * @return: 系统消息列表
     **/
    @GetMapping("/getSystemChatList")
    public Result getSystemChatList() {
        List<InformVo> informVos = informService.getSystemChatList();
        return Result.success("系统消息列表",informVos);
    }


    /**
     * @description: 获取消息页面的消息列表
     * @author: fengxin
     * @date: 2023/8/15 19:47
     * @param: []
     * @return: 消息页面的消息列表
     **/
    @GetMapping("/getMessageList")
    public Result getMessageList() {
        // 1. 获取自己所有的聊天记录
        List<ChatRecord> chatList = chatRecordService.getChatList();
        // 2. 拿到最新的一条和专家的消息、一条和商家的消息，一条系统通知消息，所有的用户消息
        List<ChatRecordVo> expertMessages = chatRecordService.getNeedChatList(chatList, "专家");
        ChatRecordVo expertMessage = null;
        if (!Objects.equals(expertMessages, new ArrayList<>())) {
            expertMessage = expertMessages.get(0);
        }
        List<ChatRecordVo> goodMessages = chatRecordService.getNeedChatList(chatList, "商家");
        ChatRecordVo goodMessage = null;
        if (!Objects.equals(goodMessages, new ArrayList<>())) {
            goodMessage = goodMessages.get(0);
        }
        List<InformVo> systemMessages = informService.getSystemChatList();
        InformVo systemMessage = null;
        if (!Objects.equals(systemMessages, new ArrayList<>())) {
            systemMessage = systemMessages.get(0);
        }
        List<ChatRecordVo> userChatList = chatRecordService.getNeedChatList(chatList, "用户");
        // 3. 创建一个Map集合作为返回值返回给前端
        HashMap<String, Object> messages = new HashMap<>();
        messages.put("expertMessage",expertMessage);
        messages.put("goodMessage",goodMessage);
        messages.put("systemMessage",systemMessage);
        messages.put("userMessage",userChatList);
        return Result.success("消息页面的消息列表",messages);
    }

    /**
     * @description: 获取和具体某人的所有聊天记录
     * @author: fengxin
     * @date: 2023/8/15 20:59
     * @param: [chatObjectId]
     * @return: 聊天记录
     **/
    @GetMapping("/getChatRecord")
    public Result getChatRecord(@RequestParam("chatObjectId")Long chatObjectId) {
        return chatRecordService.getChatRecord(chatObjectId);
    }

    /**
     * @description: 系统通知 使已读
     * @author: fengxin
     * @date: 2023/9/16 8:45
     * @param: [inform] = {id, read=1}
     * @return: 是否成功
     **/
    @Transactional
    @PutMapping("/informRead")
    public Result informRead(@RequestBody Inform inform) {
        log.info("查看参数：{}",inform);
        if (EqualUtil.objectIsEmpty(inform,inform.getId())) {
            return Result.fail("请检查参数，参数为 null");
        }
        if (inform.getRead() == 0) {
            inform.setRead(1);
        }
        boolean update = informService.updateById(inform);
        if (update) {
            return Result.success("已读");
        }else {
            return Result.fail("已读失败！");
        }
    }
}