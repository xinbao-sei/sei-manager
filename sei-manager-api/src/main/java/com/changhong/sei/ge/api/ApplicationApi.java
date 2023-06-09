package com.changhong.sei.ge.api;

import com.changhong.sei.core.api.BaseEntityApi;
import com.changhong.sei.core.api.FindByPageApi;
import com.changhong.sei.core.dto.ResultData;
import com.changhong.sei.core.dto.serach.PageResult;
import com.changhong.sei.core.dto.serach.Search;
import com.changhong.sei.ge.dto.ApplicationDto;
import com.changhong.sei.cicd.dto.ApplicationRequisitionDto;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 实现功能: 应用API
 */
@FeignClient(name = "sei-manager", path = "application")
public interface ApplicationApi extends BaseEntityApi<ApplicationDto>, FindByPageApi<ApplicationDto> {

    /**
     * 分页查询业务实体
     *
     * @param search 查询参数
     * @return 分页查询结果
     */
    @PostMapping(path = "findByPageNoAuth", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "分页查询业务实体", notes = "分页查询业务实体")
    ResultData<PageResult<ApplicationDto>> findByPageNoAuth(@RequestBody Search search);

    /**
     * 分页查询应用申请单
     *
     * @param search 查询参数
     * @return 分页查询结果
     */
    @PostMapping(path = "findRequisitionByPage", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "分页查询应用申请单", notes = "分页查询应用申请单")
    ResultData<PageResult<ApplicationRequisitionDto>> findRequisitionByPage(@RequestBody Search search);

    /**
     * 创建应用申请单
     *
     * @param dto 业务实体DTO
     * @return 操作结果
     */
    @PostMapping(path = "createRequisition", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "创建应用申请单", notes = "创建应用申请单")
    ResultData<ApplicationRequisitionDto> createRequisition(@RequestBody @Valid ApplicationDto dto);

    /**
     * 修改编辑应用申请单
     *
     * @param dto 业务实体DTO
     * @return 操作结果
     */
    @PostMapping(path = "modifyRequisition", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "修改编辑应用申请单", notes = "修改编辑应用申请单")
    ResultData<ApplicationRequisitionDto> modifyRequisition(@RequestBody @Valid ApplicationDto dto);

    /**
     * 删除应用申请单
     *
     * @param id 申请单id
     * @return 操作结果
     */
    @DeleteMapping(path = "deleteRequisition/{id}")
    @ApiOperation(value = "删除应用申请单", notes = "删除应用申请单")
    ResultData<Void> deleteRequisition(@PathVariable("id") String id);

    /**
     * 分配应用管理员
     *
     * @return 返回分配结果
     */
    @PostMapping(path = "assignManager", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "分配应用管理员", notes = "分配应用管理员")
    ResultData<Void> assignManager(@RequestParam("account") String account, @RequestParam("appId") String appId);

}
