package com.ttk.openapi.sdk.dto;


import java.io.Serializable;

/**
 * 企业详细信息
 */
public class OpenApiOrgDetailDto implements Serializable{

    /**
     * 企业id
     */
    private Long orgId;
    /**
     * 企业名称
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

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }
}
