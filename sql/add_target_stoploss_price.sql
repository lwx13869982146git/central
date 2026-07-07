-- 为 products 表添加目标价格和止损价格字段
ALTER TABLE products ADD COLUMN target_price DECIMAL(10, 4) COMMENT '目标价格';
ALTER TABLE products ADD COLUMN stop_loss_price DECIMAL(10, 4) COMMENT '止损价格';
