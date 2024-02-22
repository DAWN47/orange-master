package com.shuai.config;

import com.alipay.easysdk.factory.Factory;
import com.alipay.easysdk.kernel.Config;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Data
@Component
@PropertySource(value = "classpath:application.yml")
public class AliPayConfig {

    @Value("${alipay.appId}")
    private String appId;

    @Value("${alipay.appPrivateKey}")
    private String appPrivateKey;

    @Value("${alipay.alipayPublicKey}")
    private String alipayPublicKey;

    @Value("${alipay.notifyUrl}")
    private String notifyUrl;


    @PostConstruct
    public void init() {
        // 设置参数（全局只需设置一次）
        Config options = getOptions();
        options.appId = this.appId;
        options.merchantPrivateKey = this.appPrivateKey;
        options.alipayPublicKey = this.alipayPublicKey;
        options.notifyUrl = this.notifyUrl;
        Factory.setOptions(options);
        System.out.println("=======支付宝SDK初始化成功=======");
    }

    private Config getOptions() {
        Config config = new Config();
        config.protocol = "https";
        config.gatewayHost = "openapi.alipaydev.com";
        config.signType = "RSA2";
        return config;
    }

}
