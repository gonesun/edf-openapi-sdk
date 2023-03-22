package com.ttk.openapi.sdk.dto;


import java.io.Serializable;

/**
 *申报结果查询dto
 */
public class OpenApiQueryTaxResultDto implements Serializable {

    /**
     *0
     */
    private Long userId ;
    /**
     * 企业ID
     */
    private Long orgId;
    /**
     * 申报年
     */
    private String year;
    /**
     * 申报月
     */
    private String month;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }





}
