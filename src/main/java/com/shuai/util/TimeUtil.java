package com.shuai.util;

import javax.xml.crypto.Data;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author: fengxin
 * @CreateTime: 2023-04-25  21:06
 * @Description: TODO
 */
public class TimeUtil {

    // 获取当前时间
    public static String getNowTime(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(System.currentTimeMillis());
    }

    // 获取当前时间数串
    public static String getNowTimeString(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String format = simpleDateFormat.format(System.currentTimeMillis());
        return format.replace(":","").replace(" ","")
                .replace("-","");
    }

    // 时间字符串转日期
    public static Date switchDate(String dateString) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.parse(dateString);
    }
}
