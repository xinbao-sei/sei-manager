package com.changhong.sei.deploy.service;

import com.changhong.sei.core.dao.BaseEntityDao;
import com.changhong.sei.core.dto.ResultData;
import com.changhong.sei.core.service.BaseEntityService;
import com.changhong.sei.core.service.bo.OperateResultWithData;
import com.changhong.sei.deploy.dao.FlowTaskHistoryDao;
import com.changhong.sei.deploy.entity.FlowTaskHistory;
import com.changhong.sei.deploy.entity.FlowTaskInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 流程任务历史记录(FlowTaskHistory)业务逻辑实现类
 *
 * @author sei
 * @since 2020-11-26 14:45:21
 */
@Service("flowTaskHistoryService")
public class FlowTaskHistoryService extends BaseEntityService<FlowTaskHistory> {
    @Autowired
    private FlowTaskHistoryDao dao;

    @Override
    protected BaseEntityDao<FlowTaskHistory> getDao() {
        return dao;
    }

    /**
     * 记录任务执行历史
     *
     * @param taskInstance    任务实例
     * @param executeAccount  任务执行人
     * @param executeUserName 任务执行人名称
     * @param message         任务执行消息
     * @return 历史记录结果
     */
    public ResultData<Void> record(FlowTaskInstance taskInstance, String executeAccount, String executeUserName, String message) {
        if (Objects.isNull(taskInstance)) {
            return ResultData.fail("流程任务实例不能为空!");
        }

        FlowTaskHistory history = new FlowTaskHistory(taskInstance);
        // 执行人
        history.setExecuteAccount(executeAccount);
        history.setExecuteUserName(executeUserName);
        // 执行时间
        history.setExecuteTime(LocalDateTime.now());
        // 执行结果
        history.setResult(message);
        // 持久化
        OperateResultWithData<FlowTaskHistory> result = this.save(history);
        if (result.successful()) {
            return ResultData.success();
        } else {
            return ResultData.fail(result.getMessage());
        }
    }

}