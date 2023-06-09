package com.changhong.sei.cicd.entity;

import com.changhong.sei.core.entity.BaseAuditableEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * 部署阶段(DeployStage)实体类
 *
 * @author sei
 * @since 2020-11-23 08:34:01
 */
@Entity
@Table(name = "cicd_deploy_stage")
@DynamicInsert
@DynamicUpdate
public class DeployStage extends BaseAuditableEntity implements Serializable {
    private static final long serialVersionUID = 343437188083584989L;
    /**
     * 阶段名称
     */
    @Column(name = "name")
    private String name;
    /**
     * 是否冻结
     */
    @Column(name = "frozen")
    private Boolean frozen = Boolean.FALSE;

    /**
     * 脚本
     */
    @Column(name = "playscript")
    private String playscript;
    /**
     * 描述说明
     */
    @Column(name = "remark")
    private String remark;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getFrozen() {
        return frozen;
    }

    public void setFrozen(Boolean frozen) {
        this.frozen = frozen;
    }

    public String getPlayscript() {
        return playscript;
    }

    public void setPlayscript(String playscript) {
        this.playscript = playscript;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

}