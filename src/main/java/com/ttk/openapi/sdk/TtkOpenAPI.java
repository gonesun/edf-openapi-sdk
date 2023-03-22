package com.ttk.openapi.sdk;

import com.alibaba.fastjson.JSONObject;
import com.ttk.openapi.sdk.dto.OpenApiBusinessException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * @author lvaolin
 * 18/8/27 下午4:57
 */
public class TtkOpenAPI {

    private static Logger logger = Logger.getLogger("TtkOpenAPI");
    /**
     * api 网关主机地址
     */
    private String apiHost;


    /**
     * 嵌入网页打开地址
     */
    private String webHost;
    /**
     * 应用key
     */
    private String appKey;
    /**
     * 应用密钥
     */
    private String appSecret;

    /**
     * md5后的密钥
     */
    //private String appSecretMD5;
    /**
     * 应用级访问令牌
     */
    private volatile String token;

    /**
     * token有效期，毫秒，15天有效期
     */
    private volatile Long expiresIn;

    /**
     * 刷新token用
     */
    private volatile String refreshToken;
    /**
     * token获取时间
     */
    private Long lastTokenTime;

    public TtkOpenAPI(String apiHost, String appKey, String appSecret, String webHost) {
        if (TtkStringUtil.isNullOrEmpty(apiHost)) {
            throw new OpenApiBusinessException("","参数apiHost不正确");
        }
        if (TtkStringUtil.isNullOrEmpty(appKey)) {
            throw new OpenApiBusinessException("","参数appKey不正确");
        }
        if (TtkStringUtil.isNullOrEmpty(appSecret)) {
            throw new OpenApiBusinessException("","参数appSecret不正确");
        }
        if (TtkStringUtil.isNullOrEmpty(webHost)) {
            throw new OpenApiBusinessException("","参数webHost不正确");
        }
        if (apiHost.endsWith("/")) {
            apiHost = apiHost.substring(0, apiHost.length() - 1);
        }
        if (webHost.endsWith("/")) {
            webHost = webHost.substring(0, webHost.length() - 1);
        }
        this.apiHost = apiHost;
        this.appKey = appKey;
        this.appSecret = appSecret;
        this.webHost = webHost;
        //this.appSecretMD5 = TtkHttpUtil.MD5(appSecret);
    }


    /**
     * 获取应用级访问token
     */
    private String getAccessToken() {


        if (TtkStringUtil.isNullOrEmpty(token)) {
            synchronized (this) {
                if (TtkStringUtil.isNullOrEmpty(token)) {
                    //获取新token
                    newToken();
                }
            }
        } else {
            //验证有效期，如果快到有效期则刷新token，如果超了有效期则重新获取token
            Long currentTime = System.currentTimeMillis();
            //token持有时间
            Long holdingTime = currentTime - lastTokenTime;
            //到期前24~2小时内 均可刷新token
            if (holdingTime >= (expiresIn - 3600 * 24 * 1000) && holdingTime <= (expiresIn - 3600 * 2 * 1000)) {
                synchronized (this) {
                    //刷新token
                    refreshToken();
                }
            } else if (holdingTime > (expiresIn - 3600 * 2 * 1000)) {
                synchronized (this) {
                    //获取新token
                    newToken();
                }

            }
        }
        return token;
    }

    /**
     * 获取新token
     */
    private void newToken() {
        //1、body数据准备
        JSONObject requestBody = new JSONObject();
        requestBody.put("grant_type", "client_credentials");
        requestBody.put("client_appkey", appKey);
        //requestBody.put("client_secret", appSecretMD5);
        requestBody.put("client_secret", TtkHttpUtil.MD5(appSecret));
        //2、发送请求
        JSONObject jsonObject = TtkHttpUtil.post(apiHost + "/edf/oauth2/access_token", requestBody, null);
        //3、解析返回结果
        if (jsonObject == null) {
            throw new OpenApiBusinessException("","获取token失败，请确认网址" + apiHost + " 是否能正常访问！");
        }
        JSONObject jsonObjectBody = jsonObject.getJSONObject("body");
        if (jsonObjectBody.get("error_msg") != null) {
            throw new OpenApiBusinessException("",jsonObjectBody.get("error_msg").toString());
        }
        token = jsonObjectBody.getString("access_token");
        expiresIn = jsonObjectBody.getLong("expires_in");
        lastTokenTime = System.currentTimeMillis();
        refreshToken = jsonObjectBody.getString("refresh_token");
    }

    /**
     * 刷新token
     */
    private void refreshToken() {
        //refresh_token
        //1、body数据准备
        JSONObject requestBody = new JSONObject();
        requestBody.put("grant_type", "refresh_token");
        requestBody.put("refresh_token", refreshToken);
        //2、发送请求
        JSONObject jsonObject = TtkHttpUtil.post(apiHost + "/edf/oauth2/access_token", requestBody, null);
        //3、解析返回结果
        JSONObject jsonObjectBody = jsonObject.getJSONObject("body");
        if (jsonObjectBody.get("error_msg") != null) {
            throw new OpenApiBusinessException("",jsonObjectBody.get("error_msg").toString());
        }
        token = jsonObjectBody.getString("access_token");
        expiresIn = jsonObjectBody.getLong("expires_in");
        lastTokenTime = System.currentTimeMillis();
        refreshToken = jsonObjectBody.getString("refresh_token");
    }

    /**
     * 获取验证码
     *
     * @param orgId orgId
     * @return JSONObject
     */
    public JSONObject getAuthCode(String orgId) {

        //1、body数据准备
        JSONObject requestBody = new JSONObject();
        requestBody.put("userId", "0");
        requestBody.put("orgId", orgId);
        //2、发送请求
        Map<String, String> headerMap = new HashMap<String, String>();
        return TtkHttpUtil.post(apiHost + "/edf/oauth2/getAuthCode?access_token=" + getAccessToken(), requestBody, headerMap);

    }


    /**
     * json串方式获取验证码
     *
     * @param jsonStringData jsonStringData
     * @return JSONObject
     */
    public JSONObject getAuthCodeWithJson(String jsonStringData) {

        //1、body数据准备
//        JSONObject requestBody = new JSONObject();
//        requestBody.put("userId", "0");
//        requestBody.put("orgId", orgId);

        JSONObject requestBody = JSONObject.parseObject(jsonStringData);
        if (requestBody.getString("userId") == null || !"0".equals(requestBody.getString("userId"))) {
            requestBody.put("userId", "0");
        }
        //2、发送请求
        Map<String, String> headerMap = new HashMap<String, String>();
        return TtkHttpUtil.post(apiHost + "/edf/oauth2/getAuthCode?access_token=" + getAccessToken(), requestBody, headerMap);

    }

    /**
     * 创建企业
     *
     * @param jsonStringData jsonStringData
     * @return JSONObject
     */
    public JSONObject createOrg(String jsonStringData) {
        //1、body数据准备
        JSONObject requestBody = JSONObject.parseObject(jsonStringData);
        //2、发送请求
        try {
            return TtkHttpUtil.postRestfulRequest(apiHost + "/openapi/basicData/createOrg", getAccessToken(), appSecret, requestBody, null);
        } catch (Exception e) {
            throw new OpenApiBusinessException("",e.getMessage());
        }

    }

    /**
     * 创建企业(代账云专用)
     *
     * @param jsonStringData jsonStringData
     * @return JSONObject
     */
    public JSONObject createOrgForDaiZhangCloud(String jsonStringData) {
        //1、body数据准备
        JSONObject requestBody = JSONObject.parseObject(jsonStringData);
        //2、发送请求
        try {
            return TtkHttpUtil.postRestfulRequest(apiHost + "/openapi/basicData/createOrgForDaiZhangCloud", getAccessToken(), appSecret, requestBody, null);
        } catch (Exception e) {
            throw new OpenApiBusinessException("",e.getMessage());
        }

    }

    /**
     * 保存纳企业税基本信息
     *
     * @param jsonStringData jsonStringData
     * @return JSONObject
     */
    public JSONObject saveNsxx(String jsonStringData) {
        //1、body数据准备
        JSONObject requestBody = JSONObject.parseObject(jsonStringData);
        //2、发送请求
        try {
            return TtkHttpUtil.postRestfulRequest(apiHost + "/openapi/basicData/saveNsxx", getAccessToken(), appSecret, requestBody, null);
        } catch (Exception e) {
            throw new OpenApiBusinessException("",e.getMessage());
        }

    }


    /**
     * 网报账号是否已验证通过
     *
     * @param jsonStringData jsonStringData
     * @return JSONObject
     */
    public JSONObject hasReadSJInfo(String jsonStringData) {
        //1、body数据准备
        JSONObject requestBody = JSONObject.parseObject(jsonStringData);
        try {
            return TtkHttpUtil.postRestfulRequest(apiHost + "/openapi/basicData/hasReadSJInfo", getAccessToken(), appSecret, requestBody, null);
        } catch (Exception e) {
            throw new OpenApiBusinessException("",e.getMessage());
        }
    }

    /**
     * 保存 网报账号信息
     *
     * @param jsonStringData jsonStringData
     * @return JSONObject
     */
    public JSONObject saveTaxLoginInfo(String jsonStringData) {
        //1、body数据准备
        JSONObject requestBody = JSONObject.parseObject(jsonStringData);

        try {
            if (requestBody.getJSONObject("dlxxDto") != null) {
                String before = requestBody.getJSONObject("dlxxDto").getString("DLMM");
                if (before != null) {
                    //对密码信息加密
                    requestBody.getJSONObject("dlxxDto").put("DLMM", TtkOpenApiDesUtil.encryption(before));
                }
            }

            return TtkHttpUtil.postRestfulRequest(apiHost + "/openapi/basicData/saveTaxLoginInfo", getAccessToken(), appSecret, requestBody, null);
        } catch (Exception e) {
            throw new OpenApiBusinessException("",e.getMessage());
        }
    }


    /**
     * 保存 网报账号信息
     *
     * @param jsonStringData jsonStringData
     * @return JSONObject
     */
    public JSONObject saveTaxLoginInfoForPrivateCloud(String jsonStringData) {
        //1、body数据准备
        JSONObject requestBody = JSONObject.parseObject(jsonStringData);

        try {
            if (requestBody.getJSONObject("dlxxDto") != null) {
                String before = requestBody.getJSONObject("dlxxDto").getString("DLMM");
                if (before != null) {
                    //对密码信息加密
                    requestBody.getJSONObject("dlxxDto").put("DLMM", TtkOpenApiDesUtil.encryption(before));
                }
            }

            return TtkHttpUtil.postRestfulRequest(apiHost + "/openapi/basicData/saveTaxLoginInfoForPrivateCloud", getAccessToken(), appSecret, requestBody, null);
        } catch (Exception e) {

            return null;
        }
    }

    /**
     * 查询企业详细信息
     *
     * @param jsonStringData jsonStringData
     * @return JSONObject
     */
    public JSONObject queryOrgDetailInfo(String jsonStringData) {
        //1、body数据准备
        JSONObject requestBody = JSONObject.parseObject(jsonStringData);
        try {
            return TtkHttpUtil.postRestfulRequest(apiHost + "/openapi/basicData/queryOrgDetailInfo", getAccessToken(), appSecret, requestBody, null);
        } catch (Exception e) {
            throw new OpenApiBusinessException("",e.getMessage());
        }
    }


    /**
     * 获取发票
     *
     * @param jsonStringData jsonStringData
     * @return JSONObject
     */
    public JSONObject fetchInvoice(String jsonStringData) {

        //1、body数据准备
        JSONObject requestBody = JSONObject.parseObject(jsonStringData);
        //2、发送请求
        JSONObject jsonObject = null;
        try {
            return TtkHttpUtil.postRestfulRequest(apiHost + "/openapi/invoice/fetchInvoice", getAccessToken(), appSecret, requestBody, null);
        } catch (Exception e) {
            throw new OpenApiBusinessException("",e.getMessage());
        }

    }

    /**
     * 获取发票，异步获取，能避免发票数量大、网速慢导致的超时问题
     *
     * @param jsonStringData jsonStringData
     * @return JSONObject
     */
    public JSONObject fetchInvoiceAsync(String jsonStringData) {

        //1、body数据准备
        JSONObject requestBody = JSONObject.parseObject(jsonStringData);
        //2、发送请求
        try {
            JSONObject jsonObject = TtkHttpUtil.postRestfulRequest(apiHost + "/openapi/invoice/collecteDataAsync", getAccessToken(), appSecret, requestBody, null);
            if (!"0".equals(jsonObject.getJSONObject("head").getString("errorCode"))) {
                //有异常
                logger.severe("采集请求异常");
                return jsonObject;
            } else {
                String seq = jsonObject.getString("body");
                logger.severe("采集发票流水号：" + seq);
                requestBody = JSONObject.parseObject("{\"orgId\":" + requestBody.getLong("orgId") + ",\"seq\":" + seq + "}");
                while (true) {
                    //休眠2秒钟
                    TimeUnit.SECONDS.sleep(2);
                    //循环查询结果
                    JSONObject result = TtkHttpUtil.postRestfulRequest(apiHost + "/openapi/invoice/asyncRequestResult", getAccessToken(), appSecret, requestBody, null);
                    if ("0".equals(result.getJSONObject("head").getString("errorCode"))) {
                        //采集有结果了
                        logger.severe("发票采集完毕");
                        return result;
                    } else {
                        if (result.getJSONObject("head").getString("errorMsg").contains("请求尚未返回")) {
                            //还没采集完，当前线程休眠2秒钟，然后继续查询结果
                            logger.severe("发票采集中");
                        } else {
                            logger.severe("发票采集异常");
                            return result;
                        }

                    }
                }
            }
        } catch (Exception e) {
            throw new OpenApiBusinessException("",e.getMessage());
        }

    }

    /**
     * 私有云用
     *
     * @param jsonStringData jsonStringData
     * @return JSONObject
     */
    public JSONObject fetchInvoiceAsyncForPrivateCloud(String jsonStringData) {

        //1、body数据准备
        JSONObject requestBody = JSONObject.parseObject(jsonStringData);
        //2、发送请求
        try {
            JSONObject jsonObject = TtkHttpUtil.postRestfulRequest(apiHost + "/openapi/invoice/collecteDataAsyncForPrivateCloud", getAccessToken(), appSecret, requestBody, null);
            if (!"0".equals(jsonObject.getJSONObject("head").getString("errorCode"))) {
                //有异常
                logger.severe("采集请求异常");
                return jsonObject;
            } else {
                String seq = jsonObject.getString("body");
                logger.severe("采集发票流水号：" + seq);
                requestBody = JSONObject.parseObject("{\"orgId\":" + requestBody.getLong("orgId") + ",\"seq\":" + seq + "}");
                while (true) {
                    //休眠2秒钟
                    TimeUnit.SECONDS.sleep(2);
                    //循环查询结果
                    JSONObject result = TtkHttpUtil.postRestfulRequest(apiHost + "/openapi/invoice/asyncRequestResultForPrivateCloud", getAccessToken(), appSecret, requestBody, null);
                    if ("0".equals(result.getJSONObject("head").getString("errorCode"))) {
                        //采集有结果了
                        logger.severe("发票采集完毕");
                        return result;
                    } else {
                        if (result.getJSONObject("head").getString("errorMsg").contains("请求尚未返回")) {
                            //还没采集完，当前线程休眠2秒钟，然后继续查询结果
                            logger.severe("发票采集中");
                        } else {
                            logger.severe("发票采集异常");
                            return result;
                        }

                    }
                }
            }
        } catch (Exception e) {
            throw new OpenApiBusinessException("",e.getMessage());
        }

    }

    /**
     * 批量采集发票
     *
     * @param jsonStringData jsonStringData
     * @return JSONObject
     */
    public JSONObject collectInvoiceBatch(String jsonStringData) {

        //1、body数据准备
        JSONObject requestBody = JSONObject.parseObject(jsonStringData);
        //2、发送请求
        try {
            return TtkHttpUtil.postRestfulRequest(apiHost + "/openapi/invoice/collectBatch", getAccessToken(), appSecret, requestBody, null);
        } catch (Exception e) {
            throw new OpenApiBusinessException("",e.getMessage());
        }

    }

    /**
     * 获取发票统计信息
     *
     * @param jsonStringData jsonStringData
     * @return JSONObject
     */
    public JSONObject queryInvoiceSummary(String jsonStringData) {

        //1、body数据准备
        JSONObject requestBody = JSONObject.parseObject(jsonStringData);
        //2、发送请求
        try {
            return TtkHttpUtil.postRestfulRequest(apiHost + "/openapi/invoice/querySummary", getAccessToken(), appSecret, requestBody, null);
        } catch (Exception e) {
            throw new OpenApiBusinessException("",e.getMessage());
        }

    }

    /**
     * 获取会计准则
     *
     * @param jsonStringData jsonStringData
     * @return JSONObject
     */
    @Deprecated
    public JSONObject queryAccountStandardId(String jsonStringData) {

        //1、body数据准备
        JSONObject requestBody = JSONObject.parseObject(jsonStringData);
        //2、发送请求
        JSONObject jsonObject = null;
        try {
            return TtkHttpUtil.postRestfulRequest(apiHost + "/openapi/taxReport/queryAccountStandardId", getAccessToken(), appSecret, requestBody, null);
        } catch (Exception e) {
            throw new OpenApiBusinessException("",e.getMessage());
        }
    }

    /**
     * 获取会计准则和申报周期
     *
     * @param jsonStringData jsonStringData
     * @return JSONObject
     */
    public JSONObject queryFinancialDeclarationBasicInfo(String jsonStringData) {

        //1、body数据准备
        JSONObject requestBody = JSONObject.parseObject(jsonStringData);
        //2、发送请求
        JSONObject jsonObject = null;
        try {
            return TtkHttpUtil.postRestfulRequest(apiHost + "/openapi/taxReport/queryFinancialDeclarationBasicInfo", getAccessToken(), appSecret, requestBody, null);
        } catch (Exception e) {
            throw new OpenApiBusinessException("",e.getMessage());
        }
    }

    /**
     * 上传财报数据
     *
     * @param jsonStringData jsonStringData
     * @return JSONObject
     */
    public JSONObject writeFinancialReportData(String jsonStringData) {

        //1、body数据准备
        JSONObject requestBody = JSONObject.parseObject(jsonStringData);
        //2、发送请求
        try {
            return TtkHttpUtil.postRestfulRequest(apiHost + "/openapi/taxReport/writeFinancialReportData", getAccessToken(), appSecret, requestBody, null);
        } catch (Exception e) {
            throw new OpenApiBusinessException("",e.getMessage());
        }

    }


    /**
     * 上传税报数据
     *
     * @param jsonStringData jsonStringData
     * @return JSONObject
     */
    public JSONObject writeValueAddedTaxData(String jsonStringData) {

        //1、body数据准备
        JSONObject requestBody = JSONObject.parseObject(jsonStringData);
        //2、发送请求
        try {
            return TtkHttpUtil.postRestfulRequest(apiHost + "/openapi/taxReport/writeValueAddedTaxData", getAccessToken(), appSecret, requestBody, null);
        } catch (Exception e) {
            throw new OpenApiBusinessException("",e.getMessage());
        }

    }
    /**
     * 上传税报数据 xml数据格式
     *
     * @param jsonStringData jsonStringData
     * @return JSONObject
     */
    public JSONObject writeValueAddedTaxXmlData(String jsonStringData) {

        //1、body数据准备
        JSONObject requestBody = JSONObject.parseObject(jsonStringData);
        //2、发送请求
        try {
            return TtkHttpUtil.postRestfulRequest(apiHost + "/openapi/taxReport/writeValueAddedTaxXmlData", getAccessToken(), appSecret, requestBody, null);
        } catch (Exception e) {
            throw new OpenApiBusinessException("",e.getMessage());
        }

    }

    /**
     * 获取企业信息页面访问地址
     *
     * @param orgId orgId
     * @return String
     */
    public String getWebUrlForCompanyInformation(String orgId) {

        JSONObject ttkResultDto = getAuthCode(orgId);
        String errorCode = ttkResultDto.getJSONObject("head").getString("errorCode");
        if ("0".equals(errorCode)) {
            String body = ttkResultDto.getString("body");
            return webHost + "/#/edfx-app-root/simplelogin?appkey=" + appKey + "&page=edfx-app-org&code=" + body;
        } else {
            if ("10000".equals(errorCode)) {
                this.token = null;
                throw new OpenApiBusinessException("","token解析失败，sdk刷新token");
            } else {
                this.token = null;
                throw new OpenApiBusinessException("","获取验证码时遇到错误,错误码:" + errorCode + "；错误信息：" + ttkResultDto.getJSONObject("head").getString("errorMsg"));
            }
        }
    }


    /**
     * 获取税务申报页面访问地址
     *
     * @param orgId orgId
     * @return String
     */
    public String getWebUrlForShenBao(String orgId) {
        JSONObject ttkResultDto = getAuthCode(orgId);
        String errorCode = ttkResultDto.getJSONObject("head").getString("errorCode");
        if ("0".equals(errorCode)) {
            String body = ttkResultDto.getString("body");
            return webHost + "/#/edfx-app-root/simplelogin?appkey=" + appKey + "&page=ttk-taxapply-app-taxlist&code=" + body;
        } else {
            if ("10000".equals(errorCode)) {
                this.token = null;
                throw new OpenApiBusinessException("","token解析失败，sdk刷新token");
            } else {
                this.token = null;
                throw new OpenApiBusinessException("","获取验证码时遇到错误,错误码:" + errorCode + "；错误信息：" + ttkResultDto.getJSONObject("head").getString("errorMsg"));
            }
        }
    }

    /**
     * 获取税务申报页面访问地址
     *
     * @param orgId       orgId
     * @param year        year
     * @param month       month
     * @param extraParams extraParams
     * @return String
     */
    public String getWebUrlForShenBao(String orgId, Integer year, Integer month, String extraParams) {
        if (orgId == null || "".equals(orgId)) {
            throw new OpenApiBusinessException("","参数orgId不合法");
        }
        String tempStr = "";
        if (year == null || month == null || year.intValue() >= 9999 || year.intValue() <= 1900 || month.intValue() < 1 || month.intValue() > 12) {
            throw new OpenApiBusinessException("","参数year或者month不合法");
        } else {
            tempStr = "&defaultYearMonth=" + String.valueOf(year) + "-" + String.valueOf(month);
        }

        if (extraParams != null && !"".equals(extraParams.trim())) {
            tempStr = tempStr + "&extraParams=" + extraParams;
        }


        JSONObject ttkResultDto = getAuthCode(orgId);
        String errorCode = ttkResultDto.getJSONObject("head").getString("errorCode");
        if ("0".equals(errorCode)) {
            String body = ttkResultDto.getString("body");
            return webHost + "/#/edfx-app-root/simplelogin?appkey=" + appKey + "&page=ttk-taxapply-app-taxlist&code=" + body + tempStr;
        } else {
            if ("10000".equals(errorCode)) {
                this.token = null;
                throw new OpenApiBusinessException("","token解析失败，sdk刷新token");
            } else {
                this.token = null;
                throw new OpenApiBusinessException("","获取验证码时遇到错误,错误码:" + errorCode + "；错误信息：" + ttkResultDto.getJSONObject("head").getString("errorMsg"));
            }
        }
    }

    /**
     * 获取页面访问地址
     *
     * @param page   要访问网页的标识 app name，厂家提供
     * @param orgId  如果是批量相关的接口传null（比如：批量申报页，批量采集页），如果是非批量接口传对应的orgId（比如：企业信息页，申报清册页）
     * @param paramMap  参数map集合，约定好的参数，直接追加到url后面（value值会进行URL encode 编码 ）
     * @return String 完整的单点登录url地址
     */
    public String getWebUrl(String page,String orgId,Map<String,String> paramMap) {
        return getWebUrl(page, orgId, paramMap, appKey);
    }

    public String getWebUrl(String page,String orgId,Map<String,String> paramMap, String targetAppKey) {
        if (page == null || page.isEmpty()) {
            throw  new OpenApiBusinessException("","参数page不能为空");
        }
        StringBuilder urlParamsSb = new StringBuilder();

        if(paramMap!=null){
            for(String key : paramMap.keySet()){
                urlParamsSb.append("&");
                urlParamsSb.append(key);
                urlParamsSb.append("=");
                try {
                    urlParamsSb.append(URLEncoder.encode(paramMap.get(key), "utf-8"));
                } catch (UnsupportedEncodingException e) {
                    throw new OpenApiBusinessException("",e.getMessage());
                }
            }

        }


        JSONObject ttkResultDto = getAuthCode(orgId);
        String errorCode = ttkResultDto.getJSONObject("head").getString("errorCode");
        if ("0".equals(errorCode)) {
            String body = ttkResultDto.getString("body");
            String tmpAppkey = TtkStringUtil.isNullOrEmpty(targetAppKey) ? appKey : targetAppKey;
            return webHost + "/#/edfx-app-root/simplelogin?appkey=" + tmpAppkey + "&page="+page+"&code=" + body+urlParamsSb.toString();
        } else {
            if ("10000".equals(errorCode)) {
                this.token = null;
                throw  new OpenApiBusinessException("","token解析失败，请重试");
            } else {
                this.token = null;
                throw  new OpenApiBusinessException("","获取验证码时遇到错误,错误码:" + errorCode + "；错误信息：" + ttkResultDto.getJSONObject("head").getString("errorMsg"));
            }

        }
    }


    /**
     * 获取批量页面访问地址
     *
     * @param page   网页 app name ，厂家提供
     * @param orgIdList  企业orgId集合
     * @return String 完整的单点登录url地址
     */
    public String getWebUrlBatch(String page,List orgIdList) {
        if (page == null || page.isEmpty()) {
            throw  new OpenApiBusinessException("","参数page不能为空");
        }
        if (orgIdList == null || orgIdList.isEmpty()) {
            throw  new OpenApiBusinessException("","参数orgIds不能为空");
        }
        //orgIds  转String
        String orgIds = orgIdList.toString();

        try {
            orgIds = URLEncoder.encode(orgIds, "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new OpenApiBusinessException("",e.getMessage());
        }
        JSONObject ttkResultDto = getAuthCode(null);
        String errorCode = ttkResultDto.getJSONObject("head").getString("errorCode");
        if ("0".equals(errorCode)) {
            String body = ttkResultDto.getString("body");
            return webHost + "/#/edfx-app-root/simplelogin?appkey=" + appKey + "&page="+page+"&orgIds=" + orgIds + "&code=" + body;
            //return webHost + "/sso.html?appkey=" + appKey + "&page=ttk-dz-app-batchdeclaration&orgIds="+orgIds+"&code=" + body;
        } else {
            if ("10000".equals(errorCode)) {
                this.token = null;
                throw  new OpenApiBusinessException("","token解析失败，请重试");
            } else {
                this.token = null;
                throw  new OpenApiBusinessException("","获取验证码时遇到错误,错误码:" + errorCode + "；错误信息：" + ttkResultDto.getJSONObject("head").getString("errorMsg"));
            }

        }
    }

    /**
     * 获取税务批量申报页面访问地址
     *
     * @param orgIdList orgIdList
     * @return String
     */
    public String getWebUrlForShenBaoBatch(List orgIdList) {
        if (orgIdList == null || orgIdList.isEmpty()) {
            throw new OpenApiBusinessException("","参数orgIds不能为空");
        }
        //orgIds  转String
        String orgIds = orgIdList.toString();

        try {
            orgIds = URLEncoder.encode(orgIds, "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new OpenApiBusinessException("",e.getMessage());
        }
        JSONObject ttkResultDto = getAuthCode(null);
        String errorCode = ttkResultDto.getJSONObject("head").getString("errorCode");
        if ("0".equals(errorCode)) {
            String body = ttkResultDto.getString("body");
            return webHost + "/#/edfx-app-root/simplelogin?appkey=" + appKey + "&page=ttk-dz-app-batchdeclaration&orgIds=" + orgIds + "&code=" + body;
            //return webHost + "/sso.html?appkey=" + appKey + "&page=ttk-dz-app-batchdeclaration&orgIds="+orgIds+"&code=" + body;
        } else {
            if ("10000".equals(errorCode)) {
                this.token = null;
                throw new OpenApiBusinessException("","token解析失败，sdk刷新token");
            } else {
                this.token = null;
                throw new OpenApiBusinessException("","获取验证码时遇到错误,错误码:" + errorCode + "；错误信息：" + ttkResultDto.getJSONObject("head").getString("errorMsg"));
            }
        }
    }
    // http://dev.aierp.cn:8089/sso.html?appkey=10005001&page=ttk-dz-app-batchdeclaration&code=eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJbMCw1NDUzNDU0MTczNTY4MDAwLC0xLDEwNSxcImRiXzVfa2V5X3NwcmluZ1wiLDEwMDA1MDAxLG51bGwsXCJcIl0iLCJleHAiOjE1NDQ3NzYyMDMsImlhdCI6MTU0NDc3NjA4M30.a9IjxCZBGa39GuH89fuqhDBMzgzhz4ABC4tsCIeHPZNSjyE8BXygXUgx60XbrHnQkUvTJN4lCHAh7wS4P1xiCA&orgIds=%5B5719083657390080,5453454173568000%5D&extraParams=

    /**
     * 获取报税结果
     *
     * @param jsonStringData jsonStringData
     * @return JSONObject
     */
    public JSONObject getTaxResult(String jsonStringData) {
        //1、body数据准备
        JSONObject requestBody = JSONObject.parseObject(jsonStringData);
        //2、发送请求
        try {
            return TtkHttpUtil.postRestfulRequest(apiHost + "/openapi/taxReport/getTaxResult", getAccessToken(), appSecret, requestBody, null);
        } catch (Exception e) {
            throw new OpenApiBusinessException("",e.getMessage());
        }

    }


    /**
     * 获取发票
     *
     * @param jsonStringData jsonStringData
     * @return JSONObject
     */
    public JSONObject queryWuXianYiJin(String jsonStringData) {

        //1、body数据准备
        JSONObject requestBody = JSONObject.parseObject(jsonStringData);
        //2、发送请求
        JSONObject jsonObject = null;
        try {
            return TtkHttpUtil.postRestfulRequest(apiHost + "/openapi/taxReport/queryWuXianYiJin", getAccessToken(), appSecret, requestBody, null);
        } catch (Exception e) {
            throw new OpenApiBusinessException("",e.getMessage());
        }

    }

    /**
     * 查询地区是否支持发票汇总
     *
     * @param jsonStringData jsonStringData
     * @return JSONObject
     */
    public JSONObject isAreaSupportInvoice(String jsonStringData) {
        JSONObject requestBody = JSONObject.parseObject(jsonStringData);
        try {
            return TtkHttpUtil.postRestfulRequest(apiHost + "/openapi/basicData/isAreaSupportInvoice", getAccessToken(), appSecret, requestBody, null);
        } catch (Exception e) {
            throw new OpenApiBusinessException("",e.getMessage());
        }
    }


    /**
     * 设置税报默认取数方式
     *
     * @param jsonStringData jsonStringData
     * @return JSONObject
     */
    public JSONObject setDefaultTaxReportAccessType(String jsonStringData) {
        JSONObject requestBody = JSONObject.parseObject(jsonStringData);
        try {
            return TtkHttpUtil.postRestfulRequest(apiHost + "/openapi/basicData/setDefaultTaxReportAccessType", getAccessToken(), appSecret, requestBody, null);
        } catch (Exception e) {
            throw new OpenApiBusinessException("",e.getMessage());
        }
    }


    /**
     * 设置各税种取数方式
     *
     * @param jsonStringData jsonStringData
     * @return JSONObject
     */
    public JSONObject saveTaxReportAccessType(String jsonStringData) {
        JSONObject requestBody = JSONObject.parseObject(jsonStringData);
        try {
            return TtkHttpUtil.postRestfulRequest(apiHost + "/openapi/basicData/saveTaxReportAccessType", getAccessToken(), appSecret, requestBody, null);
        } catch (Exception e) {
            throw new OpenApiBusinessException("",e.getMessage());
        }
    }

    /**
     * 查询各税种取数方式
     *
     * @param jsonStringData jsonStringData
     * @return JSONObject
     */
    public JSONObject queryTaxReportAccessTypeList(String jsonStringData) {
        JSONObject requestBody = JSONObject.parseObject(jsonStringData);
        try {
            return TtkHttpUtil.postRestfulRequest(apiHost + "/openapi/basicData/queryTaxReportAccessTypeList", getAccessToken(), appSecret, requestBody, null);
        } catch (Exception e) {
            throw new OpenApiBusinessException("",e.getMessage());
        }
    }

    /**
     * 获取税务申报表XML
     *
     * @param jsonStringData jsonStringData
     * @return JSONObject
     */
    public JSONObject downloadTaxReportXML(String jsonStringData) {
        //1、body数据准备
        JSONObject requestBody = JSONObject.parseObject(jsonStringData);
        //2、发送请求
        try {
            return TtkHttpUtil.postRestfulRequest(apiHost + "/openapi/taxReport/downloadTaxReportXML", getAccessToken(), appSecret, requestBody, null);
        } catch (Exception e) {
            throw new OpenApiBusinessException("",e.getMessage());
        }

    }

    /**
     * 获取税务申报表PDF
     *
     * @param jsonStringData jsonStringData
     * @return JSONObject
     */
    public JSONObject downloadTaxReportPDF(String jsonStringData) {
        //1、body数据准备
        JSONObject requestBody = JSONObject.parseObject(jsonStringData);
        //2、发送请求
        try {
            return TtkHttpUtil.postRestfulRequest(apiHost + "/openapi/taxReport/downloadTaxReportPDF", getAccessToken(), appSecret, requestBody, null);
        } catch (Exception e) {
            throw new OpenApiBusinessException("",e.getMessage());
        }

    }

    /**
     * 获取必填表单名单信息
     *
     * @param jsonStringData jsonStringData
     * @return JSONObject
     */
    public JSONObject queryRequiredTaxTables(String jsonStringData) {
        //1、body数据准备
        JSONObject requestBody = JSONObject.parseObject(jsonStringData);
        //2、发送请求
        try {
            return TtkHttpUtil.postRestfulRequest(apiHost + "/openapi/taxReport/queryRequiredTaxTables", getAccessToken(), appSecret, requestBody, null);
        } catch (Exception e) {
            throw new OpenApiBusinessException("",e.getMessage());
        }
    }

    /**
     * 查询纳税人未缴税款信息查询
     *
     * @param jsonStringData jsonStringData
     * @return JSONObject
     */
    public JSONObject queryUnpaidInfo(String jsonStringData) {
        //1、body数据准备
        JSONObject requestBody = JSONObject.parseObject(jsonStringData);
        //2、发送请求
        try {
            return TtkHttpUtil.postRestfulRequest(apiHost + "/openapi/taxReport/queryUnpaidInfo", getAccessToken(), appSecret, requestBody, null);
        } catch (Exception e) {
            throw new OpenApiBusinessException("",e.getMessage());
        }
    }

    public JSONObject createUsersAndOrg(String jsonStringData) {
        //1、body数据准备
        JSONObject requestBody = JSONObject.parseObject(jsonStringData);
        //2、发送请求
        try {
            return TtkHttpUtil.postRestfulRequest(apiHost + "/openapi/es/createUsersAndOrg", getAccessToken(), appSecret, requestBody, null);
        } catch (Exception e) {
            throw new OpenApiBusinessException("",e.getMessage());
        }
    }

    public JSONObject updateUserRole(String jsonStringData) {
        //1、body数据准备
        JSONObject requestBody = JSONObject.parseObject(jsonStringData);
        //2、发送请求
        try {
            return TtkHttpUtil.postRestfulRequest(apiHost + "/openapi/es/updateUserRole", getAccessToken(), appSecret, requestBody, null);
        } catch (Exception e) {
            throw new OpenApiBusinessException("",e.getMessage());
        }
    }


    public JSONObject queryWorkbenchTotalInfo(String jsonStringData) {
        //1、body数据准备
        JSONObject requestBody = JSONObject.parseObject(jsonStringData);
        //2、发送请求
        try {
            return TtkHttpUtil.postRestfulRequest(apiHost + "/openapi/es/queryWorkbenchTotalInfo", getAccessToken(), appSecret, requestBody, null);
        } catch (Exception e) {
            throw new OpenApiBusinessException("",e.getMessage());
        }
    }

    public JSONObject queryWorkbenchDetailInfo(String jsonStringData) {
        //1、body数据准备
        JSONObject requestBody = JSONObject.parseObject(jsonStringData);
        //2、发送请求
        try {
            return TtkHttpUtil.postRestfulRequest(apiHost + "/openapi/es/queryWorkbenchDetailInfo", getAccessToken(), appSecret, requestBody, null);
        } catch (Exception e) {
            throw new OpenApiBusinessException("",e.getMessage());
        }
    }

    public JSONObject calendarQuery(String jsonStringData) {
        //1、body数据准备
        JSONObject requestBody = JSONObject.parseObject(jsonStringData);
        //2、发送请求
        try {
            return TtkHttpUtil.postRestfulRequest(apiHost + "/openapi/es/calendarQuery", getAccessToken(), appSecret, requestBody, null);
        } catch (Exception e) {
            throw new OpenApiBusinessException("",e.getMessage());
        }
    }

    public JSONObject createBatchUser(String jsonStringData) {
        //1、body数据准备
        JSONObject requestBody = JSONObject.parseObject(jsonStringData);
        //2、发送请求
        try {
            return TtkHttpUtil.postRestfulRequest(apiHost + "/openapi/es/createBatchUser", getAccessToken(), appSecret, requestBody, null);
        } catch (Exception e) {
            throw new OpenApiBusinessException("",e.getMessage());
        }
    }

    public JSONObject updateUserEnable(String jsonStringData) {
        //1、body数据准备
        JSONObject requestBody = JSONObject.parseObject(jsonStringData);
        //2、发送请求
        try {
            return TtkHttpUtil.postRestfulRequest(apiHost + "/openapi/es/updateUserEnable", getAccessToken(), appSecret, requestBody, null);
        } catch (Exception e) {
            throw new OpenApiBusinessException("",e.getMessage());
        }
    }

    public JSONObject updateJcyyCustomOrgState(String jsonStringData) {
        //1、body数据准备
        JSONObject requestBody = JSONObject.parseObject(jsonStringData);
        //2、发送请求
        try {
            return TtkHttpUtil.postRestfulRequest(apiHost + "/openapi/es/updateJcyyCustomOrgState", getAccessToken(), appSecret, requestBody, null);
        } catch (Exception e) {
            throw new OpenApiBusinessException("",e.getMessage());
        }
    }


    public JSONObject updateUser(String jsonStringData) {
        //1、body数据准备
        JSONObject requestBody = JSONObject.parseObject(jsonStringData);
        //2、发送请求
        try {
            return TtkHttpUtil.postRestfulRequest(apiHost + "/openapi/es/updateUser", getAccessToken(), appSecret, requestBody, null);
        } catch (Exception e) {
            throw new OpenApiBusinessException("",e.getMessage());
        }
    }

    public JSONObject updateOrg(String jsonStringData) {
        //1、body数据准备
        JSONObject requestBody = JSONObject.parseObject(jsonStringData);
        //2、发送请求
        try {
            return TtkHttpUtil.postRestfulRequest(apiHost + "/openapi/basicData/updateOrg", getAccessToken(), appSecret, requestBody, null);
        } catch (Exception e) {
            throw new OpenApiBusinessException("",e.getMessage());
        }
    }


    public JSONObject queryOrgBaseInfo(String jsonStringData) {
        //1、body数据准备
        JSONObject requestBody = JSONObject.parseObject(jsonStringData);
        //2、发送请求
        try {
            return TtkHttpUtil.postRestfulRequest(apiHost + "/openapi/es/queryOrgBaseInfo", getAccessToken(), appSecret, requestBody, null);
        } catch (Exception e) {
            throw new OpenApiBusinessException("",e.getMessage());
        }
    }

    /**
     * 通过该接口查询客户申报进度信息
     *
     * @param jsonStringData jsonStringData
     * @return JSONObject
     */
    public JSONObject queryDeclarationProgress(String jsonStringData) {
        //1、body数据准备
        JSONObject requestBody = JSONObject.parseObject(jsonStringData);

        //2、发送请求
        try {
            return TtkHttpUtil.postRestfulRequest(apiHost + "/openapi/es/queryDeclarationProgress", getAccessToken(), appSecret, requestBody, null);
        } catch (Exception e) {
            throw new OpenApiBusinessException("",e.getMessage());
        }
    }

    /**
     * 给代账的代理机构批量添加用户
     *
     * @param jsonStringData jsonStringData
     * @return JSONObject
     */
    public JSONObject createDljgUsersForDzgl(String jsonStringData) {
        //1、body数据准备
        JSONObject requestBody = JSONObject.parseObject(jsonStringData);

        //2、发送请求
        try {
            return TtkHttpUtil.postRestfulRequest(apiHost + "/openapi/es/createDljgUsersForDzgl", getAccessToken(), appSecret, requestBody, null);
        } catch (Exception e) {
            throw new OpenApiBusinessException("",e.getMessage());
        }
    }

    /**
     * 通过该接口修改企业报税是否全部完成状态
     *
     * @param jsonStringData jsonStringData
     * @return JSONObject
     */
    public JSONObject finishDeclaration(String jsonStringData) {
        //1、body数据准备
        JSONObject requestBody = JSONObject.parseObject(jsonStringData);

        //2、发送请求
        try {
            return TtkHttpUtil.postRestfulRequest(apiHost + "/openapi/es/finishDeclaration", getAccessToken(), appSecret, requestBody, null);
        } catch (Exception e) {
            throw new OpenApiBusinessException("",e.getMessage());
        }
    }

    /**
     * 删除ES客户企业
     *
     * @param jsonStringData jsonStringData
     * @return JSONObject
     */
    public JSONObject deleteCustomerOrg(String jsonStringData) {
        //1、body数据准备
        JSONObject requestBody = JSONObject.parseObject(jsonStringData);

        //2、发送请求
        try {
            return TtkHttpUtil.postRestfulRequest(apiHost + "/openapi/es/deleteCustomerOrg", getAccessToken(), appSecret, requestBody, null);
        } catch (Exception e) {
            throw new OpenApiBusinessException("",e.getMessage());
        }
    }

    /**
     * 初始化ES客户企业检测
     *
     * @param jsonStringData jsonStringData
     * @return JSONObject
     */
    public JSONObject canInitializeCustomerOrg(String jsonStringData) {
        //1、body数据准备
        JSONObject requestBody = JSONObject.parseObject(jsonStringData);

        //2、发送请求
        try {
            return TtkHttpUtil.postRestfulRequest(apiHost + "/openapi/es/canInitializeCustomerOrg", getAccessToken(), appSecret, requestBody, null);
        } catch (Exception e) {
            throw new OpenApiBusinessException("",e.getMessage());
        }
    }

    /**
     * 同步客户账套名称
     *
     * @param jsonStringData jsonStringData
     * @return JSONObject
     */
    public JSONObject syncCustomerOrgName(String jsonStringData) {
        //1、body数据准备
        JSONObject requestBody = JSONObject.parseObject(jsonStringData);

        //2、发送请求
        try {
            return TtkHttpUtil.postRestfulRequest(apiHost + "/openapi/es/syncCustomerOrgName", getAccessToken(), appSecret, requestBody, null);
        } catch (Exception e) {
            throw new OpenApiBusinessException("",e.getMessage());
        }
    }

//    public JSONObject queryMenuForDzgl(String jsonStringData) {
//        //1、body数据准备
//        JSONObject requestBody = JSONObject.parseObject(jsonStringData);
//        //2、发送请求
//        try {
//            return TtkHttpUtil.postRestfulRequest(apiHost + "/openapi/es/queryMenuForDzgl", getAccessToken(), appSecret, requestBody, null);
//        } catch (Exception e) {
//            throw new OpenApiBusinessException("",e.getMessage());
//        }
//    }


    /**
     * 通用接口
     *
     * @param path          请求url ，举例：/openapi/taxReport/queryRequiredTaxTables ，/openapi/basicData/createOrg
     * @param jsonParameter 参数json串
     * @return JSONObject
     */
    public JSONObject rest(String path, String jsonParameter) {
        if (path == null) {
            throw new OpenApiBusinessException("","path不能为空");
        }
        if (jsonParameter == null) {
            throw new OpenApiBusinessException("","jsonParameter不能为空");
        }


        //1、body数据准备
        JSONObject requestBody = JSONObject.parseObject(jsonParameter);
        //2、发送请求
        try {
            return TtkHttpUtil.postRestfulRequest(apiHost + path.trim(), getAccessToken(), appSecret, requestBody, null);
        } catch (Exception e) {
            throw new OpenApiBusinessException("",e.getMessage());
        }

    }

    public String getApiHost() {
        return apiHost;
    }

    public void setApiHost(String apiHost) {
        this.apiHost = apiHost;
    }

    public String getWebHost() {
        return webHost;
    }

    public void setWebHost(String webHost) {
        this.webHost = webHost;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    /**
     * token 解析错误代码情况下 清除本地token缓存
     *
     * @param jsonObject
     */
    private void clearLocalToken(JSONObject jsonObject) {
        if (jsonObject != null && jsonObject.getJSONObject("head") != null) {
            if ("10000".equals(jsonObject.getJSONObject("head").getString("errorCode"))) {
                this.token = null;
            }
        }

    }

    /**
     * 获取报税的饼图数据
     * 2019-03-25 14:29
     *
     * @param jsonStringData jsonStringData
     * @return JSONObject
     * @author huangzhong
     */
    public JSONObject getEntryChartDtoForTax(String jsonStringData) {
        //1、body数据准备
        JSONObject requestBody = JSONObject.parseObject(jsonStringData);
        //2、发送请求
        try {
            return TtkHttpUtil.postRestfulRequest(apiHost + "/openapi/basicData/getEntryChartDtoForTax", getAccessToken(), appSecret, requestBody, null);
        } catch (Exception e) {
            throw new OpenApiBusinessException("",e.getMessage());
        }
    }

    /**
     * 获取报税明细数据
     * 2019-03-25 14:29
     *
     * @param jsonStringData jsonStringData
     * @return JSONObject
     * @author huangzhong
     */
    public JSONObject getTaxHandleStatusList(String jsonStringData) {
        //1、body数据准备
        JSONObject requestBody = JSONObject.parseObject(jsonStringData);
        //2、发送请求
        try {
            return TtkHttpUtil.postRestfulRequest(apiHost + "/openapi/basicData/getTaxHandleStatusList", getAccessToken(), appSecret, requestBody, null);
        } catch (Exception e) {
            throw new OpenApiBusinessException("",e.getMessage());
        }
    }

    /**
     * 获取报税状态数据
     * 2019-05-15 14:29
     *
     * @param jsonStringData jsonStringData
     * @return JSONObject
     * @author huangzhong
     */
    public JSONObject getTaxClosedStatusList(String jsonStringData) {
        //1、body数据准备
        JSONObject requestBody = JSONObject.parseObject(jsonStringData);
        //2、发送请求
        try {
            return TtkHttpUtil.postRestfulRequest(apiHost + "/openapi/basicData/getTaxClosedStatusList", getAccessToken(), appSecret, requestBody, null);
        } catch (Exception e) {
            throw new OpenApiBusinessException("",e.getMessage());
        }
    }

    /**
     * 修改web代账的代理机构
     *
     */
    public JSONObject updateEsDljgOrg(String jsonStringData) {
        //1、body数据准备
        JSONObject requestBody = JSONObject.parseObject(jsonStringData);
        //2、发送请求
        try {
            return TtkHttpUtil.postRestfulRequest(apiHost + "/openapi/es/updateEsDljgOrg", getAccessToken(), appSecret, requestBody, null);
        } catch (Exception e) {
            throw new OpenApiBusinessException("",e.getMessage());
        }
    }

}
