package com.changhong.sei.deploy.entity;

import com.changhong.sei.core.entity.BaseEntity;
import com.changhong.sei.deploy.dto.ApplyType;
import com.changhong.sei.deploy.dto.ApprovalStatus;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 实现功能：应用申请单(视图)
 *
 * @author 马超(Vision.Mac)
 * @version 1.0.00  2020-11-30 10:15
 */
@Entity
@Table(name = "v_app_module_requisition")
public class AppModuleRequisition extends BaseEntity implements Serializable {
    private static final long serialVersionUID = -64497955636689211L;
    /**
     * 关联id
     *
     * @see ApplyType
     */
    @Column(name = "relation_id")
    private String relationId;
    /**
     * 申请人账号
     */
    @Column(name = "applicant_account")
    private String applicantAccount;
    /**
     * 申请人名称
     */
    @Column(name = "applicant_user_name")
    private String applicantUserName;
    /**
     * 申请时间
     */
    @Column(name = "application_time")
    private LocalDateTime applicationTime;
    /**
     * 审核状态
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "approval_status")
    private ApprovalStatus approvalStatus;
    /**
     * 所属应用id
     */
    @Column(name = "app_id")
    private String appId;
    /**
     * 模块代码
     */
    @Column(name = "code")
    private String code;
    /**
     * 模块名称
     */
    @Column(name = "name")
    private String name;
    /**
     * 模块版本
     */
    @Column(name = "version_")
    private String version;
    /**
     * 命名空间(包路径)
     */
    @Column(name = "name_space")
    private String nameSpace;
    /**
     * 描述说明
     */
    @Column(name = "remark")
    private String remark;

    public String getRelationId() {
        return relationId;
    }

    public void setRelationId(String relationId) {
        this.relationId = relationId;
    }

    public String getApplicantAccount() {
        return applicantAccount;
    }

    public void setApplicantAccount(String applicantAccount) {
        this.applicantAccount = applicantAccount;
    }

    public String getApplicantUserName() {
        return applicantUserName;
    }

    public void setApplicantUserName(String applicantUserName) {
        this.applicantUserName = applicantUserName;
    }

    public LocalDateTime getApplicationTime() {
        return applicationTime;
    }

    public void setApplicationTime(LocalDateTime applicationTime) {
        this.applicationTime = applicationTime;
    }

    public ApprovalStatus getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(ApprovalStatus approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getNameSpace() {
        return nameSpace;
    }

    public void setNameSpace(String nameSpace) {
        this.nameSpace = nameSpace;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
