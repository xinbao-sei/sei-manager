package com.changhong.sei.deploy.controller;

import com.changhong.sei.core.controller.BaseEntityController;
import com.changhong.sei.core.dto.ResultData;
import com.changhong.sei.core.dto.serach.PageResult;
import com.changhong.sei.core.dto.serach.Search;
import com.changhong.sei.core.service.BaseEntityService;
import com.changhong.sei.deploy.api.BuildJobApi;
import com.changhong.sei.deploy.dto.GitlabPushHookRequest;
import com.changhong.sei.deploy.dto.BuildDetailDto;
import com.changhong.sei.deploy.dto.BuildJobDto;
import com.changhong.sei.deploy.dto.BuildJobRequisitionDto;
import com.changhong.sei.deploy.entity.BuildJob;
import com.changhong.sei.deploy.entity.BuildJobRequisition;
import com.changhong.sei.deploy.service.BuildJobService;
import io.swagger.annotations.Api;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 构建任务(BuildJob)控制类
 *
 * @author sei
 * @since 2020-11-23 08:34:09
 */
@RestController
@Api(value = "BuildJobApi", tags = "构建任务服务")
@RequestMapping(path = "releaseRecord", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class BuildJobController extends BaseEntityController<BuildJob, BuildJobDto> implements BuildJobApi {
    /**
     * 发布记录服务对象
     */
    @Autowired
    private BuildJobService service;

    @Override
    public BaseEntityService<BuildJob> getService() {
        return service;
    }

    /**
     * 分页发布业务实体
     *
     * @param search 查询参数
     * @return 分页查询结果
     */
    @Override
    public ResultData<PageResult<BuildJobDto>> findByPage(Search search) {
        return convertToDtoPageResult(service.findByPage(search));
    }

    /**
     * 分页查询发布申请单
     *
     * @param search 查询参数
     * @return 分页查询结果
     */
    @Override
    public ResultData<PageResult<BuildJobRequisitionDto>> findRequisitionByPage(Search search) {
        PageResult<BuildJobRequisition> pageResult = service.findRequisitionByPage(search);
        PageResult<BuildJobRequisitionDto> result = new PageResult<>(pageResult);
        List<BuildJobRequisition> requisitions = pageResult.getRows();
        if (CollectionUtils.isNotEmpty(requisitions)) {
            List<BuildJobRequisitionDto> dtos = requisitions.stream().map(e -> dtoModelMapper.map(e, BuildJobRequisitionDto.class)).collect(Collectors.toList());
            result.setRows(dtos);
        }
        return ResultData.success(result);
    }

    /**
     * 创建发布申请单
     *
     * @param dto 业务实体DTO
     * @return 操作结果
     */
    @Override
    public ResultData<BuildJobRequisitionDto> createRequisition(BuildJobDto dto) {
        return service.createRequisition(convertToEntity(dto));
    }

    /**
     * 修改发布申请单
     *
     * @param dto 业务实体DTO
     * @return 操作结果
     */
    @Override
    public ResultData<BuildJobRequisitionDto> modifyRequisition(BuildJobDto dto) {
        return service.modifyRequisition(convertToEntity(dto));
    }

    /**
     * 删除发布申请单
     *
     * @param id@return 操作结果
     */
    @Override
    public ResultData<Void> deleteRequisition(String id) {
        return service.deleteRequisition(id);
    }

    /**
     * 构建Jenkins任务
     *
     * @param id 发布记录id
     * @return 返回构建操作
     */
    @Override
    public ResultData<Void> buildJob(String id) {
        return service.buildJob(id);
    }

    /**
     * 获取构建明细
     *
     * @param id 发布记录id
     * @return 返回构建明细
     */
    @Override
    public ResultData<BuildDetailDto> getBuildDetail(String id) {
        return service.getBuildDetail(id);
    }

    /**
     * Gitlab Push Hook
     *
     * @param request gitlab push hook
     * @return 返回结果
     */
    @Override
    public ResultData<Void> webhook(GitlabPushHookRequest request) {
        return service.webhook(request);
    }
}