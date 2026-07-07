package com.central.sale.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ProductPrice {
    private Long id;
    private Long productId;
    private String productCode;
    private String productName;
    private LocalDate priceDate;
    private BigDecimal openPrice;
    private BigDecimal closePrice;
    private BigDecimal highPrice;
    private BigDecimal lowPrice;
    private BigDecimal volume;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
