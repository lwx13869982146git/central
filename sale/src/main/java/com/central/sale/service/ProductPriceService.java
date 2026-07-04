package com.central.sale.service;

import com.central.sale.dto.PageResult;
import com.central.sale.entity.ProductPrice;

import java.util.List;

public interface ProductPriceService {
    
    ProductPrice findById(Long id);

    List<ProductPrice> findByProductId(Long productId);

    ProductPrice findByProductCodeAndDate(String productCode, java.time.LocalDate priceDate);

    ProductPrice save(ProductPrice productPrice);

    ProductPrice update(ProductPrice productPrice);

    void deleteById(Long id);

    PageResult<ProductPrice> findPage(String productName, String productCode, String priceDate, Integer pageNum, Integer pageSize);

    int batchImport(List<com.central.sale.dto.ProductPriceImportDTO> importList);
}
