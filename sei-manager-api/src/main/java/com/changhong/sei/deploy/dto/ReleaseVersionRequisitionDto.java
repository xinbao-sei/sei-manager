package com.changhong.sei.deploy.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * 应用版本申请(ReleaseVersionRequisition)DTO类
 *
 * @author sei
 * @since 2020-11-26 14:45:23
 */
@ApiModel(description = "应用版本申请单DTO")
public class ReleaseVersionRequisitionDto extends RequisitionDto implements Serializable {
    private static final long serialVersionUID = -38024625578859016L;
    /**
     * 所属应用id
     */
    @ApiModelProperty(value = "所属应用id")
    private String appId;
    /**
     * 所属应用id
     */
    @ApiModelProperty(value = "所属应用")
    private String appName;
    /**
     * 模块git id
     */
    @ApiModelProperty(value = "模块git id")
    private String gitId;
    /**
     * 模块代码
     */
    @ApiModelProperty(value = "模块代码")
    private String moduleCode;
    /**
     * 模块名称
     */
    @ApiModelProperty(value = "模块名称")
    private String moduleName;
    /**
     * 引用tag
     */
    @ApiModelProperty(value = "关联tag")
    private String refTag;
    /**
     * 版本号
     */
    @ApiModelProperty(value = "版本号")
    private String version;
    /**
     * 版本主题
     */
    @ApiModelProperty(value = "版本主题")
    private String name;
    /**
     * 构建状态
     */
    @ApiModelProperty(value = "构建状态")
    private String buildStatus = BuildStatus.NOT_BUILT.name();

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getGitId() {
        return gitId;
    }

    public void setGitId(String gitId) {
        this.gitId = gitId;
    }

    public String getModuleCode() {
        return moduleCode;
    }

    public void setModuleCode(String moduleCode) {
        this.moduleCode = moduleCode;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getRefTag() {
        return refTag;
    }

    public void setRefTag(String refTag) {
        this.refTag = refTag;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBuildStatus() {
        return buildStatus;
    }

    public void setBuildStatus(String buildStatus) {
        this.buildStatus = buildStatus;
    }
}