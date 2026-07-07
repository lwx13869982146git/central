package com.central.flow.dto;

import lombok.Data;
import java.time.LocalDate;

/**
 * 请假申请请求DTO
 *
 * @author author
 * @date 2026/07/07
 */
@Data
public class LeaveRequestDTO {
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
}