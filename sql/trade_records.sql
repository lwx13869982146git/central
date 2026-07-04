-- 交易记录表
CREATE TABLE trade_records (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_id BIGINT NOT NULL COMMENT '产品ID',
    product_code VARCHAR(50) NOT NULL COMMENT '产品代码',
    trade_date DATE NOT NULL COMMENT '交易日期',
    trade_type VARCHAR(10) NOT NULL COMMENT '交易类型：BUY-买入，SELL-卖出',
    net_value DECIMAL(15, 4) COMMENT '净值',
    shares DECIMAL(20, 4) COMMENT '份额',
    amount DECIMAL(20, 4) COMMENT '金额（净值 * 份额）',
    notes VARCHAR(500) COMMENT '备注',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_product_id (product_id),
    INDEX idx_trade_date (trade_date),
    INDEX idx_trade_type (trade_type)
);
