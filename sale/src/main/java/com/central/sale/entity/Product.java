package com.central.sale.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class Product {
    private Long id;
    private String productName;
    private String productCode;
    private String productType;
    private String market;
    private String currency;
    private BigDecimal holdingPrice;
    private BigDecimal currentPrice;
    private BigDecimal quantity;
    private BigDecimal currentValue;
    private BigDecimal profitLoss;
    private BigDecimal profitLossRate;
    private BigDecimal targetPrice;
    private BigDecimal stopLossPrice;
    private String notes;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
