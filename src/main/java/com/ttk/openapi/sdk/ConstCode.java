package com.ttk.openapi.sdk;

/**
 * @version V1.0
 * @uathor gaoen
 * @Title: edf-openapi-sdk
 * @Description: 描述
 * @Date 2023-03-22 18:11
 */
public class ConstCode {

    /**
     * 一秒
     */
    static final long oneSecond = 1000;
    /**
     * 一分钟
     */
    static final long oneMinute = 60 * oneSecond;
    /**
     * 一小时
     */
    static final long oneHour = 60 * oneMinute;

    /**
     * token请求地址
     */
    static final String accessToken = "/edf/oauth2/access_token";

    /**
     * 获取页面地址
     */
    static final String webUrl = "/api/getWebUrl";
}
