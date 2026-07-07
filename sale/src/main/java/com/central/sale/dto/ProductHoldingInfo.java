package com.central.sale.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductHoldingInfo {
    private BigDecimal holdingPrice; // 持有价格（加权平均）
    private BigDecimal quantity; // 持有数量
    private BigDecimal currentPrice; // 当前价格
    private BigDecimal profitLoss; // 盈亏金额
    private BigDecimal profitLossRate; // 盈亏比例
}
