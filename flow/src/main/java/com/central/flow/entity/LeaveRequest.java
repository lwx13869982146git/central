package com.central.flow.entity;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 请假申请实体
 *
 * @author author
 * @date 2026/07/07
 */
@Data
public class LeaveRequest {
    private Long id;
    /** 申请人用户ID */
    private Long applicantId;
    /** 申请人姓名 */
    private String applicantName;
    /** 请假类型: ANNUAL-年假, SICK-病假, PERSONAL-事假 */
    private String leaveType;
    /** 请假开始日期 */
    private LocalDate startDate;
    /** 请假结束日期 */
    private LocalDate endDate;
    /** 请假原因 */
    private String reason;
    /** 状态: 0-待审批, 1-已批准, 2-已拒绝 */
    private Integer status;
    /** 拒绝原因 */
    private String rejectReason;
    /** Flowable流程实例ID */
    private String processInstanceId;
    /** 创建时间 */
    private LocalDateTime createdAt;
    /** 更新时间 */
    private LocalDateTime updatedAt;
}