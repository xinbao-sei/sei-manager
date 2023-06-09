package com.changhong.sei.cicd.controller;

import com.changhong.sei.cicd.api.BuildJobApi;
import com.changhong.sei.cicd.dto.*;
import com.changhong.sei.cicd.entity.BuildJob;
import com.changhong.sei.cicd.entity.BuildJobRequisition;
import com.changhong.sei.cicd.entity.Tag;
import com.changhong.sei.cicd.service.BuildJobService;
import com.changhong.sei.common.AuthorityUtil;
import com.changhong.sei.core.controller.BaseEntityController;
import com.changhong.sei.core.dto.ResultData;
import com.changhong.sei.core.dto.serach.PageResult;
import com.changhong.sei.core.dto.serach.Search;
import com.changhong.sei.core.dto.serach.SearchFilter;
import com.changhong.sei.core.service.BaseEntityService;
import io.swagger.annotations.Api;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 构建任务(BuildJob)控制类
 *
 * @author sei
 * @since 2020-11-23 08:34:09
 */
@RestController
@Api(value = "BuildJobApi", tags = "构建任务服务")
@RequestMapping(path = {"releaseRecord", BuildJobApi.PATH}, produces = MediaType.APPLICATION_JSON_VALUE)
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
        if (Objects.isNull(search)) {
            search = Search.createSearch();
        }
        // 添加数据权限过滤
        Set<String> ids = AuthorityUtil.getAuthorizedModuleIds();
        if (Objects.nonNull(ids)) {
            search.addFilter(new SearchFilter(BuildJob.FIELD_MODULE_ID, ids, SearchFilter.Operator.IN));
        }
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

    /**
     * 根据环境代码和应用模块id获取部署的tag与指定tag的变化记录
     *
     * @param envCode  环境代码
     * @param moduleId 应用模块id
     * @param tag      指定tag
     * @return 发挥tagName
     */
    @Override
    public ResultData<List<TagDto>> getTags(String envCode, String moduleId, String tag) {
        List<TagDto> list;
        List<Tag> tagList = service.getTags(envCode, moduleId, tag);
        if (CollectionUtils.isNotEmpty(tagList)) {
            list = tagList.stream().map(t -> {
                TagDto dto = new TagDto();
                dto.setId(t.getId());
                dto.setModuleId(t.getModuleId());
                dto.setModuleCode(t.getModuleCode());
                dto.setTagName(t.getTagName());
                dto.setMajor(t.getMajor());
                dto.setMinor(t.getMinor());
                dto.setRevised(t.getRevised());
                dto.setRelease(t.getRelease());
                dto.setCommitId(t.getCommitId());
                dto.setBranch(t.getBranch());
                dto.setCreateTime(t.getCreateTime());
                dto.setCreateAccount(t.getCreateAccount());
                return dto;
            }).collect(Collectors.toList());
        } else {
            list = new ArrayList<>();
        }
        return ResultData.success(list);
    }
}