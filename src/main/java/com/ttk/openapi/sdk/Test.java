package com.ttk.openapi.sdk;

import com.alibaba.fastjson.JSONObject;

/**
 * @version V1.0
 * @uathor gaoen
 * @Title: edf-openapi-sdk
 * @Description: 描述
 * @Date 2023-03-23 10:02
 */
public class Test {

    public static void main(String[] args) {
        String apiHost = "http://apitest.bjqygc.com:8089/v1";
        String webHost = "http://test-dz.bjqygc.com:8089";
        String appKey = "20005001";
        String appSecret = "DkrEhNFor4tI9Pcz1aTp0xsxIX3sd5hV2o2mwbBUSuA0w1N5";
        TtkOpenAPI ttkOpenAPI = new TtkOpenAPI(apiHost, appKey, appSecret, webHost);
        JSONObject jsonObjectRequest = new JSONObject();
//        jsonObjectRequest.put("orgName", "惠州市盈沣通科技有限公司");
//        jsonObjectRequest.put("vatTaxpayerNum", "91441303MA545K140C");
//        jsonObjectRequest.put("orgName", "");
        jsonObjectRequest.put("page", "ttk-dz-app-portal");
        jsonObjectRequest.put("orgId", "3740932340469761");
        jsonObjectRequest.put("mobile", "13051591061");
        String json = jsonObjectRequest.toJSONString();
        try {
            String url = "/api/getWebUrl";
            System.out.println(url);
            System.out.println(json);
            JSONObject jsonObject = ttkOpenAPI.rest(url, json);
            System.out.println(JSONObject.toJSON(jsonObject));
        }catch (Exception ex){
            System.out.println(JSONObject.toJSON(ex));
        }
    }
}
