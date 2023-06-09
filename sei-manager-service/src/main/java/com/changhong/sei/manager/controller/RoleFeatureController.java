package com.changhong.sei.manager.controller;

import com.changhong.sei.core.controller.BaseRelationController;
import com.changhong.sei.core.dto.ResultData;
import com.changhong.sei.core.service.BaseRelationService;
import com.changhong.sei.manager.api.RoleFeatureApi;
import com.changhong.sei.manager.dto.FeatureDto;
import com.changhong.sei.manager.dto.FeatureNode;
import com.changhong.sei.manager.dto.RoleDto;
import com.changhong.sei.manager.dto.RoleFeatureDto;
import com.changhong.sei.manager.entity.Feature;
import com.changhong.sei.manager.entity.Role;
import com.changhong.sei.manager.entity.RoleFeature;
import com.changhong.sei.manager.service.RoleFeatureService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 实现功能: 功能角色分配的功能项API服务实现
 *
 * @author 王锦光 wangjg
 * @version 2020-01-28 10:41
 */
@RestController
@Api(value = "RoleFeatureApi", tags = "功能角色分配的功能项API服务实现")
@RequestMapping(path = "roleFeature", produces = MediaType.APPLICATION_JSON_VALUE)
public class RoleFeatureController extends BaseRelationController<RoleFeature, Role, Feature, RoleFeatureDto, RoleDto, FeatureDto>
        implements RoleFeatureApi {
    @Autowired
    private RoleFeatureService service;

    @Override
    public BaseRelationService<RoleFeature, Role, Feature> getService() {
        return service;
    }

    /**
     * 将子数据实体转换成DTO
     *
     * @param entity 业务实体
     * @return DTO
     */
    @Override
    public FeatureDto convertChildToDto(Feature entity) {
        return parentDtoModelMapper.map(entity, FeatureDto.class);
    }

    /**
     * 获取已分配的角色功能项树
     *
     * @param roleId 角色id
     * @return 功能项树清单
     */
    @Override
    public ResultData<List<FeatureNode>> getFeatureTree(String roleId) {
        return ResultData.success(service.getFeatureTree(roleId));
    }

    /**
     * 获取未分配的功能项树
     *
     * @param roleId 角色id
     * @return 功能项树清单
     */
    @Override
    public ResultData<List<FeatureNode>> getUnassignedFeatureTree(String roleId) {
        return ResultData.success(service.getUnassignedFeatureTree(roleId));
    }
}
