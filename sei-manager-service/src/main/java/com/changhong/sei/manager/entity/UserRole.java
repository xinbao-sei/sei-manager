package com.changhong.sei.manager.entity;

import com.changhong.sei.core.entity.BaseEntity;
import com.changhong.sei.core.entity.RelationEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * 用户角色关系表(SecUserRole)实体类
 *
 * @author sei
 * @since 2020-11-10 16:24:34
 */
@Entity
@Table(name = "sec_user_role")
@DynamicInsert
@DynamicUpdate
public class UserRole extends BaseEntity implements RelationEntity<User, Role>, Serializable {
    private static final long serialVersionUID = 667040883030050793L;

    /**
     * 功能角色
     */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User parent;
    /**
     * 功能项
     */
    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role child;

    @Override
    public User getParent() {
        return parent;
    }

    @Override
    public void setParent(User parent) {
        this.parent = parent;
    }

    @Override
    public Role getChild() {
        return child;
    }

    @Override
    public void setChild(Role child) {
        this.child = child;
    }
}