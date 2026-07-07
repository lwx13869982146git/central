-- =============================================
-- Workflow模块数据库初始化脚本
-- 数据库: workflow
-- =============================================

CREATE DATABASE IF NOT EXISTS workflow CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE workflow;

-- ----------------------------
-- 请假申请表
-- ----------------------------
CREATE TABLE IF NOT EXISTS leave_request (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    applicant_id BIGINT NOT NULL COMMENT '申请人用户ID',
    applicant_name VARCHAR(50) NOT NULL COMMENT '申请人姓名',
    leave_type VARCHAR(20) NOT NULL COMMENT '请假类型: ANNUAL-年假, SICK-病假, PERSONAL-事假',
    start_date DATE NOT NULL COMMENT '请假开始日期',
    end_date DATE NOT NULL COMMENT '请假结束日期',
    reason VARCHAR(500) COMMENT '请假原因',
    status TINYINT DEFAULT 0 COMMENT '状态: 0-待审批, 1-已批准, 2-已拒绝',
    process_instance_id VARCHAR(100) COMMENT 'Flowable流程实例ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_applicant_id (applicant_id),
    INDEX idx_process_instance_id (process_instance_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='请假申请表';

-- =============================================
-- 注意: Flowable会自动创建以下表:
-- ACT_GE_BYTEARRAY, ACT_GE_PROPERTY, ACT_HI_ACTINST
-- ACT_HI_IDENTITYLINK, ACT_HI_PROCINST, ACT_HI_TASKINST
-- ACT_HI_VARINST, ACT_RE_DEPLOYMENT, ACT_RE_PROCDEF
-- ACT_RU_EXECUTION, ACT_RU_IDENTITYLINK, ACT_RU_TASK
-- ACT_RU_VARIABLE
-- =============================================
