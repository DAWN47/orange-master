package com.shuai.config;

import com.shuai.handler.LoginHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.filter.OrderedHiddenHttpMethodFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.swing.tree.TreeNode;

/**
 * 处理CORS报错
 * 解决前后端交互的跨域问题
 */

@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * 解决跨域问题
     * @param registry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry){
        // 设置允许跨域的路径
        registry.addMapping("/**")
                // 设置允许跨域请求的域名
                .allowedOriginPatterns("*")
                // 是否与允许cookie
                .allowCredentials(true)
                // 设置允许的请求方式
                .allowedMethods("GET","POST","PUT","DELETE","HEAD","OPTIONS")
                // 跨域允许时间
                .maxAge(3600)
                // 设置允许的 header 属性
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    //配置OrderedHiddenHttpMethodFilter
    @Bean
    public OrderedHiddenHttpMethodFilter hiddenHttpMethodFilter(){
        return new OrderedHiddenHttpMethodFilter();
    }


    // 自定义拦截器 已经弃用
//    @Autowired
//    private LoginHandler loginHandler;
//
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(loginHandler);
//    }

}
