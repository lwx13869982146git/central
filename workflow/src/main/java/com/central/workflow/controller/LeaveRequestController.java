package com.central.workflow.controller;

import com.central.common.result.Result;
import com.central.workflow.dto.LeaveRequestDTO;
import com.central.workflow.entity.LeaveRequest;
import com.central.workflow.service.LeaveRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 请假申请控制器
 *
 * @author author
 * @date 2026/07/06
 */
@RestController
@RequestMapping("/api/leave-requests")
@RequiredArgsConstructor
public class LeaveRequestController {

    private final LeaveRequestService leaveRequestService;

    /**
     * 提交请假申请 - 启动工作流流程
     */
    @PostMapping
    public Result<LeaveRequest> submit(@RequestBody LeaveRequestDTO dto) {
        LeaveRequest leaveRequest = leaveRequestService.submit(dto);
        return Result.success(leaveRequest);
    }

    /**
     * 根据ID获取请假申请详情
     */
    @GetMapping("/{id}")
    public Result<LeaveRequest> getById(@PathVariable(name = "id") Long id) {
        LeaveRequest leaveRequest = leaveRequestService.getById(id);
        return Result.success(leaveRequest);
    }

    /**
     * 获取当前用户（经理）的待审批任务
     */
    @GetMapping("/tasks")
    public Result<List<LeaveRequest>> getMyTasks() {
        List<LeaveRequest> tasks = leaveRequestService.getMyPendingTasks();
        return Result.success(tasks);
    }

    /**
     * 批准请假申请
     */
    @PostMapping("/{id}/approve")
    public Result<Void> approve(@PathVariable(name = "id") Long id) {
        leaveRequestService.approve(id);
        return Result.success();
    }

    /**
     * 拒绝请假申请
     */
    @PostMapping("/{id}/reject")
    public Result<Void> reject(@PathVariable(name = "id") Long id,
                               @RequestParam(required = false) String reason) {
        leaveRequestService.reject(id, reason);
        return Result.success();
    }
}
