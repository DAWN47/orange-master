package com.shuai.handler;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.shuai.pojo.vo.UserVo;
import com.shuai.util.JwtUtil;
import com.shuai.common.RedisKey;
import com.shuai.util.Result;
import com.shuai.util.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Objects;

//将拦截器放入mvc配置中
@Deprecated/* 标识：已经弃用 */
@Slf4j
//@Component 不再注入容器
public class LoginHandler implements HandlerInterceptor {

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
                // 方法执行之前进行拦截
        /**
         * 1. 判断请求是否是请求 controller方法
         * 2. 有些接口不需要登录拦截，需要开发自定义的注解 @NoAuth => 此注解标识的，表示不需要登录验证
         * 3. 拿到 token
         * 4. 去 redis 进行token认证 ，获得 user信息
         * 5. 如果 token 认证通过就放行，不通过则返回未登录
         * 6. 得到了用户信息，放入ThreadLocal中
         */

        log.info("请求路径：{}",request.getRequestURL());
        log.info("请求时间：{}", TimeUtil.getNowTime());
        log.info("handler==>{}",handler);
        log.info("token==>{}",request.getHeader("Authorization"));
        log.info("请求类型：{}",request.getMethod());

        Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            // 处理每个参数的逻辑：拿到参数值、打印
            String[] parameterValues = request.getParameterValues(paramName);
            System.out.println("" + paramName + " = " + Arrays.toString(parameterValues));
        }

        if (!(handler instanceof HandlerMethod)){//如果不是访问的controller方法就直接放行
            return true;
        }

        //如过方法上有@NoAuth注解就直接放行
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        if (handlerMethod.hasMethodAnnotation(NoAuth.class)){
            return true;
        }

        String token = request.getHeader("Authorization");
        if (Objects.equals(token,null)) {
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write(JSON.toJSONString(Result.fail("token为 null")));
        }
        log.info("token==>{}",token);
//        token = token.replace("Bearer","");
        boolean verify = JwtUtil.verify(token);
        if (!verify){
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write(JSON.toJSONString(Result.fail("token不合法或已过期!请重新登录")));
            return false;
        }
        String userJson = (String) redisTemplate.opsForValue().get(RedisKey.TOKEN + token);
        if (StringUtils.isBlank(userJson)){
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write(JSON.toJSONString(Result.fail("Redis缓存出错!请重新登录")));
            return false;
        }
        UserVo uservo = JSON.parseObject(userJson, UserVo.class);
        UserThreadLocal.put(uservo);
        return true;

    }
}
