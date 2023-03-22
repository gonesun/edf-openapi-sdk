package com.ttk.openapi.sdk.dto;


public class OpenApiTaxResultQueryRetDto  {

    private static final long serialVersionUID = -3393061230636170783L;


    private Long userId;
    private Long orgId;
    private String year;
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
