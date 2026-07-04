-- 产品行情价格表
CREATE TABLE product_prices (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_id BIGINT NOT NULL COMMENT '产品ID',
    product_code VARCHAR(50) NOT NULL COMMENT '产品代码',
    price_date DATE NOT NULL COMMENT '价格日期',
    open_price DECIMAL(15, 4) COMMENT '开盘价',
    close_price DECIMAL(15, 4) COMMENT '收盘价',
    high_price DECIMAL(15, 4) COMMENT '最高价',
    low_price DECIMAL(15, 4) COMMENT '最低价',
    volume DECIMAL(20, 4) COMMENT '成交量',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    INDEX idx_product_id (product_id),
    INDEX idx_product_code (product_code),
    INDEX idx_price_date (price_date),
    UNIQUE KEY uk_product_date (product_code, price_date)
);
