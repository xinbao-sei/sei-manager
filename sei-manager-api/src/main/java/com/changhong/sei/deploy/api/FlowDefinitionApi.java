package com.changhong.sei.deploy.api;

import com.changhong.sei.core.dto.ResultData;
import com.changhong.sei.core.dto.serach.PageResult;
import com.changhong.sei.core.dto.serach.Search;
import com.changhong.sei.deploy.dto.FlowTypeDto;
import com.changhong.sei.deploy.dto.FlowTypeNodeDto;
import com.changhong.sei.deploy.dto.FlowTypeNodeRecordDto;
import com.changhong.sei.deploy.dto.FlowTypeVersionDto;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

/**
 * 流程定义(FlowDefinition)API
 *
 * @author sei
 * @since 2020-12-15 18:09:47
 */
@Valid
@FeignClient(name = "sei-manager", path = "flow/definition")
public interface FlowDefinitionApi {
    /**
     * 保存流程类型
     *
     * @param dto dto
     * @return 返回结果
     */
    @PostMapping(path = "saveType", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "保存流程类型", notes = "保存一个流程类型")
    ResultData<FlowTypeDto> saveType(@RequestBody @Valid FlowTypeDto dto);

    /**
     * 分页查询流程类型
     *
     * @param search search
     * @return 分页数据结果
     */
    @PostMapping(path = "findTypeByPage", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "分页查询流程类型", notes = "分页查询流程类型")
    ResultData<PageResult<FlowTypeDto>> findTypeByPage(@RequestBody Search search);

    /**
     * 保存流程类型节点
     *
     * @param dto dto
     * @return 返回结果
     */
    @PostMapping(path = "saveTypeNode", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "保存流程类型节点", notes = "保存一个流程类型节点")
    ResultData<FlowTypeNodeDto> saveTypeNode(@RequestBody @Valid FlowTypeNodeDto dto);

    /**
     * 删除流程类型节点
     *
     * @param ids 流程类型节点Id集合
     * @return 操作结果
     */
    @DeleteMapping(path = "deleteTypeNode", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "删除流程类型节点", notes = "删除流程类型节点")
    ResultData<Void> deleteTypeNode(@RequestBody Set<String> ids);

    /**
     * 通过流程类型获取节点清单
     *
     * @param typeId 流程类型id
     * @return 返回结果
     */
    @GetMapping(path = "getTypeNode/{typeId}")
    @ApiOperation(value = "通过流程类型获取节点清单", notes = "通过流程类型获取节点清单")
    ResultData<List<FlowTypeNodeDto>> getTypeNodeByTypeId(@PathVariable("typeId") String typeId);

    /**
     * 通过流程类型获取版本清单
     *
     * @param typeId 流程类型id
     * @return 返回结果
     */
    @GetMapping(path = "getTypeVersion/{typeId}")
    @ApiOperation(value = "通过流程类型获取版本清单", notes = "通过流程类型获取版本清单")
    ResultData<List<FlowTypeVersionDto>> getTypeVersionByTypeId(@PathVariable("typeId") String typeId);

    /**
     * 通过流程类型获取节点清单
     *
     * @param typeId  流程类型id
     * @param version 流程类型版本
     * @return 返回结果
     */
    @GetMapping(path = "getTypeNode/{typeId}/{version}")
    @ApiOperation(value = "通过流程类型和版本获取节点清单", notes = "通过流程类型和版本获取节点清单")
    ResultData<List<FlowTypeNodeRecordDto>> getTypeNodeRecord(@PathVariable("typeId") String typeId,
                                                              @PathVariable("version") Integer version);

    /**
     * 发布流程类型
     *
     * @param typeId 流程类型id
     * @return 发布结果
     */
    @PostMapping(path = "publish/{typeId}")
    @ApiOperation(value = "发布流程类型", notes = "发布流程类型")
    ResultData<Void> publish(@PathVariable("typeId") String typeId);
}