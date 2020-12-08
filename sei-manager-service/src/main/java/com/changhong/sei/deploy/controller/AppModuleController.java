package com.changhong.sei.deploy.controller;

import com.changhong.sei.core.controller.BaseEntityController;
import com.changhong.sei.core.dto.ResultData;
import com.changhong.sei.core.dto.serach.PageResult;
import com.changhong.sei.core.dto.serach.Search;
import com.changhong.sei.core.service.BaseEntityService;
import com.changhong.sei.deploy.api.AppModuleApi;
import com.changhong.sei.deploy.dto.AppModuleDto;
import com.changhong.sei.deploy.dto.AppModuleRequisitionDto;
import com.changhong.sei.deploy.dto.CreateTagRequest;
import com.changhong.sei.deploy.dto.GitlabTagDto;
import com.changhong.sei.deploy.entity.AppModule;
import com.changhong.sei.deploy.entity.AppModuleRequisition;
import com.changhong.sei.deploy.entity.Application;
import com.changhong.sei.deploy.service.AppModuleService;
import com.changhong.sei.deploy.service.ApplicationService;
import com.changhong.sei.integrated.service.GitlabService;
import io.swagger.annotations.Api;
import org.apache.commons.collections.CollectionUtils;
import org.gitlab4j.api.models.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 应用模块(AppModule)控制类
 *
 * @author sei
 * @since 2020-11-26 14:45:21
 */
@RestController
@Api(value = "AppModuleApi", tags = "应用模块服务")
@RequestMapping(path = "appModule", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class AppModuleController extends BaseEntityController<AppModule, AppModuleDto> implements AppModuleApi {
    private static final Logger LOG = LoggerFactory.getLogger(AppModuleController.class);
    /**
     * 应用模块服务对象
     */
    @Autowired
    private AppModuleService service;
    @Autowired
    private ApplicationService applicationService;
    @Autowired
    private GitlabService gitlabService;

    @Override
    public BaseEntityService<AppModule> getService() {
        return service;
    }

    /**
     * 分页查询业务实体
     *
     * @param search 查询参数
     * @return 分页查询结果
     */
    @Override
    public ResultData<PageResult<AppModuleDto>> findByPage(Search search) {
        return convertToDtoPageResult(service.findByPage(search));
//        PageResult<AppModule> pageResult = service.findByPage(search);
//        PageResult<AppModuleDto> result = new PageResult<>(pageResult);
//
//        List<AppModule> appModules = pageResult.getRows();
//        if (CollectionUtils.isNotEmpty(appModules)) {
//            Map<String, String> appMap = new HashMap<>();
//            List<Application> applications = applicationService.findAll();
//            if (CollectionUtils.isNotEmpty(applications)) {
//                appMap = applications.stream().collect(Collectors.toMap(Application::getId, Application::getName));
//            }
//
//            AppModuleDto moduleDto;
//            List<AppModuleDto> dtos = new ArrayList<>();
//            for (AppModule module : appModules) {
//                moduleDto = dtoModelMapper.map(module, AppModuleDto.class);
//                moduleDto.setAppName(appMap.get(module.getAppId()));
//
//                dtos.add(moduleDto);
//            }
//            result.setRows(dtos);
//        }
//
//        return ResultData.success(result);
    }

    /**
     * 通过应用Id获取模块清单
     *
     * @param appId 应用id
     * @return 模块清单
     */
    @Override
    public ResultData<List<AppModuleDto>> findAppId(String appId) {
        Application application = applicationService.findOne(appId);
        if (Objects.isNull(application)) {
            return ResultData.fail("[" + appId + "]应用不存在");
        }

        List<AppModuleDto> dtos = new ArrayList<>();
        List<AppModule> appModules = service.findListByProperty(AppModule.FIELD_APP_ID, appId);
        if (CollectionUtils.isNotEmpty(appModules)) {
//            String appName = application.getName();
            AppModuleDto moduleDto;
            for (AppModule module : appModules) {
                moduleDto = dtoModelMapper.map(module, AppModuleDto.class);
//                moduleDto.setAppName(appName);

                dtos.add(moduleDto);
            }
        }
        return ResultData.success(dtos);
    }

    /**
     * 分页查询应用模块申请单
     *
     * @param search 查询参数
     * @return 分页查询结果
     */
    @Override
    public ResultData<PageResult<AppModuleRequisitionDto>> findRequisitionByPage(Search search) {
        PageResult<AppModuleRequisition> pageResult = service.findRequisitionByPage(search);
        PageResult<AppModuleRequisitionDto> result = new PageResult<>(pageResult);
        List<AppModuleRequisition> requisitions = pageResult.getRows();
        if (CollectionUtils.isNotEmpty(requisitions)) {
            List<AppModuleRequisitionDto> dtos = requisitions.stream().map(e -> dtoModelMapper.map(e, AppModuleRequisitionDto.class)).collect(Collectors.toList());
            result.setRows(dtos);
        }
        return ResultData.success(result);
    }

    /**
     * 创建应用模块申请单
     *
     * @param dto 业务实体DTO
     * @return 操作结果
     */
    @Override
    public ResultData<AppModuleRequisitionDto> createRequisition(AppModuleDto dto) {
        return service.createRequisition(convertToEntity(dto));
    }

    /**
     * 修改编辑应用模块申请单
     *
     * @param dto 业务实体DTO
     * @return 操作结果
     */
    @Override
    public ResultData<AppModuleRequisitionDto> modifyRequisition(AppModuleDto dto) {
        return service.modifyRequisition(convertToEntity(dto));
    }

    /**
     * 删除应用模块申请单
     *
     * @param id@return 操作结果
     */
    @Override
    public ResultData<Void> deleteRequisition(String id) {
        return service.deleteRequisition(id);
    }

    /**
     * 获取项目标签
     *
     * @param gitId git项目id
     * @return 创建结果
     */
    @Override
    public ResultData<List<GitlabTagDto>> getTags(String gitId) {
        List<GitlabTagDto> tagList = new ArrayList<>(16);
        ResultData<List<Tag>> resultData = gitlabService.getProjectTags(gitId);
        if (resultData.successful()) {
            GitlabTagDto dto;
            List<Tag> tags = resultData.getData();
            for (Tag tag : tags) {
                dto = new GitlabTagDto();
                dto.setName(tag.getName());
                dto.setMessage(tag.getMessage());
                dto.setRelease(Objects.nonNull(tag.getRelease()));
                tagList.add(dto);
            }
        }
        return ResultData.success(tagList);
    }

    /**
     * 创建标签
     *
     * @param request 创建标签请求
     * @return 创建结果
     */
    @Override
    public ResultData<GitlabTagDto> createTag(CreateTagRequest request) {
        ResultData<Tag> resultData = gitlabService.createProjectTag(request.getGitId(), request.getTagName(), request.getBranch(), request.getMessage());
        if (resultData.successful()) {
            Tag tag = resultData.getData();
            GitlabTagDto dto = new GitlabTagDto();
            dto.setName(tag.getName());
            dto.setMessage(tag.getMessage());
            dto.setRelease(Objects.nonNull(tag.getRelease()));
            return ResultData.success(dto);
        } else {
            return ResultData.fail(resultData.getMessage());
        }
    }

    /**
     * 删除项目标签
     *
     * @param gitId   git项目id
     * @param tagName tag名
     * @return 创建结果
     */
    @Override
    public ResultData<Void> deleteRelease(String gitId, String tagName) {
        return gitlabService.deleteProjectTag(gitId, tagName);
    }
}