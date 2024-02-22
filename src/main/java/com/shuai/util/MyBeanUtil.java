package com.shuai.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @Author: fengxin
 * @CreateTime: 2023-05-12  20:50
 * @Description: 解决 websocket 内 @Autowired注入为null问题
 */
@Component
public class MyBeanUtil implements ApplicationContextAware {

    protected static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext app) throws BeansException {
        if (applicationContext == null) {
            applicationContext = app;
        }
    }

    /**
     * 通过此方法，以类的class从容器中手动获取对象
     */
    public static <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }
}
