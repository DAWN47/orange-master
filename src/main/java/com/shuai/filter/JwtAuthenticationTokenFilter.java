package com.shuai.filter;

import com.alibaba.fastjson.JSON;
import com.shuai.common.RedisKey;
import com.shuai.handler.UserThreadLocal;
import com.shuai.pojo.vo.UserVo;
import com.shuai.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Objects;

/**
 * @Author: fengxin
 * @CreateTime: 2023-08-04  20:31
 * @Description: TODO
 */
@Slf4j
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Autowired
    private RedisUtil redisCache;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 便于前后端调试：
        log.info("请求路径：{}",request.getRequestURL());
        log.info("请求时间：{}", TimeUtil.getNowTime());
        log.info("token==>{}",request.getHeader("Authorization"));
        log.info("请求类型：{}",request.getMethod());
        Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            // 处理每个参数的逻辑：拿到参数值、打印
            String[] parameterValues = request.getParameterValues(paramName);
            System.out.println("" + paramName + " = " + Arrays.toString(parameterValues));
        }


        // 获取token
        String token = request.getHeader("Authorization");
        if (!StringUtils.hasText(token)) {
            // 放行（即不去解析token，放行让后面的过滤器拦截这个不合法登录）
            filterChain.doFilter(request, response);
//            WebUtils.renderString(response,JSON.toJSONString(Result.fail("token不是字符串！")));
            return;
        }
        // 解析token 判断是否合法
        log.info("token==>{}",token);
        boolean verify = JwtUtil.verify(token);
        if (!verify){
            log.info("token不合法或已过期!请重新登录");
            WebUtils.renderString(response,JSON.toJSONString(Result.fail("token不合法或已过期!请重新登录")));
            return;
        }
        // 从redis中获取用户信息
        String redisKey = RedisKey.TOKEN + token;
        String userJson = redisCache.getCacheObject(redisKey);
        if (com.baomidou.mybatisplus.core.toolkit.StringUtils.isBlank(userJson)){
            log.info("Redis缓存出错!请重新登录");
            WebUtils.renderString(response,JSON.toJSONString(Result.fail("Redis缓存出错!请重新登录")));
            return;
        }
        UserVo uservo = JSON.parseObject(userJson, UserVo.class);
        UserThreadLocal.put(uservo);

        // 存入SecurityContextHolder
        // TODO 获取权限信息封装到Authentication中
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(uservo,null,uservo.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        // 放行
        filterChain.doFilter(request, response);
    }
}

