-- 为 products 表添加 holding_price 字段
ALTER TABLE products ADD COLUMN holding_price DECIMAL(15, 4) COMMENT '持有价格' AFTER purchase_price;
