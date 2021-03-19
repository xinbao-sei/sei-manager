package com.changhong.sei.ge.service;

import com.changhong.sei.common.ObjectType;
import com.changhong.sei.core.dao.BaseEntityDao;
import com.changhong.sei.core.dto.ResultData;
import com.changhong.sei.core.dto.serach.PageResult;
import com.changhong.sei.core.dto.serach.Search;
import com.changhong.sei.core.dto.serach.SearchFilter;
import com.changhong.sei.core.service.BaseEntityService;
import com.changhong.sei.ge.dao.ProjectUserDao;
import com.changhong.sei.ge.dto.ProjectUserDto;
import com.changhong.sei.ge.entity.ProjectUser;
import com.changhong.sei.manager.entity.User;
import com.changhong.sei.manager.service.UserService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 项目用户(ProjectUser)业务逻辑实现类
 *
 * @author sei
 * @since 2020-11-23 08:34:09
 */
@Service
public class ProjectUserService extends BaseEntityService<ProjectUser> {
    @Autowired
    private ProjectUserDao dao;
    @Autowired
    private UserService userService;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    protected BaseEntityDao<ProjectUser> getDao() {
        return dao;
    }

    /**
     * 分配应用管理员
     *
     * @return 返回分配结果
     */
    @Transactional(rollbackFor = Exception.class)
    public ResultData<Void> assign(String account, String userName, String objectId, String objectName, ObjectType type) {
        if (StringUtils.isBlank(account)) {
            return ResultData.fail("分配的账号不能为空");
        }
        if (StringUtils.isBlank(objectId)) {
            return ResultData.fail("对象id不能为空.");
        }
        if (Objects.isNull(type)) {
            return ResultData.fail("对象类型不能为空.");
        }
        Search search = Search.createSearch();
        search.addFilter(new SearchFilter(ProjectUser.FIELD_OBJECT_ID, objectId));
        search.addFilter(new SearchFilter(ProjectUser.FIELD_ACCOUNT, account));
        search.addFilter(new SearchFilter(ProjectUser.FIELD_TYPE, type));
        ProjectUser user = dao.findOneByFilters(search);
        if (Objects.isNull(user)) {
            user = new ProjectUser();
            user.setType(type);
            user.setObjectId(objectId);
            user.setObjectName(objectName);
            user.setAccount(account);
            user.setUserName(userName);
            this.save(user);
        }
        return ResultData.success();

    }

    /**
     * 批量分配应用模块用户
     *
     * @param users 用户
     * @return 返回分配结果
     */
    @Transactional(rollbackFor = Exception.class)
    public ResultData<Void> assign(List<ProjectUser> users) {
        if (CollectionUtils.isNotEmpty(users)) {
            Set<String> objIds = users.stream().map(ProjectUser::getObjectId).collect(Collectors.toSet());
            Search search = Search.createSearch();
            search.addFilter(new SearchFilter(ProjectUser.FIELD_OBJECT_ID, objIds, SearchFilter.Operator.IN));
            List<ProjectUser> userList = dao.findByFilters(search);
            Set<String> accounts = userList.stream().map(u -> u.getAccount() + "|" + u.getType().name()).collect(Collectors.toSet());
            for (ProjectUser user : users) {
                ObjectType type = user.getType();
                if (StringUtils.isNotBlank(user.getAccount()) && Objects.nonNull(type)
                        && !accounts.contains(user.getAccount() + "|" + type.name())) {
                    this.save(user);
                }
            }
            return ResultData.success();
        } else {
            return ResultData.fail("分配的用户不能为空.");
        }
    }

    /**
     * 获取未分配的用户
     *
     * @param objectId 对象id
     * @return 返回已分配的用户清单
     */
    public PageResult<ProjectUserDto> getUnassignedUser(String objectId, Search searchUser) {
        Search search = Search.createSearch();
        search.addFilter(new SearchFilter(ProjectUser.FIELD_OBJECT_ID, objectId));
        List<ProjectUser> projectUsers = dao.findByFilters(search);
        Set<String> accounts = projectUsers.stream().map(ProjectUser::getAccount).collect(Collectors.toSet());

        searchUser.addFilter(new SearchFilter(User.FIELD_ACCOUNT, accounts, SearchFilter.Operator.NOTIN));
        PageResult<User> pageResult = userService.findByPage(search);
        PageResult<ProjectUserDto> userPageResult = new PageResult<>(pageResult);
        if (pageResult.getTotal() > 0) {
            List<ProjectUserDto> list = pageResult.getRows().stream().map(o -> {
                ProjectUserDto user = new ProjectUserDto();
                user.setAccount(o.getAccount());
                user.setUserName(o.getNickname());
                return user;
            }).collect(Collectors.toList());
            userPageResult.setRows(list);
        }
        return userPageResult;
    }

    /**
     * 获取已分配的用户
     *
     * @param objectId 对象id
     * @return 返回已分配的用户清单
     */
    public List<ProjectUser> getAssignedUser(String objectId) {
        Search search = Search.createSearch();
        search.addFilter(new SearchFilter(ProjectUser.FIELD_OBJECT_ID, objectId));
        return dao.findByFilters(search);
    }

    /**
     * 获取已分配的对象
     * 数据权限检查用
     *
     * @param account 账号
     * @return 返回已分配的对象清单
     */
    public Set<String> getAssignedObjects(String account) {
        List<ProjectUser> projectUsers = dao.findListByProperty(ProjectUser.FIELD_ACCOUNT, account);
        if (CollectionUtils.isNotEmpty(projectUsers)) {
            return projectUsers.stream().map(ProjectUser::getObjectId).collect(Collectors.toSet());
        } else {
            return new HashSet<>();
        }
    }
}