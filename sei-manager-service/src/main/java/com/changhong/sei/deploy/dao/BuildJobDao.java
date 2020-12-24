package com.changhong.sei.deploy.dao;

import com.changhong.sei.core.dao.BaseEntityDao;
import com.changhong.sei.deploy.entity.BuildJob;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * 构建任务(BuildJob)数据库访问类
 *
 * @author sei
 * @since 2020-10-30 15:20:23
 */
@Repository
public interface BuildJobDao extends BaseEntityDao<BuildJob> {

    /**
     * 更新版本号
     *
     * @return 返回更新的记录数
     */
    @Modifying
    @Query("update BuildJob t set t.allowBuild = :allowBuild where t.jobName = :jobName ")
    int updateAllowBuildStatus(@Param("jobName") String jobName, @Param("allowBuild") Boolean allowBuild);
}