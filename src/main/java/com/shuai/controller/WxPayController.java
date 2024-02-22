package com.shuai.controller;

import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.shuai.config.WeChatProperties;
import com.shuai.pojo.vo.OrderPaymentVO;
import com.shuai.pojo.vo.OrderVo;
import com.shuai.service.OrderService;
import com.shuai.util.Result;
import com.wechat.pay.contrib.apache.httpclient.util.AesUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

/**
 * @Author: fengxin
 * @CreateTime: 2023-09-10  08:56
 * @Description: 微信支付接口
 */
@Slf4j
@Transactional
@RestController
@RequestMapping("/wechat")
public class WxPayController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private WeChatProperties weChatProperties;

    /**
     * @description: 订单支付
     * @author: fengxin
     * @date: 2023/9/10 9:01
     * @param: [orderVo] = {orderNo, payMethod}
     * @return: 预支付交易单
     **/
    @PutMapping("/payment")
    public Result payment(@RequestBody OrderVo orderVo) throws Exception {
        log.info("订单支付：{}", orderVo);
        // 注意：这里就唯一支付方式：微信支付 （后期可添加支付方式，删除此行）
        orderVo.setPayMethod("微信支付");
        // 生成预支付交易单
        OrderPaymentVO orderPaymentVO = orderService.payment(orderVo);
        log.info("生成预支付交易单：{}", orderPaymentVO);
        return Result.success("预支付交易单",orderPaymentVO);
    }

    /**
     * @description: 支付成功回调
     * @author: fengxin
     * @date: 2023/9/10 9:50
     * @param: [request, response]
     * @return: void
     **/
    @RequestMapping("/notify")
    public void paySuccessNotify(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 1. 读取数据
        String body = readData(request);
        log.info("支付成功回调：{}", body);
        // 2. 数据解密
        String plainText = decryptData(body);
        log.info("解密后的文本：{}", plainText);
        JSONObject jsonObject = JSON.parseObject(plainText);
        String outTradeNo = jsonObject.getString("out_trade_no");//商户平台订单号
        String transactionId = jsonObject.getString("transaction_id");//微信支付交易号
        log.info("商户平台订单号：{}", outTradeNo);
        log.info("微信支付交易号：{}", transactionId);
        // 3. 业务处理，修改订单状态
        orderService.paySuccess(outTradeNo);
        // 4. 给微信响应
        responseToWeixin(response);
    }

    /* 读取数据 */
    private String readData(HttpServletRequest request) throws Exception {
        BufferedReader reader = request.getReader();
        StringBuilder result = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            if (result.length() > 0) {
                result.append("\n");
            }
            result.append(line);
        }
        return result.toString();
    }

    /* 数据解密 */
    private String decryptData(String body) throws Exception {
        JSONObject resultObject = JSON.parseObject(body);
        JSONObject resource = resultObject.getJSONObject("resource");
        String ciphertext = resource.getString("ciphertext");
        String nonce = resource.getString("nonce");
        String associatedData = resource.getString("associated_data");

        AesUtil aesUtil = new AesUtil(weChatProperties.getApiV3Key().getBytes(StandardCharsets.UTF_8));
        //密文解密
        String plainText = aesUtil.decryptToString(associatedData.getBytes(StandardCharsets.UTF_8),
                nonce.getBytes(StandardCharsets.UTF_8),
                ciphertext);

        return plainText;
    }

    /* 给微信响应 */
    private void responseToWeixin(HttpServletResponse response) throws Exception{
        response.setStatus(200);
        HashMap<Object, Object> map = new HashMap<>();
        map.put("code", "SUCCESS");
        map.put("message", "SUCCESS");
        response.setHeader("Content-type", ContentType.APPLICATION_JSON.toString());
        response.getOutputStream().write(JSONUtils.toJSONString(map).getBytes(StandardCharsets.UTF_8));
        response.flushBuffer();
    }
}
