package com.central.sale.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductPriceImportDTO {
    
    @ExcelProperty(index = 0, value = "产品代码")
    private String productCode;
    
    @ExcelProperty(index = 1, value = "日期")
    private String priceDate;
    
    @ExcelProperty(index = 2, value = "收盘价")
    private BigDecimal closePrice;
    
    @ExcelProperty(index = 3, value = "开盘价")
    private BigDecimal openPrice;
    
    @ExcelProperty(index = 4, value = "最高价")
    private BigDecimal highPrice;
    
    @ExcelProperty(index = 5, value = "最低价")
    private BigDecimal lowPrice;
    
    @ExcelProperty(index = 6, value = "成交量")
    private BigDecimal volume;
}
