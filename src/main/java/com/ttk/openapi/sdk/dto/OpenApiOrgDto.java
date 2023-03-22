package com.ttk.openapi.sdk.dto;


import java.io.Serializable;

/**
 *创建企业信息
 */
public class OpenApiOrgDto implements Serializable {



    /**
     * 企业ID
     */
    private Long orgId;

    /**
     * 创建者id（用户id）
     */
    private Long creator;

    /**
     * 企业基本信息
     */
    private String name;

    /**
     * 纳税人身份(41：一般纳税人；42：小规模纳税人)
     */
    private Long vatTaxpayer;
    /**
     * 会计准则
     */
    private Long accountingStandards;
    /**
     * 会计启用年
     */
    private String enabledYear;
    /**
     * 会计启用月
     */
    private String enabledMonth;

    /**
     * 企业关联app
     */
    private Long appId;

    /**
     * 来源appkey
     */
    private Long appKey;

    /**
     * CA证书内容
     */
    private String caContent;


    /**
     * 数据源
     */
    private String dbKey;

    public String getDbKey() {
        return dbKey;
    }

    public void setDbKey(String dbKey) {
        this.dbKey = dbKey;
    }

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }
    public String getCaContent() {
        return caContent;
    }

    public void setCaContent(String caContent) {
        this.caContent = caContent;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getVatTaxpayer() {
        return vatTaxpayer;
    }

    public void setVatTaxpayer(Long vatTaxpayer) {
        this.vatTaxpayer = vatTaxpayer;
    }

    public Long getAccountingStandards() {
        return accountingStandards;
    }

    public void setAccountingStandards(Long accountingStandards) {
        this.accountingStandards = accountingStandards;
    }

    public String getEnabledYear() {
        return enabledYear;
    }

    public void setEnabledYear(String enabledYear) {
        this.enabledYear = enabledYear;
    }

    public String getEnabledMonth() {
        return enabledMonth;
    }

    public void setEnabledMonth(String enabledMonth) {
        this.enabledMonth = enabledMonth;
    }
    public Long getCreator() {
        return creator;
    }

    public void setCreator(Long creator) {
        this.creator = creator;
    }
    public Long getAppId() {
        return appId;
    }

    public void setAppId(Long appId) {
        this.appId = appId;
    }

    public Long getAppKey() {
        return appKey;
    }

    public void setAppKey(Long appKey) {
        this.appKey = appKey;
    }

}
