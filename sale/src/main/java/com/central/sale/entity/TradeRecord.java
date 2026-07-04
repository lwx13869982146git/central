package com.central.sale.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class TradeRecord {
    private Long id;
    private Long productId;
    private String productCode;
    private String productName;
    private LocalDate tradeDate;
    private String tradeType; // BUY-买入, SELL-卖出
    private BigDecimal netValue; // 净值
    private BigDecimal shares; // 份额
    private BigDecimal amount; // 金额（净值 * 份额）
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
