package com.shuai.service.impl;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shuai.common.RedisKey;
import com.shuai.handler.UserThreadLocal;
import com.shuai.mapper.MenuMapper;
import com.shuai.mapper.UserMapper;
import com.shuai.mapper.UserRoleMapper;
import com.shuai.pojo.bo.WxAuth;
import com.shuai.pojo.po.User;
import com.shuai.pojo.vo.UserVo;
import com.shuai.pojo.vo.WxUserInfo;
import com.shuai.service.UserService;
import com.shuai.service.WxService;
import com.shuai.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @Author: fengxin
 * @CreateTime: 2023-04-19  22:43
 * @Description: TODO
 */

@Slf4j
@Service
@Transactional
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {


    private static Integer ADMIN=1;
    private static Integer BUSINESS=2;
    private static Integer EXPERT=3;
    private static Integer USER=4;
    private static Integer BANNEDUSER=5;


    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MenuMapper menuMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

//    @Autowired
//    private RedisTemplate<String,Object> redisTemplate;
    @Autowired
    private RedisUtil redisUtil;

    @Value("${weixin.appid}")
    private String appid;

    @Value("${weixin.secret}")
    private String secret;

    @Autowired
    private WxService wxService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /* 注册用户名+并设置密码
     * @description:
     * @author: fengxin
     * @date: 2023/4/20 10:38
     * @param: [uservo]：{username,password}
     * @return: com.improve.shell.util.Result
     **/
    @Override
    @Deprecated/* 标识：已经弃用 */
    public Result loginByUser(UserVo uservo) {
        // 1. 通过用户名获取用户记录
        User userRecord = selectUserByUsername(uservo.getUsername());
        // 2. 判断用户存不存在
        if (null != userRecord){
            // 2.1 用户存在：
            // 2.1.1 将密码进行MD5加密
            uservo.setPassword(MD5Utils.digest(uservo.getPassword()));
            // 2.1.2 判断密码是否正确
            if (Objects.equals(uservo.getPassword(), userRecord.getPassword())){
                // 设置用户id、传入登录模块
                uservo.setId(userRecord.getId());
                return this.login(uservo);
            }else {
                return Result.fail("账号或密码错误！");
            }
        }else {
            // 2.2 用户不存在：
            return Result.fail("该用户名还未注册！");
        }
    }

    @Override
    public Result accountLogin(UserVo uservo) {
        //AuthenticationManager authenticate进行用户认证
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(uservo.getUsername(),uservo.getPassword());
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        //如果认证没通过，给出对应的提示
        if(Objects.isNull(authenticate)){
            throw new RuntimeException("登录失败");
        }
        //如果认证通过了，使用userid生成一个jwt jwt存入ResponseResult返回
        UserVo userVo = (UserVo) authenticate.getPrincipal();
        Integer userNormal = userMapper.isNormal(userVo.getId());
        if(userNormal==null){
            return Result.fail("账户被禁用");
        }
        List<Integer> roleNormal = userMapper.roleIsNormal(userVo.getId());
        if(roleNormal==null||roleNormal.size()==0){
            return Result.fail("系统维护中,该角色无法登入");
        }
        return login(userVo);
    }

    /**
     * @param uservo:
     * @return Result
     * @author DAWN
     * @description TODO
     * @date 2024/2/21 13:15
     */
    @Override
    public Result adminLogin(UserVo uservo) {
        //AuthenticationManager authenticate进行用户认证
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(uservo.getUsername(),uservo.getPassword());
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        //如果认证没通过，给出对应的提示
        if(Objects.isNull(authenticate)){
            throw new RuntimeException("登录失败");
        }
        //如果认证通过了，使用userid生成一个jwt jwt存入ResponseResult返回
        UserVo userVo = (UserVo) authenticate.getPrincipal();
        if(userMapper.isAdmin(userVo.getId())==null){
            return Result.fail("你不是管理员，无权限登入");
        }
        return login(userVo);
    }


    /* 注册用户名+并设置密码
     * @description:
     * @author: fengxin
     * @date: 2023/4/20 10:38
     * @param: [uservo]：{username,password}
     * @return: com.improve.shell.util.Result
     **/
    @Override
    public Result register(UserVo uservo) {
        User user = new User();
        // 0. 判断有无，参数值 用户名和密码;
        // 0.1 有则是账号密码登录
        // 0.2 无则是微信登录
        if (!EqualUtil.objectIsEmpty(uservo.getUsername(),uservo.getPassword())) {
            // 1. 通过用户名查询数据库有没有已注册的用户记录
            User userRecord = selectUserByUsername(uservo.getUsername());
            // 2. 判断用户存不存在
            if (null != userRecord){
                // 2.1 用户存在：
                return Result.fail("该用户名已被注册！");
            }
            // 2.2 用户不存在：
            // 3. 将密码进行MD5加密
//        uservo.setPassword(MD5Utils.digest(uservo.getPassword()));
            uservo.setPassword(passwordEncoder.encode(uservo.getPassword()));
        }
        // 4. 将uservo的值全部赋值给user：为了用user存入数据库
        BeanUtils.copyProperties(uservo,user);
        // 4.1 获取并设置注册时间
        user.setCreateTime(TimeUtil.getNowTime());
        // 4.2 设置默认昵称、头像
        user.setNickname("昵称" + TimeUtil.getNowTimeString());
        user.setAvatar("https://thirdwx.qlogo.cn/mmopen/vi_32/POgEwh4mIHO4nibH0KlMECNjjGxQUq24ZEaGT4poC6icRiccVGKSyXwibcPq4BWmiaIGuG1icwxaQX6grC9VemZoJ8rg/132");
        // 向数据库添加新注册的用户
        userMapper.insert(user);
        // 添加成功后通过设置uservo的id属性为数据库的id
        uservo.setId(user.getId());
        userRoleMapper.registerUser(user.getId());
        //将新注册的用户添加为用户
        return Result.success("注册成功，请登录！");
    }

    /*
     * (抽取方法)：通过用户名从数据库获取用户记录
     * @param: [uservo]
     * @return: User：数据库中一条用户记录 或 null
     **/
    private User selectUserByUsername(String username) {
        // 1.使用MySQL Plus 的 QueryWrapper
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        // 2.写入查询条件：username
        queryWrapper.eq("username", username);
        // 3.查询并返回记录
        return userMapper.selectOne(queryWrapper);
    }


    /*
     * 签发token
     * @param: [uservo]
     * @return: com.improve.shell.util.Result
     **/
    private Result login(UserVo uservo) {
        // 1. 以用户id 签发token
        String token = JwtUtil.sign(uservo.getId());
        // 2. 设置存入 redis
        UserVo userVo = new UserVo();
        userVo.setId(uservo.getId());
        userVo.setToken(token);
        userVo.setPermissions(uservo.getPermissions());
        // 2. 需要把token 存入redis，value存为uservo，下次用户访问登录资源时，可以根据token拿到用户的详细信息（存储 7 天）
//        redisTemplate.opsForValue().set(RedisKey.TOKEN + token, JSON.toJSONString(uservo),7, TimeUnit.DAYS);
        redisUtil.setCacheObject(RedisKey.TOKEN + token, JSON.toJSONString(userVo),7, TimeUnit.DAYS);
        uservo.setToken(token);
        return Result.success("登录成功",uservo);
    }

    /*-------------------------------------------------微信登录--------------------------------------------------------------*/

    @Override
    public Result getSessionId(String code){
        /**
         * 1. 拼接url，微信登录凭证校验接口
         * 2. 发起http调用，获取微信的返回结构
         * 3. 存到redis
         * 4. 生成sessionId返回给前端，作为用户登录的标识
         * 5. 生成sessionId，当用户点击登录时，可以标识用户身份
         */
        String replaceUrl = "https://api.weixin.qq.com/sns/jscode2session?appid="+
                appid + "&secret=" + secret + "&js_code=" + code + "&grant_type=authorization_code";
        String res = HttpUtil.get(replaceUrl);
        log.info("调用微信接口返回的值 ==> {}", res);

        String uuid = UUID.randomUUID().toString();
//        redisTemplate.opsForValue().set(RedisKey.WX_SESSION_ID + uuid, res, 30, TimeUnit.MINUTES);
        redisUtil.setCacheObject(RedisKey.WX_SESSION_ID + uuid, res, 30, TimeUnit.MINUTES);

        Map<String, String> map = new HashMap<>();
        map.put("sessionId", uuid);
        log.info("sessionId ==> {}", uuid);
        return Result.success("session获取成功",map);
    }

    @Override
    public Result authLogin(WxAuth wxAuth){
        /**
         * 1. 通过 wxAuth中的值,对它进行解密
         * 2. 解密完成后，会获取到微信用户信息，其中包含openId 、性别、 昵称、 头像等信息
         * 3. openId 是唯一的，需要去user表中查询openId是否存在（存在则登录成功，不存在则先注册）
         * 4. 使用jwt技术，生成token 提供给前端 token令牌，用户下次将携带token访问
         * 5. 后端通过对token的验证，知道是 此用户是否处于登录状态 以及是哪个用户登录的
         */

        try {
            // 三个参数，
            String wxRes = wxService.WxDecrypt(wxAuth.getEncryptedData(),wxAuth.getIv(),wxAuth.getSessionId());
            WxUserInfo wxUserInfo = JSON.parseObject(wxRes, WxUserInfo.class);
            // 从redis中拿到openId
//            String json = (String) redisTemplate.opsForValue().get(RedisKey.WX_SESSION_ID + wxAuth.getSessionId());
            String json = redisUtil.getCacheObject(RedisKey.WX_SESSION_ID + wxAuth.getSessionId());

            JSONObject jsonObject = JSON.parseObject(json);
            String openid = (String) jsonObject.get("openid");
            // 给 uservo 赋值
            wxUserInfo.setOpenid(openid);
            UserVo uservo = new UserVo();
            uservo.from(wxUserInfo);
            // 从数据库查询此用户是否存在
            User user = userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getOpenid, openid));
            if (user == null) { // 不存在：
                // 注册
                uservo.setSex(null);
                uservo.setUsername(null);
                uservo.setPassword(null);
                this.register(uservo);
            }
            User userInfo = userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getOpenid, openid));
            // 填充数据库的用户信息(昵称、头像)
            BeanUtils.copyProperties(userInfo,uservo);
            // 查询权限信息
            List<String> list = menuMapper.getPermissions(userInfo.getId());
            uservo.setPermissions(list);
            // 登录
            return this.login(uservo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("登录失败");
    }

    @Override
    public Result userinfo(boolean refresh) {
        /**
         * 1.获取目前登录的用户信息
         * 2.refresh 如果为true 代表刷新 重新生成 token 和 redis里面重新存储 续期
         * 3.false 直接返回用户信息(从redis中查询出来), 直接返回
         */

        UserVo uservo = UserThreadLocal.get();
        if (refresh) {
            String token = JwtUtil.sign(uservo.getId());
            uservo.setToken(token);
//            redisTemplate.opsForValue().set(RedisKey.TOKEN + token, JSON.toJSONString(uservo), 7, TimeUnit.DAYS);
            redisUtil.setCacheObject(RedisKey.TOKEN + token, JSON.toJSONString(uservo),7, TimeUnit.DAYS);
        }
        return Result.success("返回用户信息", uservo);
    }

    /* 登出 */
    @Override
    public Result logout() {
        // 拿到用户的 token
        String token = UserThreadLocal.get().getToken();
        // 塑造 key
        String key = RedisKey.TOKEN + token;
        // 删除此 key
//        redisTemplate.delete(key);
        redisUtil.deleteObject(key);
        return Result.success("登出成功！");
    }


    @Override
    public Result updateInfo(User user) {
        // 0. 拿到当前用户id
        Long userId = UserThreadLocal.get().getId();
        // 1. 组装user
        user.setId(userId);
        // 2. 修改
        int i = userMapper.updateById(user);
        if (i > 0) {
            return Result.success("个人信息修改成功！");
        }else {
            return Result.fail("个人信息修改失败！请联系服务器");
        }
    }

    @Override
    public Result banById(Long id) {
        Integer integer = userMapper.banById(id);
        if(integer==null||integer==0){
            return Result.fail("封禁失败，该用户不存在或已被禁用！！！");
        }
        return Result.success("封禁成功");
    }

    @Override
    public Result unlockById(Long target) {
        Integer integer = userMapper.unlockById(target);
        if(integer==null||integer==0){
            return Result.fail("解禁失败，该用户不存在或状态正常！！！");
        }
        return Result.success("解禁成功");
    }

    @Override
    public Result userToBusiness(Long id) {
//        检查是否是商家
        Integer isOk = userMapper.isOk(id, BUSINESS);
        if(isOk!=null){
            return Result.fail("用户已为商家，请勿重复升级");
        }
        Integer advanceUser = userMapper.advanceUser(id, BUSINESS);
        if(advanceUser==null||advanceUser==0){
            return Result.fail("用户升级为商家失败");
        }
        return Result.success("用户升级商家成功");
    }

    @Override
    public Result businessToUser(Long id) {
        Integer advanceUser = userMapper.demoteUser(id, BUSINESS);
        if(advanceUser==null||advanceUser==0){
            return Result.fail("用户不是商家，无法降级");
        }
        return Result.success("商家降级用户成功");
    }

    @Override
    public Result userToExpert(Long id) {
        //        检查是否是专家
        Integer isOk = userMapper.isOk(id, EXPERT);
        if(isOk!=null){
            return Result.fail("用户已为专家，请勿重复升级");
        }
        Integer advanceUser = userMapper.advanceUser(id, EXPERT);
        if(advanceUser==null||advanceUser==0){
            return Result.fail("用户升级为专家失败");
        }
        return Result.success("用户升级专家成功");
    }

    @Override
    public Result expertToUser(Long id) {
        Integer advanceUser = userMapper.demoteUser(id, EXPERT);
        if(advanceUser==null||advanceUser==0){
            return Result.fail("用户不是专家，无法降级");
        }
        return Result.success("专家降级用户成功");
    }

    @Override
    public Result userToAdmin(Long id) {
        //        检查是否是管理员
        Integer isOk = userMapper.isOk(id,ADMIN);
        if(isOk!=null){
            return Result.fail("用户已为管理员，请勿重复升级");
        }
        Integer advanceUser = userMapper.advanceUser(id, ADMIN);
        if(advanceUser==null||advanceUser==0){
            return Result.fail("用户升级为管理员失败");
        }
        return Result.success("用户升级管理员成功");
    }

    @Override
    public Result adminToUser(Long id) {
        Integer advanceUser = userMapper.demoteUser(id, ADMIN);
        if(advanceUser==null||advanceUser==0){
            return Result.fail("用户不是管理员，无法降级");
        }
        return Result.success("管理员降级用户成功");
    }

    @Override
    public Result banUserPost(Long id) {
        //        检查是否已经被禁言
        Integer isOk1 = userMapper.isOk(id,BANNEDUSER);
        Integer isOk2 = userMapper.isOk(id,USER);
        if(isOk1!=null&&isOk2==null){
            return Result.fail("用户已被禁言，请勿重复禁言");
        }
        Integer advanceUser = userMapper.advanceUser(id, BANNEDUSER);
        Integer demoteUser = userMapper.demoteUser(id, USER);
        if(advanceUser==null&& demoteUser==null||advanceUser==0&&demoteUser==0){
            return Result.fail("用户禁言失败");
        }
        return Result.success("用户禁言成功");
    }

    @Override
    public Result unlockUserPost(Long id) {
        Integer isOk = userMapper.isOk(id, USER);
        if(isOk!=null){
            return Result.fail("用户未被禁言");
        }
        Integer advanceUser1 = userMapper.demoteUser(id, BANNEDUSER);
        if(advanceUser1==null||advanceUser1==0){
            return Result.fail("用户禁言解除失败");
        }
        Integer advanceUser2 = userMapper.advanceUser(id, USER);
        return Result.success("用户解除禁言成功");
    }

    @Override
    public Result selectAllUser() {
        List<User> users = userMapper.selectList(null);
        return Result.success("查询成功",users);
    }

}
