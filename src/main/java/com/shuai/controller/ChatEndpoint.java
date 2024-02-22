package com.shuai.controller;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.shuai.common.Constants;
import com.shuai.mapper.InformMapper;
import com.shuai.mapper.UserMapper;
import com.shuai.pojo.po.Inform;
import com.shuai.pojo.po.ChatRecord;
import com.shuai.pojo.po.User;
import com.shuai.pojo.bo.ChatRecordBo;
import com.shuai.pojo.vo.InformVo;
import com.shuai.service.ChatRecordService;
import com.shuai.util.EqualUtil;
import com.shuai.util.MessageUtils;
import com.shuai.util.MyBeanUtil;
import com.shuai.util.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: fengxin
 * @CreateTime: 2023-04-20  19:28
 * @Description: websocket实现聊天功能
 */
@Slf4j
@Controller
@ServerEndpoint("/chat/{id}")
public class ChatEndpoint {

    // 用来存储每一个客户端对象对应的 ChatEndpoint 对象
    private static Map<Long, Session> onlineUsers = new ConcurrentHashMap<>();

    // 声明Session对象，通过该对象可以发送消息给指定的用户
    private Session session;

    // 存储当前用户的id 在 map 中做 key
    private Long id;

    /*
     * @description: 抽取方法：将当前在线用户的id推送给所有的在线客户端
     * @author: fengxin
     * @date: 2023/4/20 22:43
     * @param: []
     * @return: void
     **/
    private void sendUsers() {
        log.info("进行广播，通知所有用户，目前在线用户情况");
        // 1. 获取所有在线用户的id
        Set<Long> ids = onlineUsers.keySet();
        log.info("当前在线用户：{}",ids);
        // 2. 封装成系统发送给用户的消息格式
        String resultMessage = MessageUtils.getMessage(Constants.SYSTEM_MESSAGE, null, ids);
        // 3. 通过遍历所有的在线用户id
        for (Long id : ids) {
            // 3.1 获取每个在线用户的id，通过id拿到对应的ChatEndpoint对象
            Session session = onlineUsers.get(id);
            try {
                // 3.2 通过ChatEndpoint对象给对应在线用户发送系统消息
                session.getBasicRemote().sendText(resultMessage);
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /*
     * @description: 聊天连接建立时被调用：将当前在线用户的id推送给所有的在线客户端
     * @author: fengxin
     * @date: 2023/4/20 19:33
     * @param: [session, config]
     * @return: void
     **/
    @OnOpen
    public void onOpen(Session session, @PathParam("id") Long id) {
        log.info("进入onOpen方法！");
        // 1. 将局部session对象赋值给成员session
        this.session = session;
        // 2. 将当前用户id 赋值给 成员id
        this.id = id;
        // 3. 将当前用户对象存储到容器中（通过id标识在线用户）
        onlineUsers.put(id, session);
        // 4. 将当前在线用户的id推送给所有的在线客户端
        sendUsers();
        log.info("新用户成功建立连接，用户id：{}，当前在线人数：{}", id, onlineUsers.size());
    }

    /*
     * @description: 接收到客户端发送的数据被调用：实现用户与用户的消息传递
     * @author: fengxin
     * @date: 2023/4/20 19:33
     * @param: [message, session]
     * @return: void
     **/
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("进入onMessage方法!接收到用户id：{}发来信息：{}", id, message);
        if (Objects.equals(message,"心跳")) {
            log.info("接收到用户id：{}发来心跳：{}", id, message);
            try {
                sendText(id,"收到心跳，砰~砰~砰");
                log.info("成功回复“心跳”");
                return;
            }catch (Exception e) {
                e.printStackTrace();
            }

        }

        // 0. 通过工具类手动获取 userMapper 对象
        UserMapper userMapper = MyBeanUtil.getBean(UserMapper.class);
        // 0.1 获取身份信息 判断通知消息种类：用户消息/商家消息/专家消息
        User userInfo = userMapper.selectById(id);
        String role = userInfo.getRole();
        // 默认身份：用户 -> 用户消息
        String informType = Constants.USER_MESSAGE;
        if (Objects.equals(role,"商家")) {
            informType = Constants.GOOD_MESSAGE;
        }else if (Objects.equals(role,"专家")) {
            informType = Constants.EXPERT_MESSAGE;
        }

        try {
            // 1. 将局部变量message转换成JSONObject对象
            JSONObject obj = JSONUtil.parseObj(message);
            // 2. 获取接收者用户的id ,获取发送者用户的id ,消息类型 messageType
            Long receiverId = obj.getLong("toId");
            Long senderId = obj.getLong("fromId");
            String messageType = obj.getStr("messageType");
            // 3. 获取消息数据
            String content = obj.getStr("content");
            // 判断参数是否正常：
            if (EqualUtil.objectIsEmpty(receiverId,senderId,messageType,content) || Objects.equals(content,"")) {
                String returnMessage = MessageUtils.getMessage(Constants.SYSTEM_TIP, null,"发送失败！参数异常");
                sendText(senderId, returnMessage);
                return;
            }
            // 3.1 构成 浏览器发送给服务器的数据对象
            ChatRecordBo chatRecordBo = new ChatRecordBo(senderId,receiverId,content,messageType);
            // 判断用户toId是否在线
            if (onlineUsers.containsKey(receiverId)) {
                // 用户toId在线：
                // 4. 封装成系统发送给用户的消息格式
                String resultMessage = MessageUtils.getMessage(informType, senderId, chatRecordBo);
                // 5. 给客户端发送信息
                sendText(receiverId, resultMessage);
            }
            // 6. 向发送者反馈发送成功
            String returnMessage = MessageUtils.getMessage(Constants.SYSTEM_TIP, null,"发送成功！");
            sendText(senderId, returnMessage);

            // 7. 将当前用户发送的消息记录到数据库
            ChatRecord chatRecord = new ChatRecord();
            // 7.1 把chatRecordBo里的值都赋值给chatRecord
            BeanUtils.copyProperties(chatRecordBo,chatRecord);
            // 7.2 继续赋值
            chatRecord.setId(id + TimeUtil.getNowTimeString() + receiverId);
            chatRecord.setSenderId(senderId);
            chatRecord.setReceiverId(receiverId);
            chatRecord.setSendTime(TimeUtil.getNowTime());
            // 7.3 通过工具类手动获取 chatRecordService 对象
            ChatRecordService chatRecordService = MyBeanUtil.getBean(ChatRecordService.class);
            // 7.4 调用 chatRecordService 的sava方法，将该条数据保存到数据库
            chatRecordService.save(chatRecord);
            log.info("将这条聊天记录：{}保存到了数据库", chatRecord);
        }catch (Exception e) {
            log.info("onMessage方法出现错误了，快去调试~");
            e.printStackTrace();
        }
    }

    /* 给客户端发送信息 */
    private static void sendText(Long receiverId, String resultMessage) throws IOException {
        // 1. 服务器向指定（id）客户端发送数据
        // 1.1 获取接收消息用户id对应的ChatEndpoint对象
        Session receiverSession = onlineUsers.get(receiverId);
        // 2. 通过ChatEndpoint对象给对应在线用户发送系统消息
        receiverSession.getBasicRemote().sendText(resultMessage);
    }

    /*
     * @description: 连接关闭时被调用：将新的所有在线用户通知给所有在线用户
     * @author: fengxin
     * @date: 2023/4/20 19:34
     * @param: [session]
     * @return: void
     **/
    @OnClose
    public void onClose(Session session) {
        log.info("进入onClose方法，在线用户 -1");
        // 1. 当前用户下线，则从在线用户中删除该用户
        onlineUsers.remove(id);
        // 2. 将新的所有在线用户通知给所有在线用户
        sendUsers();
    }



    /*
     * @description: 当 websocket 发生错误自动调用此方法
     * @author: fengxin
     * @date: 2023/5/12 18:30
     * @param: [session, error]
     * @return: void
     **/
    @OnError
    public void onError(Session session, Throwable error) {
        // 什么都不想打印都去掉就好了
        log.info(" websocket 出错啦 -_-!");
        // 打印错误信息，如果你不想打印错误信息，去掉就好了
        // 这里打印的也是  java.io.EOFException: null
        error.printStackTrace();
    }


    /**
     * @description: 自定义，系统给用户发送消息
     * @author: fengxin
     * @date: 2023/8/11 19:50
     * @param: [inform]
     * @return: void
     **/
    public static void sendInfo(InformVo informVo)  {
        log.info("调用服务器给客户端发送系统信息，{}",informVo);
        // 1. 发送者id
        Long fromId = informVo.getFromId();
        // 2. 接收者id
        Long toId = informVo.getToId();
        // 3. 判断用户toId是否在线
        if (onlineUsers.containsKey(toId)) {   // 用户toId在线：
            log.info("用户toId在线");
            // 4. 封装成系统发送给用户的消息格式
            String resultMessage = MessageUtils.getMessage(Constants.SYSTEM_MESSAGE, fromId, informVo);
            try {
                // 5. 向客户端通知消息
                sendText(toId,resultMessage);
            } catch (IOException e) {
                e.printStackTrace();
            }
            log.info("发送在线系统信息成功！");
        }
        // log.info("用户toId 不在线 ");
        // 6. 将此系统消息存入数据库
        Inform inform = new Inform();
        BeanUtils.copyProperties(informVo,inform);
        inform.setId(fromId + TimeUtil.getNowTimeString() + toId);
        // 6.1 通过工具类手动获取 informMapper 对象
        InformMapper informMapper = MyBeanUtil.getBean(InformMapper.class);
        // 6.2 通知消息添加到数据库
        informMapper.insert(inform);
        log.info("离线通知已存入数据库！");
    }
}
