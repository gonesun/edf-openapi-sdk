package com.ttk.openapi.sdk;

/**
 * @author lvaolin
 * 18/11/14 下午12:05
 */
public class TtkResultHead {
    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    /**
     * 错误码
     */
   private String errorCode;
    /**
     * 错误信息
     */
   private String errorMsg;





}
