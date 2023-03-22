package com.ttk.openapi.sdk;

/**
 * 字符串工具类
 *
 */
public final class TtkStringUtil {

    public static boolean isNullOrEmpty(String str) {
        if (str == null) {
            return true;
        }
        return str.isEmpty();
    }

}
