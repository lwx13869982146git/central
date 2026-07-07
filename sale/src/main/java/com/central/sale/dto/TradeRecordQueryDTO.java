package com.central.sale.dto;

import lombok.Data;

@Data
public class TradeRecordQueryDTO {
    private String productName;
    private String productCode;
    private String tradeType;
    private String tradeDate;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}
