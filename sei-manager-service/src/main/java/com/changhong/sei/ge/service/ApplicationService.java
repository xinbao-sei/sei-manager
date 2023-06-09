package com.changhong.sei.ge.service;

import com.changhong.sei.cicd.dao.ApplicationRequisitionDao;
import com.changhong.sei.cicd.dto.ApplicationRequisitionDto;
import com.changhong.sei.cicd.dto.ApplyType;
import com.changhong.sei.cicd.dto.ApprovalStatus;
import com.changhong.sei.cicd.entity.ApplicationRequisition;
import com.changhong.sei.cicd.entity.RequisitionOrder;
import com.changhong.sei.cicd.service.RequisitionOrderService;
import com.changhong.sei.common.ObjectType;
import com.changhong.sei.core.context.ContextUtil;
import com.changhong.sei.core.dao.BaseEntityDao;
import com.changhong.sei.core.dto.ResultData;
import com.changhong.sei.core.dto.serach.PageResult;
import com.changhong.sei.core.dto.serach.Search;
import com.changhong.sei.core.dto.serach.SearchFilter;
import com.changhong.sei.core.service.BaseEntityService;
import com.changhong.sei.core.service.bo.OperateResult;
import com.changhong.sei.core.service.bo.OperateResultWithData;
import com.changhong.sei.ge.dao.ApplicationDao;
import com.changhong.sei.ge.entity.AppModule;
import com.changhong.sei.ge.entity.Application;
import com.changhong.sei.manager.entity.User;
import com.changhong.sei.manager.service.UserService;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.Objects;

/**
 * 应用服务(Application)业务逻辑实现类
 *
 * @author sei
 * @since 2020-10-30 15:20:57
 */
@Service
public class ApplicationService extends BaseEntityService<Application> {
    @Autowired
    private ApplicationDao dao;
    @Autowired
    private ApplicationRequisitionDao applicationRequisitionDao;
    @Autowired
    private AppModuleService appModuleService;
    @Autowired
    private RequisitionOrderService requisitionOrderService;
    @Autowired
    private UserService userService;
    @Autowired
    private ProjectUserService projectUserService;

    @Override
    protected BaseEntityDao<Application> getDao() {
        return dao;
    }

    /**
     * 删除数据保存数据之前额外操作回调方法 子类根据需要覆写添加逻辑即可
     *
     * @param id 待删除数据对象主键
     */
    @Override
    protected OperateResult preDelete(String id) {
        // 检查状态控制删除
        Application app = this.findOne(id);
        if (Objects.isNull(app)) {
            return OperateResult.operationFailure("[" + id + "]应用不存在,删除失败!");
        }
        if (!app.getFrozen()) {
            return OperateResult.operationFailure("[" + id + "]应用已审核确认,不允许删除!");
        }
        if (appModuleService.isExistsByProperty(AppModule.FIELD_APP_ID, id)) {
            return OperateResult.operationFailure("[" + id + "]应用存在应用模块,不允许删除!");
        }
        OperateResult result = super.preDelete(id);
        if (result.successful()) {
            // 移除应用管理员授权
            ContextUtil.getBean(ProjectUserService.class).cancelAssign(app.getId(), Sets.newHashSet(app.getManagerAccount()));
        }
        return result;
    }

    /**
     * 分页查询应用申请单
     *
     * @param search 查询参数
     * @return 分页查询结果
     */
    public PageResult<ApplicationRequisition> findRequisitionByPage(Search search) {
        if (Objects.isNull(search)) {
            search = Search.createSearch();
        }
        search.addFilter(new SearchFilter(ApplicationRequisition.APPLICANT_ACCOUNT, ContextUtil.getUserAccount()));
        return applicationRequisitionDao.findByPage(search);
    }

    /**
     * 创建应用申请单
     *
     * @param application 应用
     * @return 操作结果
     */
    @Transactional(rollbackFor = Exception.class)
    public ResultData<ApplicationRequisitionDto> createRequisition(Application application) {
        // 申请是设置为冻结状态,带申请审核确认后再值为可用状态
        application.setFrozen(Boolean.TRUE);
        // 保存应用
        OperateResultWithData<Application> resultWithData = this.save(application);
        if (resultWithData.successful()) {
            RequisitionOrder requisitionOrder = new RequisitionOrder();
            // 申请类型:应用申请
            requisitionOrder.setApplyType(ApplyType.APPLICATION);
            // 应用id
            requisitionOrder.setRelationId(application.getId());
            // 申请摘要
            requisitionOrder.setSummary(application.getGroupName().concat("-")
                    .concat(application.getName())
                    .concat("[").concat(application.getCode()).concat("]"));

            ResultData<RequisitionOrder> result = requisitionOrderService.createRequisition(requisitionOrder);
            if (result.successful()) {
                RequisitionOrder requisition = result.getData();
                ApplicationRequisitionDto dto = new ApplicationRequisitionDto();
                dto.setId(requisition.getId());
                dto.setApplicantAccount(requisition.getApplicantAccount());
                dto.setApplicantUserName(requisition.getApplicantUserName());
                dto.setApplicationTime(requisition.getApplicationTime());
                dto.setApplyType(requisition.getApplyType());
                dto.setApprovalStatus(requisition.getApprovalStatus());
                dto.setRelationId(application.getId());
                dto.setCode(application.getCode());
                dto.setName(application.getName());
                dto.setGroupCode(application.getGroupCode());
                dto.setGroupName(application.getGroupName());
                dto.setVersion(application.getVersion());
                dto.setRemark(application.getRemark());
                return ResultData.success(dto);
            } else {
                // 事务回滚
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return ResultData.fail(result.getMessage());
            }
        } else {
            return ResultData.fail(resultWithData.getMessage());
        }
    }

    /**
     * 创建应用申请单
     *
     * @param application 应用
     * @return 操作结果
     */
    @Transactional(rollbackFor = Exception.class)
    public ResultData<ApplicationRequisitionDto> modifyRequisition(Application application) {
        Application entity = this.findOne(application.getId());
        if (Objects.isNull(entity)) {
            return ResultData.fail("应用不存在!");
        }
        // 检查应用审核状态
        if (!entity.getFrozen()) {
            return ResultData.fail("应用已审核,不允许编辑!");
        }

        entity.setCode(application.getCode());
        entity.setName(application.getName());
        entity.setGroupCode(application.getGroupCode());
        entity.setGroupName(application.getGroupName());
        entity.setVersion(application.getVersion());
        entity.setRemark(application.getRemark());

        // 保存应用
        OperateResultWithData<Application> resultWithData = this.save(entity);
        if (resultWithData.successful()) {
            RequisitionOrder requisitionOrder = requisitionOrderService.getByRelationId(entity.getId());
            if (Objects.isNull(requisitionOrder)) {
                // 事务回滚
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return ResultData.fail("申请单不存在!");
            }
            // 检查申请单是否已审核
            if (ApprovalStatus.INITIAL != requisitionOrder.getApprovalStatus()) {
                // 事务回滚
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return ResultData.fail("申请单不存在!");
            }

            // 申请类型:应用申请
            requisitionOrder.setApplyType(ApplyType.APPLICATION);
            // 应用id
            requisitionOrder.setRelationId(entity.getId());
            // 申请摘要
            requisitionOrder.setSummary(entity.getGroupName().concat("-")
                    .concat(entity.getName())
                    .concat("[").concat(entity.getCode()).concat("]"));

            ResultData<RequisitionOrder> result = requisitionOrderService.modifyRequisition(requisitionOrder);
            if (result.successful()) {
                RequisitionOrder requisition = result.getData();
                ApplicationRequisitionDto dto = new ApplicationRequisitionDto();
                dto.setId(requisition.getId());
                dto.setApplicantAccount(requisition.getApplicantAccount());
                dto.setApplicantUserName(requisition.getApplicantUserName());
                dto.setApplicationTime(requisition.getApplicationTime());
                dto.setApplyType(requisition.getApplyType());
                dto.setApprovalStatus(requisition.getApprovalStatus());
                dto.setRelationId(application.getId());
                dto.setCode(application.getCode());
                dto.setName(application.getName());
                dto.setGroupCode(application.getGroupCode());
                dto.setGroupName(application.getGroupName());
                dto.setVersion(application.getVersion());
                dto.setRemark(application.getRemark());
                return ResultData.success(dto);
            } else {
                // 事务回滚
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return ResultData.fail(result.getMessage());
            }
        } else {
            return ResultData.fail(resultWithData.getMessage());
        }
    }

    /**
     * 创建应用申请单
     *
     * @param id@return 操作结果
     */
    @Transactional(rollbackFor = Exception.class)
    public ResultData<Void> deleteRequisition(String id) {
        Application application = this.findOne(id);
        if (Objects.nonNull(application)) {
            if (application.getFrozen()) {
                // 删除应用
                this.delete(id);

                // 删除申请单
                ResultData<Void> resultData = requisitionOrderService.deleteRequisition(id);
                if (resultData.successful()) {
                    return ResultData.success();
                } else {
                    // 事务回滚
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return ResultData.fail(resultData.getMessage());
                }
            } else {
                return ResultData.fail("应用已审核,禁止删除!");
            }
        }
        return ResultData.fail("应用不存在!");
    }

    /**
     * 流程审核完成,更新冻结状态为:启用
     */
    @Transactional(rollbackFor = Exception.class)
    public ResultData<Void> updateFrozen(String id) {
        Application application = this.findOne(id);
        if (Objects.nonNull(application)) {
            application.setFrozen(Boolean.FALSE);
        }
        // 默认将申请人作为应用的管理员
        projectUserService.assign(application.getCreatorAccount(), application.getId(), application.getName(), ObjectType.APPLICATION);
        OperateResultWithData<Application> result = this.save(application);
        if (result.successful()) {
            return ResultData.success();
        } else {
            return ResultData.fail(result.getMessage());
        }
    }

    /**
     * 分配应用管理员
     *
     * @return 返回分配结果
     */
    @Transactional(rollbackFor = Exception.class)
    public ResultData<Void> assignManager(String account, String appId) {
        if (StringUtils.isBlank(account)) {
            return ResultData.fail("分配的账号不能为空");
        }
        if (StringUtils.isBlank(appId)) {
            return ResultData.fail("应用id不能为空.");
        }

        User user = userService.findByProperty(User.FIELD_ACCOUNT, account);
        if (Objects.isNull(user)) {
            return ResultData.fail("用户[" + account + "]不存在");
        }

        Application application = dao.findByProperty(Application.ID, appId);
        if (Objects.isNull(application)) {
            return ResultData.fail("应用[" + appId + "]不存在");
        }

        if (StringUtils.isNotBlank(application.getManagerAccount())) {
            // 移除权限
            projectUserService.cancelAssign(appId, Sets.newHashSet(application.getManagerAccount()));
        }

        ResultData<Void> resultData = projectUserService.assign(account, appId, application.getName(), ObjectType.APPLICATION);
        if (resultData.successful()) {
            application.setManagerAccount(user.getAccount());
            application.setManagerAccountName(user.getUserName());
            this.save(application);
        }
        return resultData;
    }
}
