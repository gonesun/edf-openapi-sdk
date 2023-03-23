package com.ttk.openapi.sdk;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import com.ttk.openapi.sdk.dto.OpenApiBusinessException;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSONObject;

/**
 * 基于Apache HttpClient 实现的 http请求工具类
 *
 * @author lvaolin
 * 18/11/7 下午3:46
 */
public class HttpUtil {

    private static final Logger log = Logger.getLogger(HttpUtil.class.getName());

    private static String sdkVersion = null;

    /**
     * 读取sdk版本号
     */
    static {
        try {
            ResourceBundle resource = ResourceBundle.getBundle("sdk");
            if (resource != null) {
                sdkVersion = resource.getString("sdkVersion");
            }
        } catch (Exception e) {
            sdkVersion = "";
        }
    }

    public static JSONObject post(String url, JSONObject requestBody, Map<String, String> map) {

        if (url == null || url.isEmpty()) {
            throw new OpenApiBusinessException("","url不能为空");
        }
        if (requestBody == null) {
            throw new OpenApiBusinessException("","requestBody不能为null");
        }

        long startTime = System.currentTimeMillis();
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);

        // 定制header参数
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-Type", "application/json");
        if (sdkVersion == null) {
            sdkVersion = "";
        }
        httpPost.setHeader("sdkVersion", sdkVersion);
        // 添加通用参数
        if (map != null) {
            Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                httpPost.setHeader(entry.getKey(), entry.getValue());
            }
        }
        String charSet = "UTF-8";
        // 请求体body
        String bodyJsonString = requestBody.toJSONString();
        StringEntity entity = new StringEntity(bodyJsonString, charSet);
        httpPost.setEntity(entity);
        CloseableHttpResponse httpResponse = null;
        try {
            // 开始网络请求
            httpResponse = httpClient.execute(httpPost);
            // 响应状态码
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                HttpEntity httpEntity = httpResponse.getEntity();
                String res_body_string = EntityUtils.toString(httpEntity, charSet);
                JSONObject jsonObject = JSONObject.parseObject(res_body_string);
                return jsonObject;
            } else {
                throw new OpenApiBusinessException("","rest请求失败，(" + url + ")状态码：" + statusCode);
            }
        } catch (Exception e) {
            throw new OpenApiBusinessException("","rest请求过程中有异常发生：" + e.getMessage());
        } finally {
            try {
                if (httpResponse != null) {
                    httpResponse.close();
                }
                if (httpClient != null) {
                    httpClient.close();
                }

            } catch (Exception e) {
                throw new OpenApiBusinessException("","http关闭时有异常发生：" + e.getMessage());
            }
        }

    }

    /**
     * 业务数据签名
     * @param url url
     * @param access_token token
     * @param appSecret appSecret
     * @param requestBodyData requestBodyData
     * @param header header
     * @return 对象
     * @throws NoSuchAlgorithmException 算法异常
     */
    public static JSONObject postRestfulRequest(String url, String access_token, String appSecret,
            JSONObject requestBodyData, Map<String, String> header) throws NoSuchAlgorithmException {
        if (url == null || url.isEmpty()) {
            throw new OpenApiBusinessException("","url不能为空");
        }
        if (access_token == null || access_token.isEmpty()) {
            throw new OpenApiBusinessException("","access_token不能为空");
        }
        if (appSecret == null || appSecret.isEmpty()) {
            throw new OpenApiBusinessException("","appSecret不能为空");
        }

        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        // 签名准备
        long timestamp = System.currentTimeMillis();
        String jsonbody = requestBodyData.toJSONString();
        // 签名前
        String before = "access_token=" + access_token + "&timestamp=" + timestamp + "&" + jsonbody + "{" + appSecret
                + "}";
        // 签名后
        String sign = MD5(before).toUpperCase();

        // url中参数串
        String uriParameters = "?access_token=" + access_token + "&timestamp=" + timestamp + "&sign=" + sign;

        // 发送请求
        JSONObject jsonObject = HttpUtil.post(url + uriParameters, requestBodyData, header);

        // 得到结果
        return jsonObject;
    }

    /**
     * 计算MD5值
     * @param sourceStr sourceStr
     * @return sourceStr
     */
    public static String MD5(String sourceStr) {
        String result = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(sourceStr.getBytes("UTF-8"));
            byte b[] = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0) {
                    i += 256;
                }
                if (i < 16) {
                    buf.append("0");
                }

                buf.append(Integer.toHexString(i));
            }
            result = buf.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new OpenApiBusinessException("",e.getMessage());
        } catch (UnsupportedEncodingException e) {
            throw new OpenApiBusinessException("",e.getMessage());
        }
        return result;
    }

}
