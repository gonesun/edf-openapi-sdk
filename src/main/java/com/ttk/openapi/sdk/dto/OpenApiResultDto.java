package com.ttk.openapi.sdk.dto;

import java.io.Serializable;

/**
 * 简单结果 dto 实体定义
 * @author lvaolin
 */
public class OpenApiResultDto implements Serializable {

    private static final long serialVersionUID = -5814724099405354316L;

    /** 结果 true 成功 false 失败 */
    private boolean result;

    /** 详细信息，主要是错误信息，成功后这项一般为空 */
    private String message;

    /**
     * 获取结果
     * @return 结果
     */
    public boolean isResult() {
        return result;
    }

    /**
     * 设置结果
     * @param result 结果
     */
    public void setResult(boolean result) {
        this.result = result;
    }

    /**
     * 获取详细信息
     * @return 详细信息
     */
    public String getMessage() {
        return message;
    }

    /**
     * 设置详细信息
     * @param message 详细信息
     */
    public void setMessage(String message) {
        this.message = message;
    }

}
