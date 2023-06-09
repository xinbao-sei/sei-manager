package com.changhong.sei.cicd.service;

import com.changhong.sei.core.context.ContextUtil;
import com.changhong.sei.core.context.SessionUser;
import com.changhong.sei.core.dao.BaseEntityDao;
import com.changhong.sei.core.dto.ResultData;
import com.changhong.sei.core.log.LogUtil;
import com.changhong.sei.core.service.BaseEntityService;
import com.changhong.sei.core.service.bo.OperateResult;
import com.changhong.sei.core.service.bo.OperateResultWithData;
import com.changhong.sei.cicd.dao.RequisitionOrderDao;
import com.changhong.sei.cicd.dto.ApprovalStatus;
import com.changhong.sei.cicd.dto.TaskHandleRequest;
import com.changhong.sei.cicd.dto.TaskSubmitRequest;
import com.changhong.sei.cicd.entity.RequisitionOrder;
import com.changhong.sei.ge.service.AppModuleService;
import com.changhong.sei.ge.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 申请单(RequisitionOrder)业务逻辑实现类
 *
 * @author sei
 * @since 2020-11-26 14:45:21
 */
@Service
public class RequisitionOrderService extends BaseEntityService<RequisitionOrder> {
    @Autowired
    private RequisitionOrderDao dao;
    @Autowired
    private FlowRuntimeService flowRuntimeService;

    @Autowired
    private ApplicationService applicationService;
    @Autowired
    private AppModuleService appModuleService;
    @Autowired
    private BuildJobService releaseRecordService;
    @Autowired
    private VersionRecordService versionService;

    @Override
    protected BaseEntityDao<RequisitionOrder> getDao() {
        return dao;
    }

    /**
     * 根据关联id获取申请单
     *
     * @param relationId 关联id
     * @return 申请单
     */
    public RequisitionOrder getByRelationId(String relationId) {
        return this.findFirstByProperty(RequisitionOrder.FIELD_RELATION_ID, relationId);
    }

    /**
     * 创建申请单
     *
     * @param requisitionRecord 申请单
     * @return 操作结果
     */
    @Transactional(rollbackFor = Exception.class)
    public ResultData<RequisitionOrder> createRequisition(RequisitionOrder requisitionRecord) {
        // 审核状态:初始
        requisitionRecord.setApprovalStatus(ApprovalStatus.INITIAL);
        SessionUser sessionUser = ContextUtil.getSessionUser();
        // 申请人账号
        requisitionRecord.setApplicantAccount(sessionUser.getAccount());
        requisitionRecord.setApplicantUserName(sessionUser.getUserName());
        requisitionRecord.setApplicationTime(LocalDateTime.now());

        OperateResultWithData<RequisitionOrder> result = this.save(requisitionRecord);
        if (result.successful()) {
            return ResultData.success(result.getData());
        } else {
            return ResultData.fail(result.getMessage());
        }
    }

    /**
     * 修改申请单
     *
     * @param requisitionRecord 申请单
     * @return 操作结果
     */
    @Transactional(rollbackFor = Exception.class)
    public ResultData<RequisitionOrder> modifyRequisition(RequisitionOrder requisitionRecord) {
        OperateResultWithData<RequisitionOrder> result = this.save(requisitionRecord);
        if (result.successful()) {
            return ResultData.success(result.getData());
        } else {
            return ResultData.fail(result.getMessage());
        }
    }

    /**
     * 删除申请单
     *
     * @param relationId 关联id
     * @return 操作结果
     */
    @Transactional(rollbackFor = Exception.class)
    public ResultData<Void> deleteRequisition(String relationId) {
        RequisitionOrder requisition = this.findFirstByProperty(RequisitionOrder.FIELD_RELATION_ID, relationId);
        if (Objects.nonNull(requisition)) {
            if (ApprovalStatus.INITIAL == requisition.getApprovalStatus()) {
                OperateResult result = this.delete(requisition.getId());
                if (result.successful()) {
                    // 删除待办任务
                    flowRuntimeService.deleteTaskByOrderId(requisition.getId());

                    return ResultData.success();
                } else {
                    return ResultData.fail(result.getMessage());
                }
            } else {
                return ResultData.fail("申请单审核状态非初始状态,禁止删除!");
            }
        }
        return ResultData.fail("申请单不存在!");
    }

    /**
     * 提交申请单
     *
     * @param submitRequest 业务实体DTO
     * @return 操作结果
     */
    @Transactional(rollbackFor = Exception.class)
    public ResultData<Void> submit(TaskSubmitRequest submitRequest) {
        // 获取申请单
        RequisitionOrder requisition = this.findOne(submitRequest.getRequisitionId());
        if (Objects.isNull(requisition)) {
            return ResultData.fail("申请单不存在!");
        }

        // 设置业务key
        requisition.setBizKey(submitRequest.getBizKey());
        ResultData<RequisitionOrder> result = flowRuntimeService.submit(requisition);
        if (result.successful()) {
            OperateResultWithData<RequisitionOrder> resultWithData = this.save(requisition);
            if (resultWithData.successful()) {
                return ResultData.success();
            } else {
                // 事务回滚
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return ResultData.fail(resultWithData.getMessage());
            }
        } else {
            return ResultData.fail(result.getMessage());
        }
    }

    /**
     * 申请单待办任务处理
     *
     * @param handleRequest 任务处理请求
     * @return 操作结果
     */
    @Transactional(rollbackFor = Exception.class)
    public ResultData<Void> handleTask(TaskHandleRequest handleRequest) {
        // 获取申请单
        RequisitionOrder requisition = this.findOne(handleRequest.getRequisitionId());
        if (Objects.isNull(requisition)) {
            return ResultData.fail("申请单不存在!");
        }

        ResultData<RequisitionOrder> result = flowRuntimeService.handleTask(requisition, handleRequest.getOperationType(), handleRequest.getTaskId(), handleRequest.getMessage());
        if (result.successful()) {
            RequisitionOrder order = result.getData();
            OperateResultWithData<RequisitionOrder> resultWithData = this.save(order);
            if (resultWithData.successful()) {
                ResultData<Void> resultData = ResultData.success();
                // 如果审核通过,更新关联单据状态
                if (ApprovalStatus.PASSED == order.getApprovalStatus()) {
                    String relationId = order.getRelationId();
                    switch (order.getApplyType()) {
                        case APPLICATION:
                            // 应用申请
                            resultData = applicationService.updateFrozen(relationId);
                            break;
                        case MODULE:
                            // 应用模块申请
                            resultData = appModuleService.updateFrozen(relationId);
                            break;
                        case PUBLISH:
                            // 发版申请
                            resultData = versionService.updateFrozen(relationId);
                            break;
                        case DEPLOY:
                            // 部署申请
                            resultData = releaseRecordService.updateFrozen(relationId);
                            break;
                        case PROJECT:
                            // TODO 项目应用申请
                            resultData = ResultData.fail("项目应用申请暂未开发实现");
                            break;
                        default:
                            LogUtil.error("错误的申请类型.");
                            resultData = ResultData.fail("错误的申请类型");
                    }
                }
                if (resultData.successful()) {
                    return ResultData.success();
                } else {
                    // 事务回滚
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return ResultData.fail(resultData.getMessage());
                }
            } else {
                // 事务回滚
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return ResultData.fail(resultWithData.getMessage());
            }
        } else {
            return ResultData.fail(result.getMessage());
        }
    }
}