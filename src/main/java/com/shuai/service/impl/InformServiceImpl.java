package com.shuai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shuai.handler.UserThreadLocal;
import com.shuai.mapper.InformMapper;
import com.shuai.mapper.PostMapper;
import com.shuai.mapper.UserMapper;
import com.shuai.pojo.po.Inform;
import com.shuai.pojo.po.Post;
import com.shuai.pojo.po.User;
import com.shuai.pojo.vo.InformVo;
import com.shuai.service.InformService;
import com.shuai.service.PostService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: fengxin
 * @CreateTime: 2023-08-15  17:15
 * @Description: TODO
 */
@Service
public class InformServiceImpl extends ServiceImpl<InformMapper, Inform> implements InformService {

    @Autowired
    private InformMapper informMapper;

    @Autowired
    private UserMapper userMapper;


    @Override
    public List<InformVo> getSystemChatList() {
        List<InformVo> informVos = new ArrayList<>();
        // 1. 通过 当前用户id 查询数据库拿到系统消息记录
        List<Inform> informList = informMapper.selectList(
                new LambdaQueryWrapper<Inform>().eq(Inform::getToId, UserThreadLocal.get().getId()));
        // 2. 通过 发送者id 拿到发送者的昵称、头像，然后赋值给 informVo
        for (Inform inform :informList) {
            // 2.1 通过 发送者id 拿到发送者的昵称、头像
            Long fromId = inform.getFromId();
            User user = userMapper.selectById(fromId);
            // 2.2 赋值给 informVo
            InformVo informVo = new InformVo();
            BeanUtils.copyProperties(inform,informVo);
            informVo.setInformId(inform.getId());
            informVo.setFromNickname(user.getNickname());
            informVo.setFromAvatar(user.getAvatar());
            // 2.3 加入 informVos
            informVos.add(informVo);
        }
        return informVos;
    }
}
