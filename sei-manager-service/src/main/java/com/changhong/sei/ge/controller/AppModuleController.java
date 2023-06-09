package com.changhong.sei.ge.controller;

import com.changhong.sei.cicd.dto.AppModuleRequisitionDto;
import com.changhong.sei.cicd.entity.AppModuleRequisition;
import com.changhong.sei.common.AuthorityUtil;
import com.changhong.sei.core.controller.BaseEntityController;
import com.changhong.sei.core.dto.ResultData;
import com.changhong.sei.core.dto.serach.PageResult;
import com.changhong.sei.core.dto.serach.Search;
import com.changhong.sei.core.dto.serach.SearchFilter;
import com.changhong.sei.core.entity.BaseEntity;
import com.changhong.sei.core.service.BaseEntityService;
import com.changhong.sei.ge.api.AppModuleApi;
import com.changhong.sei.ge.dto.AppModuleDto;
import com.changhong.sei.ge.dto.ForkProjectRequest;
import com.changhong.sei.ge.entity.AppModule;
import com.changhong.sei.ge.entity.Application;
import com.changhong.sei.ge.service.AppModuleService;
import com.changhong.sei.ge.service.ApplicationService;
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
 * 应用模块(AppModule)控制类
 *
 * @author sei
 * @since 2020-11-26 14:45:21
 */
@RestController
@Api(value = "AppModuleApi", tags = "应用模块服务")
@RequestMapping(path = "appModule", produces = MediaType.APPLICATION_JSON_VALUE)
public class AppModuleController extends BaseEntityController<AppModule, AppModuleDto> implements AppModuleApi {
    /**
     * 应用模块服务对象
     */
    @Autowired
    private AppModuleService service;
    @Autowired
    private ApplicationService applicationService;

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
        if (Objects.isNull(search)) {
            search = Search.createSearch();
        }
        // 添加数据权限过滤
        Set<String> ids = AuthorityUtil.getAuthorizedModuleIds();
        if (Objects.nonNull(ids)) {
            search.addFilter(new SearchFilter(BaseEntity.ID, ids, SearchFilter.Operator.IN));
        }
        return convertToDtoPageResult(service.findByPage(search));
    }

    /**
     * 分页查询业务实体
     *
     * @param search 查询参数
     * @return 分页查询结果
     */
    @Override
    public ResultData<PageResult<AppModuleDto>> findByPageNoAuth(Search search) {
        if (Objects.isNull(search)) {
            search = Search.createSearch();
        }
        return convertToDtoPageResult(service.findByPage(search));
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
     * 派生二开项目
     *
     * @param request 派生参数
     * @return 操作结果
     */
    @Override
    public ResultData<Void> forkProject(ForkProjectRequest request) {
        return service.forkProject(request.getAppId(), request.getModuleId(), request.getNamespace());
    }
}