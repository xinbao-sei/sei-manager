package com.changhong.sei.deploy.dao;

import com.changhong.sei.core.dao.BaseEntityDao;
import com.changhong.sei.deploy.entity.FlowTaskInstance;
import org.springframework.stereotype.Repository;

/**
 * 流程任务实例(FlowTaskInstance)数据库访问类
 *
 * @author sei
 * @since 2020-11-26 14:45:21
 */
@Repository
public interface FlowTaskInstanceDao extends BaseEntityDao<FlowTaskInstance> {

}