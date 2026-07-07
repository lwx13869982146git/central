package com.central.flow.service.impl;

import com.central.flow.dto.LeaveRequestDTO;
import com.central.flow.entity.LeaveRequest;
import com.central.flow.service.LeaveRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 请假申请服务实现
 *
 * @author author
 * @date 2026/07/07
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LeaveRequestServiceImpl implements LeaveRequestService {

    private final RuntimeService runtimeService;
    private final TaskService taskService;

    /** 模拟数据库存储 */
    private static final Map<Long, LeaveRequest> DB = new ConcurrentHashMap<>();
    private static volatile Long idGenerator = 1L;

    private static final String PROCESS_DEFINITION_KEY = "leaveRequest";

    @Override
    public LeaveRequest submit(LeaveRequestDTO dto) {
        Long id = idGenerator++;

        // 1. 保存请假申请实体
        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setId(id);
        leaveRequest.setApplicantId(dto.getApplicantId());
        leaveRequest.setApplicantName(dto.getApplicantName());
        leaveRequest.setLeaveType(dto.getLeaveType());
        leaveRequest.setStartDate(dto.getStartDate());
        leaveRequest.setEndDate(dto.getEndDate());
        leaveRequest.setReason(dto.getReason());
        leaveRequest.setStatus(0); // 待审批
        leaveRequest.setCreatedAt(LocalDateTime.now());
        DB.put(id, leaveRequest);

        // 2. 启动Flowable流程
        Map<String, Object> variables = new HashMap<>();
        variables.put("applicantId", dto.getApplicantId());
        variables.put("applicantName", dto.getApplicantName());
        variables.put("leaveType", dto.getLeaveType());
        variables.put("startDate", dto.getStartDate().toString());
        variables.put("endDate", dto.getEndDate().toString());
        variables.put("reason", dto.getReason());

        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(
                PROCESS_DEFINITION_KEY,
                String.valueOf(id),
                variables
        );

        // 3. 更新流程实例ID
        leaveRequest.setProcessInstanceId(processInstance.getId());
        leaveRequest.setUpdatedAt(LocalDateTime.now());
        DB.put(id, leaveRequest);

        log.info("请假申请已提交: id={}, processInstanceId={}",
                leaveRequest.getId(), processInstance.getId());

        return leaveRequest;
    }

    @Override
    public LeaveRequest getById(Long id) {
        return DB.get(id);
    }

    @Override
    public List<LeaveRequest> getMySubmittedRequests(Long applicantId) {
        List<LeaveRequest> result = new ArrayList<>();
        for (LeaveRequest lr : DB.values()) {
            if (lr.getApplicantId().equals(applicantId)) {
                result.add(lr);
            }
        }
        return result;
    }

    @Override
    public List<LeaveRequest> getPendingApprovalTasks() {
        // 查询经理的待办任务
        List<Task> tasks = taskService.createTaskQuery()
                .processDefinitionKey(PROCESS_DEFINITION_KEY)
                .taskAssignee("manager")
                .list();

        List<LeaveRequest> leaveRequests = new ArrayList<>();
        for (Task task : tasks) {
            String processInstanceId = task.getProcessInstanceId();
            for (LeaveRequest lr : DB.values()) {
                if (processInstanceId.equals(lr.getProcessInstanceId())) {
                    leaveRequests.add(lr);
                    break;
                }
            }
        }
        return leaveRequests;
    }

    @Override
    public void approve(Long id) {
        LeaveRequest leaveRequest = DB.get(id);
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
            taskService.complete(task.getId(), variables);
        }

        // 更新状态
        leaveRequest.setStatus(1); // 已批准
        leaveRequest.setUpdatedAt(LocalDateTime.now());
        DB.put(id, leaveRequest);

        log.info("请假申请已批准: id={}", id);
    }

    @Override
    public void reject(Long id, String reason) {
        LeaveRequest leaveRequest = DB.get(id);
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
            taskService.complete(task.getId(), variables);
        }

        // 更新状态
        leaveRequest.setStatus(2); // 已拒绝
        leaveRequest.setRejectReason(reason);
        leaveRequest.setUpdatedAt(LocalDateTime.now());
        DB.put(id, leaveRequest);

        log.info("请假申请已拒绝: id={}, reason={}", id, reason);
    }
}
