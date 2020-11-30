package com.changhong.sei.monitor.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 实现功能：
 *
 * @author 马超(Vision.Mac)
 * @version 1.0.00  2020-11-26 23:23
 */
@ApiModel(description = "应用")
public class ServiceInstanceDto implements Serializable {
    private static final long serialVersionUID = 6201562737225188340L;

    @ApiModelProperty(notes = "应用代码")
    private String code;
    @ApiModelProperty(notes = "应用名称")
    private String name;
    @ApiModelProperty(notes = "其他属性")
    private Map<String, String> metadata;

    public ServiceInstanceDto() {

    }

    public ServiceInstanceDto(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    public void putMetadata(String key, String value) {
        if (Objects.isNull(metadata)) {
            metadata = new HashMap<>();
        }
        metadata.put(key, value);
    }
}