package com.changhong.sei.manager.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 实现功能：
 *
 * @author 马超(Vision.Mac)
 * @version 1.0.00  2020-11-10 16:47
 */
@ApiModel(description = "登录")
public class LoginRequest implements Serializable {
    private static final long serialVersionUID = 8880363469883497333L;
    /**
     * 用户名或邮箱或手机号
     */
    @ApiModelProperty(notes = "用户名或邮箱或手机号")
    @NotBlank(message = "用户名不能为空")
    private String account;

    /**
     * 密码
     */
    @ApiModelProperty(notes = "密码")
    @NotBlank(message = "密码不能为空")
    private String password;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
