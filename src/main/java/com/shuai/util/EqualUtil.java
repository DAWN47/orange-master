package com.shuai.util;

import java.util.Objects;

/**
 * 字符串工具类
 * @author 码农封心
 *
 */
public class EqualUtil {
    /**
     * 判断字符串是否为空
     */
    public static boolean stringIsEmpty(String str) {
        return str == null || "".equals(str.trim());
    }

    /**
     * 判断字符串是否不是空
     */
    public static boolean stringIsNotEmpty(String str) {
        return str != null && !"".equals(str.trim());
    }

    /**
     * 判断 object 是否是空
     */
    public static boolean objectIsEmpty(Object object) {
        return Objects.equals(object, null);
    }

    /**
     * 判断多个 object 是否是空
      * @param objects
     * @return
     */
    public static boolean objectIsEmpty(Object... objects) {
        for (Object object :objects) {
            if (Objects.equals(object,null)) {
                return true;
            }
        }
        return false;
    }
}
