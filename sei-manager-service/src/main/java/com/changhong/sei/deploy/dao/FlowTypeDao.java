package com.changhong.sei.deploy.dao;

import com.changhong.sei.core.dao.BaseEntityDao;
import com.changhong.sei.deploy.entity.AppModule;
import com.changhong.sei.deploy.entity.FlowType;
import org.springframework.stereotype.Repository;

/**
 * 流程类型(FlowType)数据库访问类
 *
 * @author sei
 * @since 2020-11-26 14:45:21
 */
@Repository
public interface FlowTypeDao extends BaseEntityDao<FlowType> {

}