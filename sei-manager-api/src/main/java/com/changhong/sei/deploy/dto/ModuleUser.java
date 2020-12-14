package com.changhong.sei.deploy.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * 实现功能：应用模块用户
 *
 * @author 马超(Vision.Mac)
 * @version 1.0.00  2020-12-14 23:52
 */
@ApiModel(description = "应用模块用户")
public class ModuleUser implements Serializable {
    private static final long serialVersionUID = -4492457497726608288L;

    @ApiModelProperty(value = "gitlab项目id", required = true)
    private String gitProjectId;
    @ApiModelProperty(value = "git用户id")
    private Integer gitUserId;
    @ApiModelProperty(value = "账号", required = true)
    private String account;
    @ApiModelProperty(value = "名称")
    private String name;

    public String getGitProjectId() {
        return gitProjectId;
    }

    public void setGitProjectId(String gitProjectId) {
        this.gitProjectId = gitProjectId;
    }

    public Integer getGitUserId() {
        return gitUserId;
    }

    public void setGitUserId(Integer gitUserId) {
        this.gitUserId = gitUserId;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
