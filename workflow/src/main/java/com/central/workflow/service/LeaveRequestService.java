package com.central.workflow.service;

import com.central.workflow.dto.LeaveRequestDTO;
import com.central.workflow.entity.LeaveRequest;

import java.util.List;

/**
 * 请假申请服务接口
 *
 * @author author
 * @date 2026/07/06
 */
public interface LeaveRequestService {

    /**
     * 提交请假申请（启动工作流）
     *
     * @param dto 请假申请信息
     * @return 请假申请实体
     */
    LeaveRequest submit(LeaveRequestDTO dto);

    /**
     * 根据ID获取请假申请
     *
     * @param id 请假申请ID
     * @return 请假申请实体
     */
    LeaveRequest getById(Long id);

    /**
     * 获取当前用户的待审批任务（经理视图）
     *
     * @return 待审批的请假申请列表
     */
    List<LeaveRequest> getMyPendingTasks();

    /**
     * 批准请假申请
     *
     * @param id 请假申请ID
     */
    void approve(Long id);

    /**
     * 拒绝请假申请
     *
     * @param id 请假申请ID
     * @param reason 拒绝原因
     */
    void reject(Long id, String reason);
}
