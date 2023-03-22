package com.ttk.openapi.sdk.dto;


/**
 * @author xuchu1
 */
public class OpenApiTaxAppraisalDto  {


    /**  */
    private Long orgId;
    /**  */
    private Long id;
    private Integer year;
    private Integer month;
    /** uuid */
    private String uuid;
    /** 财税项目ID */
    private Long taxProjectId;
    /** 财税项目编码 */
    private String taxProjectCode;
    /** 财税项目类型 */
    private Integer projectType;
    /** 财税项目 */
    private String taxProjectName;
    /** 周期 */
    private String cycleId;
    /** 税额 */
    private Double taxAmount;
    /** 周期名称 */
    private String cycleName;
    /** 有效期起 */
    private String fromDate;
    /** 有效期起 */
    private String toDate;
    /** 申报日期 */
    private String voucherDate;
    /** 截止日期 */
    private String endDate;
    /** 申报状态ID */
    private Long state;
    /** 申报状态名称 */
    private String stateName;
    /** 申报回执 */
    private String receipt;
    /** 付款状态ID */
    private Long payState;
    /** 付款状态名称 */
    private String payStateName;
    /** 付款回执 */
    private String payReceipt;
    /** 会计准则ID */
    private Long accountingStandardsId;
    /** 会计准则名称 */
    private String accountingStandardsName;
    /** 是否重分类 */
    private Long reclassificationId;
    /** 重分类名称 */
    private String reclassificationName;
    /** 本系统申报 */
    private Boolean selfSystemDeclare;
    /** 是否可删除 */
    private Boolean canDel;
    /** 时间戳 */
    private String ts;
    /** 接口是否掉通 */
    private Boolean connect;

    /** 应征凭证种类代码 */
    private String yzpzzlDm;
    /** 应征凭证种类名称 */
    private String yzpzzlMc;
    /** 申报业务编码 */
    private String sbywbm;
    /** 会计准则制度代码 */
    private String kjzdzzDm;
    /** 资料报送小类代码 */
    private String zlbsxlDm;
    /** 凭证序号 */
    private String pzxh;



    //private List<TaxationAppraisalDetailDto> details;

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Long getTaxProjectId() {
        return taxProjectId;
    }

    public void setTaxProjectId(Long taxProjectId) {
        this.taxProjectId = taxProjectId;
    }

    public String getTaxProjectCode() {
        return taxProjectCode;
    }

    public void setTaxProjectCode(String taxProjectCode) {
        this.taxProjectCode = taxProjectCode;
    }

    public Integer getProjectType() {
        return projectType;
    }

    public void setProjectType(Integer projectType) {
        this.projectType = projectType;
    }

    public String getCycleId() {
        return cycleId;
    }

    public void setCycleId(String cycleId) {
        this.cycleId = cycleId;
    }

    public Double getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(Double taxAmount) {
        this.taxAmount = taxAmount;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public Long getState() {
        return state;
    }

    public void setState(Long state) {
        this.state = state;
    }

    public String getReceipt() {
        return receipt;
    }

    public void setReceipt(String receipt) {
        this.receipt = receipt;
    }

    public Long getPayState() {
        return payState;
    }

    public void setPayState(Long payState) {
        this.payState = payState;
    }

    public String getPayReceipt() {
        return payReceipt;
    }

    public void setPayReceipt(String payReceipt) {
        this.payReceipt = payReceipt;
    }

    public Long getAccountingStandardsId() {
        return accountingStandardsId;
    }

    public void setAccountingStandardsId(Long accountingStandardsId) {
        this.accountingStandardsId = accountingStandardsId;
    }

    public Long getReclassificationId() {
        return reclassificationId;
    }

    public void setReclassificationId(Long reclassificationId) {
        this.reclassificationId = reclassificationId;
    }

    public Boolean getCanDel() {
        return canDel;
    }

    public void setCanDel(Boolean canDel) {
        this.canDel = canDel;
    }

    public String getTaxProjectName() {
        return taxProjectName;
    }

    public void setTaxProjectName(String taxProjectName) {
        this.taxProjectName = taxProjectName;
    }

    public String getCycleName() {
        return cycleName;
    }

    public void setCycleName(String cycleName) {
        this.cycleName = cycleName;
    }

    public String getAccountingStandardsName() {
        return accountingStandardsName;
    }

    public void setAccountingStandardsName(String accountingStandardsName) {
        this.accountingStandardsName = accountingStandardsName;
    }

    public String getReclassificationName() {
        return reclassificationName;
    }

    public void setReclassificationName(String reclassificationName) {
        this.reclassificationName = reclassificationName;
    }

    public Boolean getSelfSystemDeclare() {
        return selfSystemDeclare;
    }

    public void setSelfSystemDeclare(Boolean selfSystemDeclare) {
        this.selfSystemDeclare = selfSystemDeclare;
    }

    public String getTs() {
        return ts;
    }

    public void setTs(String ts) {
        this.ts = ts;
    }

    public Boolean getConnect() {
        return connect;
    }

    public void setConnect(Boolean connect) {
        this.connect = connect;
    }

   
    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public String getVoucherDate() {
        return voucherDate;
    }

    public void setVoucherDate(String voucherDate) {
        this.voucherDate = voucherDate;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getPayStateName() {
        return payStateName;
    }

    public void setPayStateName(String payStateName) {
        this.payStateName = payStateName;
    }

    public String getYzpzzlDm() {
        return yzpzzlDm;
    }

    public void setYzpzzlDm(String yzpzzlDm) {
        this.yzpzzlDm = yzpzzlDm;
    }

    public String getYzpzzlMc() {
        return yzpzzlMc;
    }

    public void setYzpzzlMc(String yzpzzlMc) {
        this.yzpzzlMc = yzpzzlMc;
    }

    public String getSbywbm() {
        return sbywbm;
    }

    public void setSbywbm(String sbywbm) {
        this.sbywbm = sbywbm;
    }

    public String getKjzdzzDm() {
        return kjzdzzDm;
    }

    public void setKjzdzzDm(String kjzdzzDm) {
        this.kjzdzzDm = kjzdzzDm;
    }

    public String getZlbsxlDm() {
        return zlbsxlDm;
    }

    public void setZlbsxlDm(String zlbsxlDm) {
        this.zlbsxlDm = zlbsxlDm;
    }

    public String getPzxh() {
        return pzxh;
    }

    public void setPzxh(String pzxh) {
        this.pzxh = pzxh;
    }


}
