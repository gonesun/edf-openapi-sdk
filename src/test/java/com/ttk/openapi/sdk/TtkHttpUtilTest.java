package com.ttk.openapi.sdk;

import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;

import com.alibaba.fastjson.JSONObject;

/** 
* TtkHttpUtil Tester. 
* 
* @author <Authors name> 
* @since <pre>Feb 1, 2019</pre> 
* @version 1.0 
*/
@Disabled
@DisplayName("HttpUtil工具类测试")
public class TtkHttpUtilTest {

    /** 
    * 
    * Method: post(String url, JSONObject requestBody, Map<String, String> map) 
    * 
    */
    @Disabled
    @DisplayName("测试post方法能否正常与服务器交互")
    public void testPost() throws Exception {
        String url = "http://api.test.aierp.cn:8089/v1/openapi/basicData/queryOrgDetailInfo";
        JSONObject requestBody = new JSONObject();
        requestBody.put("orgId", "111111111111111");
        Map<String, String> map = null;
        JSONObject jsonObject = TtkHttpUtil.post(url, requestBody, map);
        Assertions.assertNotEquals(null, jsonObject);
        Assertions.assertNotEquals(null, jsonObject.getJSONObject("head"));
        Assertions.assertEquals("40100", jsonObject.getJSONObject("head").getString("errorCode"));
    }

    /** 
    * 
    * Method: postRestfulRequest(String url, String access_token, String appSecret, JSONObject requestBodyData, Map<String, String> header) 
    * 
    */
    @DisplayName("测试报文签名")
    public void testPostRestfulRequest() throws Exception {
        String url = null;
        String appSecret = "";
        String accessToken = "";
        JSONObject requestBodyData = new JSONObject();
        requestBodyData.put("orgId", "111111111111111");
        Map<String, String> headerMap = null;
        Assertions.assertEquals(null, TtkHttpUtil.postRestfulRequest(url, accessToken, appSecret, requestBodyData, headerMap));
        url = "http://api.test.aierp.cn:8089/v1/openapi/basicData/queryOrgDetailInfo";
        Assertions.assertEquals(null, TtkHttpUtil.postRestfulRequest(url, accessToken, appSecret, requestBodyData, headerMap));
        accessToken = "1qaz2wsx";
        Assertions.assertEquals(null, TtkHttpUtil.postRestfulRequest(url, accessToken, appSecret, requestBodyData, headerMap));
        appSecret = "12345";
        JSONObject jsonObject = TtkHttpUtil.postRestfulRequest(url, accessToken, appSecret, requestBodyData, headerMap);
        Assertions.assertNotEquals(null, jsonObject);
        Assertions.assertNotEquals(null, jsonObject.getJSONObject("head"));
        // token解析失败
        Assertions.assertEquals("10000", jsonObject.getJSONObject("head").getString("errorCode"));
        accessToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJbLTEsLTEsLTEsMTA1LFwiXCIsMTAwMDUwMDUsbnVsbCxcIlwiXSIsImV4cCI6MTU1MDMxMDY3MiwiaWF0IjoxNTQ5MDE0NjcyfQ.1PeMkJ5yNuA3sC0UnJBnjF66j_fgy0iQEWtDR4lkA4E18AHUXN6Kt-b8dH9HPrAcZeIX6xd37PpUBGwpB4syjgeyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJbLTEsLTEsLTEsMTA1LFwiXCIsMTAwMDUwMDUsbnVsbCxcIlwiXSIsImV4cCI6MTU1MDMxMDY3MiwiaWF0IjoxNTQ5MDE0NjcyfQ.1PeMkJ5yNuA3sC0UnJBnjF66j_fgy0iQEWtDR4lkA4E18AHUXN6Kt-b8dH9HPrAcZeIX6xd37PpUBGwpB4syjg";
        appSecret = "fp9xxir91klyj89mwakhmdz54woxzr3blp5ukq1qzsny0i14uoqr78avfjl0o981";
        jsonObject = TtkHttpUtil.postRestfulRequest(url, accessToken, appSecret, requestBodyData, headerMap);
        Assertions.assertEquals("10000", jsonObject.getJSONObject("head").getString("errorCode"));
    }

    /** 
    * 
    * Method: MD5(String sourceStr) 
    * 
    */
    @DisplayName("计算MD5")
    public void testMD5() throws Exception {
        Assertions.assertNotEquals("", TtkHttpUtil.MD5("12345"));
    }

}
