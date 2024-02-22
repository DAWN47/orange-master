package com.shuai.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * @Author: fengxin
 * @CreateTime: 2023-04-20  19:35
 * @Description: websocket的配置类
 */
@Configuration
public class WebSocketConfig {

    /* 注入ServerEndpointExporter的bean对象，自动扫描使用了  @ServerEndpoint  注解的bean
     * @description:
     * @author: fengxin
     * @date: 2023/4/20 19:45
     * @param: []
     * @return: ServerEndpointExporter
     **/
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}
