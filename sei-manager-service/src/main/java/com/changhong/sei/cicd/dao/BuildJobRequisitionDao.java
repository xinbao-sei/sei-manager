package com.changhong.sei.cicd.dao;

import com.changhong.sei.core.dao.BaseEntityDao;
import com.changhong.sei.cicd.entity.BuildJobRequisition;
import org.springframework.stereotype.Repository;

/**
 * 发布记录(ReleaseRecord)数据库访问类
 *
 * @author sei
 * @since 2020-10-30 15:20:23
 */
@Repository
public interface BuildJobRequisitionDao extends BaseEntityDao<BuildJobRequisition> {

}