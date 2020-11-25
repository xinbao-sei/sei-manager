package com.changhong.sei.deploy.service;

import com.changhong.sei.core.dao.BaseEntityDao;
import com.changhong.sei.core.service.BaseEntityService;
import com.changhong.sei.deploy.dao.NodeDao;
import com.changhong.sei.deploy.dao.RuntimeEnvDao;
import com.changhong.sei.deploy.entity.Node;
import com.changhong.sei.deploy.entity.RuntimeEnv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 业务逻辑实现类
 *
 * @author sei
 * @since 2020-11-23 08:34:09
 */
@Service("runtimeEnvService")
public class RuntimeEnvService extends BaseEntityService<RuntimeEnv> {
    @Autowired
    private RuntimeEnvDao dao;

    @Override
    protected BaseEntityDao<RuntimeEnv> getDao() {
        return dao;
    }

}