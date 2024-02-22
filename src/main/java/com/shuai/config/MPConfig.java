package com.shuai.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// 声明这是配置类
@Configuration
public class MPConfig {
    // spring管理的bean
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(){ // 配置MyBatis-Plus的拦截器
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        // 添加分页相关的拦截器
        mybatisPlusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        return mybatisPlusInterceptor;                    /*分页管理的拦截器*/
    }

}
