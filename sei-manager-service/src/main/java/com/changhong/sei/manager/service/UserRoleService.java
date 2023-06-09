package com.changhong.sei.manager.service;

import com.changhong.sei.core.context.ContextUtil;
import com.changhong.sei.core.dao.BaseRelationDao;
import com.changhong.sei.core.service.BaseRelationService;
import com.changhong.sei.core.service.bo.OperateResult;
import com.changhong.sei.manager.dao.UserRoleDao;
import com.changhong.sei.manager.entity.Role;
import com.changhong.sei.manager.entity.User;
import com.changhong.sei.manager.entity.UserRole;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 实现功能：用户分配的功能角色的业务逻辑实现
 */
@Service
public class UserRoleService extends BaseRelationService<UserRole, User, Role> {
    @Autowired
    private UserRoleDao dao;

    @Autowired
    private RoleService roleService;

    @Override
    protected BaseRelationDao<UserRole, User, Role> getDao() {
        return dao;
    }

    /**
     * 获取可以分配的子实体清单
     *
     * @return 子实体清单
     */
    @Override
    protected List<Role> getCanAssignedChildren(String parentId) {
        return roleService.findAll();
    }

    /**
     * 创建分配关系
     *
     * @param parentId 父实体Id
     * @param childIds 子实体Id清单
     * @return 操作结果
     */
    @Override
    public OperateResult insertRelations(String parentId, List<String> childIds) {
        if (parentId.equals(ContextUtil.getUserId())) {
            //00031 = 不能为当前用户分配功能角色！
            return OperateResult.operationFailure("不能为当前用户分配功能角色！");
        }
        OperateResult result = super.insertRelations(parentId, childIds);
        // 清除用户权限缓存
//        AuthorityUtil.cleanUserAuthorizedCaches(parentId);
        return result;
    }

    /**
     * 移除分配关系
     *
     * @param parentId 父实体Id
     * @param childIds 子实体Id清单
     * @return 操作结果
     */
    @Override
    public OperateResult removeRelations(String parentId, List<String> childIds) {
        if (parentId.equals(ContextUtil.getUserId())) {
            //00032 = 不能移除当前用户的功能角色！
            return OperateResult.operationFailure("不能移除当前用户的功能角色！");
        }
        OperateResult result = super.removeRelations(parentId, childIds);
        // 清除用户权限缓存
//        AuthorityUtil.cleanUserAuthorizedCaches(parentId);
        return result;
    }

    /**
     * 通过父实体Id获取子实体清单
     *
     * @param parentId 父实体Id
     * @return 子实体清单
     */
    @Override
    public List<Role> getChildrenFromParentId(String parentId) {
        // 获取分配关系
        List<UserRole> userRoles = getRelationsByParentId(parentId);
        // 设置授权有效期
        List<Role> roleList = new LinkedList<>();
        userRoles.forEach(r -> {
            Role role = r.getChild();
            role.setRelationId(r.getId());
            roleList.add(role);
        });
        return roleList;
    }

    /**
     * 通过子实体Id获取父实体清单
     *
     * @param childId 子实体Id
     * @return 父实体清单
     */
    @Override
    public List<User> getParentsFromChildId(String childId) {
        List<User> users = super.getParentsFromChildId(childId);
        if (CollectionUtils.isEmpty(users)) {
            return new ArrayList<>();
        }
        return users;
    }
}
