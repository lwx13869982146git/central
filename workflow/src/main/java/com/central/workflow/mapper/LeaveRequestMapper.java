package com.central.workflow.mapper;

import com.central.workflow.entity.LeaveRequest;
import org.apache.ibatis.annotations.*;

/**
 * 请假申请Mapper
 *
 * @author author
 * @date 2026/07/06
 */
@Mapper
public interface LeaveRequestMapper {

    @Insert("INSERT INTO leave_request (applicant_id, applicant_name, leave_type, " +
            "start_date, end_date, reason, status, process_instance_id, created_at) " +
            "VALUES (#{applicantId}, #{applicantName}, #{leaveType}, " +
            "#{startDate}, #{endDate}, #{reason}, #{status}, #{processInstanceId}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(LeaveRequest leaveRequest);

    @Update("UPDATE leave_request SET status=#{status}, process_instance_id=#{processInstanceId}, " +
            "updated_at=#{updatedAt} WHERE id=#{id}")
    void update(LeaveRequest leaveRequest);

    @Select("SELECT * FROM leave_request WHERE id = #{id}")
    LeaveRequest findById(Long id);

    @Select("SELECT * FROM leave_request WHERE process_instance_id = #{processInstanceId}")
    LeaveRequest findByProcessInstanceId(String processInstanceId);

    @Select("SELECT * FROM leave_request WHERE applicant_id = #{applicantId} ORDER BY created_at DESC")
    java.util.List<LeaveRequest> findByApplicantId(Long applicantId);
}
