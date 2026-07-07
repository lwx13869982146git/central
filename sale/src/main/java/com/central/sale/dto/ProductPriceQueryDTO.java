package com.central.sale.dto;

import lombok.Data;

@Data
public class ProductPriceQueryDTO {
    private String productName;
    private String productCode;
    private String priceDate;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}
