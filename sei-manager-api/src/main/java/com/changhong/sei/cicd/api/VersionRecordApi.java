package com.changhong.sei.cicd.api;

import com.changhong.sei.core.api.BaseEntityApi;
import com.changhong.sei.core.api.FindByPageApi;
import com.changhong.sei.core.dto.ResultData;
import com.changhong.sei.core.dto.serach.PageResult;
import com.changhong.sei.core.dto.serach.Search;
import com.changhong.sei.cicd.dto.VersionRecordDto;
import com.changhong.sei.cicd.dto.VersionRecordRequisitionDto;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * 版本发布记录(VersionRecord)API
 *
 * @author sei
 * @since 2020-11-23 08:34:10
 */
@Valid
@FeignClient(name = "sei-manager", path = "releaseVersion")
public interface VersionRecordApi extends BaseEntityApi<VersionRecordDto>, FindByPageApi<VersionRecordDto> {

    /**
     * 分页查询应用版本申请单
     *
     * @param search 查询参数
     * @return 分页查询结果
     */
    @PostMapping(path = "findRequisitionByPage", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "分页查询应用版本申请单", notes = "分页查询应用版本申请单")
    ResultData<PageResult<VersionRecordRequisitionDto>> findRequisitionByPage(Search search);

    /**
     * 创建应用版本申请单
     *
     * @param dto 业务实体DTO
     * @return 操作结果
     */
    @PostMapping(path = "createRequisition", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "创建应用版本申请单", notes = "创建应用版本申请单")
    ResultData<VersionRecordRequisitionDto> createRequisition(@RequestBody @Valid VersionRecordDto dto);

    /**
     * 修改编辑应用版本申请单
     *
     * @param dto 业务实体DTO
     * @return 操作结果
     */
    @PostMapping(path = "modifyRequisition", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "修改编辑应用版本申请单", notes = "修改编辑应用版本申请单")
    ResultData<VersionRecordRequisitionDto> modifyRequisition(@RequestBody @Valid VersionRecordDto dto);

    /**
     * 删除应用版本申请单
     *
     * @param id 申请单id
     * @return 操作结果
     */
    @DeleteMapping(path = "deleteRequisition/{id}")
    @ApiOperation(value = "删除应用版本申请单", notes = "删除应用版本申请单")
    ResultData<Void> deleteRequisition(@PathVariable("id") String id);
}