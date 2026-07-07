package com.central.workflow.service.impl;

import com.central.workflow.dto.LeaveRequestDTO;
import com.central.workflow.entity.LeaveRequest;
import com.central.workflow.mapper.LeaveRequestMapper;
import com.central.workflow.service.LeaveRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 请假申请服务实现
 *
 * @author author
 * @date 2026/07/06
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LeaveRequestServiceImpl implements LeaveRequestService {

    private final LeaveRequestMapper leaveRequestMapper;
    private final RuntimeService runtimeService;
    private final TaskService taskService;

    private static final String PROCESS_DEFINITION_KEY = "leaveRequest";

    @Override
    @Transactional
    public LeaveRequest submit(LeaveRequestDTO dto) {
        // 1. 保存请假申请实体
        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setApplicantId(dto.getApplicantId());
        leaveRequest.setApplicantName(dto.getApplicantName());
        leaveRequest.setLeaveType(dto.getLeaveType());
        leaveRequest.setStartDate(dto.getStartDate());
        leaveRequest.setEndDate(dto.getEndDate());
        leaveRequest.setReason(dto.getReason());
        leaveRequest.setStatus(0); // 待审批
        leaveRequest.setCreatedAt(LocalDateTime.now());
        leaveRequestMapper.insert(leaveRequest);

        // 2. 启动Flowable流程
        Map<String, Object> variables = new HashMap<>();
        variables.put("applicantId", dto.getApplicantId());
        variables.put("applicantName", dto.getApplicantName());
        variables.put("leaveType", dto.getLeaveType());
        variables.put("startDate", dto.getStartDate().toString());
        variables.put("endDate", dto.getEndDate().toString());
        variables.put("reason", dto.getReason());
        variables.put("status", "PENDING");

        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(
                PROCESS_DEFINITION_KEY,
                String.valueOf(leaveRequest.getId()),
                variables
        );

        // 3. 更新流程实例ID
        leaveRequest.setProcessInstanceId(processInstance.getId());
        leaveRequest.setUpdatedAt(LocalDateTime.now());
        leaveRequestMapper.update(leaveRequest);

        log.info("请假申请已提交: id={}, processInstanceId={}",
                leaveRequest.getId(), processInstance.getId());

        return leaveRequest;
    }

    @Override
    public LeaveRequest getById(Long id) {
        return leaveRequestMapper.findById(id);
    }

    @Override
    public List<LeaveRequest> getMyPendingTasks() {
        // 查询当前用户（经理）的待办任务
        // 注意：实际应用中应从安全上下文获取当前用户ID，这里使用固定值"manager"模拟
        List<Task> tasks = taskService.createTaskQuery()
                .processDefinitionKey(PROCESS_DEFINITION_KEY)
                .taskAssignee("manager")
                .list();

        List<LeaveRequest> leaveRequests = new ArrayList<>();
        for (Task task : tasks) {
            LeaveRequest leaveRequest = leaveRequestMapper.findByProcessInstanceId(
                    task.getProcessInstanceId());
            if (leaveRequest != null) {
                leaveRequests.add(leaveRequest);
            }
        }
        return leaveRequests;
    }

    @Override
    @Transactional
    public void approve(Long id) {
        LeaveRequest leaveRequest = leaveRequestMapper.findById(id);
        if (leaveRequest == null) {
            throw new RuntimeException("请假申请不存在: " + id);
        }

        // 完成审批任务
        Task task = taskService.createTaskQuery()
                .processInstanceId(leaveRequest.getProcessInstanceId())
                .singleResult();

        if (task != null) {
            Map<String, Object> variables = new HashMap<>();
            variables.put("approved", true);
            variables.put("status", "APPROVED");
            taskService.complete(task.getId(), variables);
        }

        // 更新状态
        leaveRequest.setStatus(1); // 已批准
        leaveRequest.setUpdatedAt(LocalDateTime.now());
        leaveRequestMapper.update(leaveRequest);

        log.info("请假申请已批准: id={}", id);
    }

    @Override
    @Transactional
    public void reject(Long id, String reason) {
        LeaveRequest leaveRequest = leaveRequestMapper.findById(id);
        if (leaveRequest == null) {
            throw new RuntimeException("请假申请不存在: " + id);
        }

        // 完成拒绝任务
        Task task = taskService.createTaskQuery()
                .processInstanceId(leaveRequest.getProcessInstanceId())
                .singleResult();

        if (task != null) {
            Map<String, Object> variables = new HashMap<>();
            variables.put("approved", false);
            variables.put("rejectReason", reason);
            variables.put("status", "REJECTED");
            taskService.complete(task.getId(), variables);
        }

        // 更新状态
        leaveRequest.setStatus(2); // 已拒绝
        leaveRequest.setUpdatedAt(LocalDateTime.now());
        leaveRequestMapper.update(leaveRequest);

        log.info("请假申请已拒绝: id={}, reason={}", id, reason);
    }
}
