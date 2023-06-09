package com.changhong.sei.ge.api;

import com.changhong.sei.core.api.BaseEntityApi;
import com.changhong.sei.core.dto.ResultData;
import com.changhong.sei.core.dto.serach.PageResult;
import com.changhong.sei.core.dto.serach.Search;
import com.changhong.sei.ge.dto.ProjectUserDto;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

/**
 * 项目用户(ProjectUser)API
 *
 * @author sei
 * @since 2020-11-23 08:34:10
 */
@Valid
@FeignClient(name = "sei-manager", path = "projectUser")
public interface ProjectUserApi {

    /**
     * 批量分配应用模块用户
     *
     * @param users 用户
     * @return 返回分配结果
     */
    @PostMapping(path = "assign", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "批量分配应用模块用户", notes = "批量分配应用模块用户")
    ResultData<Void> assign(@RequestBody List<ProjectUserDto> users);

    /**
     * 按用户账号清单移除应用模块用户
     *
     * @param objectId 应用模块id
     * @param accounts 用户账号
     * @return 操作结果
     */
    @DeleteMapping(path = "cancelAssign/{objectId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "按用户账号清单移除应用模块用户", notes = "按用户账号清单移除应用模块用户")
    ResultData<Void> cancelAssign(@PathVariable("objectId") String objectId, @RequestBody Set<String> accounts);

    /**
     * 获取未分配的用户
     *
     * @param objectId 对象id
     * @return 返回已分配的用户清单
     */
    @PostMapping(path = "getUnassignedUser/{objectId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取未分配的用户", notes = "获取未分配的用户")
    PageResult<ProjectUserDto> getUnassignedUser(@PathVariable("objectId") String objectId, @RequestBody Search searchUser);

    /**
     * 获取已分配的用户
     *
     * @param objectId 对象id
     * @return 返回已分配的用户清单
     */
    @GetMapping(path = "getAssignedUser")
    @ApiOperation(value = "获取已分配的用户", notes = "获取已分配的用户")
    ResultData<List<ProjectUserDto>> getAssignedUser(@RequestParam("objectId") String objectId);

    /**
     * 获取有权限的项目组id
     * 数据权限检查用
     *
     * @param account 账号
     * @return 返回有权限的项目组id
     */
    @GetMapping(path = "getAssignedGroupIds")
    @ApiOperation(value = "获取有权限的项目组id", notes = "获取有权限的项目组id")
    ResultData<Set<String>> getAssignedGroupIds(@RequestParam("account") String account);

    /**
     * 获取有权限的应用id
     * 数据权限检查用
     *
     * @param account 账号
     * @return 返回已分配的对象清单
     */
    @GetMapping(path = "getAssignedAppIds")
    @ApiOperation(value = "获取有权限的应用id", notes = "获取有权限的应用")
    ResultData<Set<String>> getAssignedAppIds(@RequestParam("account") String account);
}