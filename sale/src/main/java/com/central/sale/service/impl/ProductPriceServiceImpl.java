package com.central.sale.service.impl;

import com.central.common.util.RedisUtil;
import com.central.sale.dto.PageResult;
import com.central.sale.dto.ProductPriceImportDTO;
import com.central.sale.entity.ProductPrice;
import com.central.sale.mapper.ProductMapper;
import com.central.sale.mapper.ProductPriceMapper;
import com.central.sale.service.ProductPriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductPriceServiceImpl implements ProductPriceService {

    private final ProductPriceMapper productPriceMapper;
    private final ProductMapper productMapper;
    private final RedisUtil redisUtil;
    
    private static final String PRICE_CACHE_KEY = "product:price:latest";

    @Override
    public ProductPrice findById(Long id) {
        return productPriceMapper.findById(id);
    }

    @Override
    public List<ProductPrice> findByProductId(Long productId) {
        return productPriceMapper.findByProductId(productId);
    }

    @Override
    public ProductPrice findByProductCodeAndDate(String productCode, java.time.LocalDate priceDate) {
        return productPriceMapper.findByProductCodeAndDate(productCode, priceDate);
    }

    @Override
    public ProductPrice save(ProductPrice productPrice) {
        productPriceMapper.insert(productPrice);
        // 清除价格缓存
        redisUtil.del(PRICE_CACHE_KEY);
        return productPrice;
    }

    @Override
    public ProductPrice update(ProductPrice productPrice) {
        productPriceMapper.update(productPrice);
        // 清除价格缓存
        redisUtil.del(PRICE_CACHE_KEY);
        return productPrice;
    }

    @Override
    public void deleteById(Long id) {
        productPriceMapper.deleteById(id);
        // 清除价格缓存
        redisUtil.del(PRICE_CACHE_KEY);
    }

    @Override
    public PageResult<ProductPrice> findPage(String productName, String productCode, String priceDate, Integer pageNum, Integer pageSize) {
        Map<String, Object> params = new HashMap<>();
        params.put("productName", productName);
        params.put("productCode", productCode);
        params.put("priceDate", priceDate);
        params.put("offset", (pageNum - 1) * pageSize);
        params.put("pageSize", pageSize);

        List<ProductPrice> list = productPriceMapper.findPage(params);
        Long total = productPriceMapper.countPage(params);

        return new PageResult<>(list, total);
    }

    @Override
    public int batchImport(List<ProductPriceImportDTO> importList) {
        if (importList == null || importList.isEmpty()) {
            return 0;
        }

        int successCount = 0;
        for (ProductPriceImportDTO dto : importList) {
            try {
                // 根据产品代码查询产品ID
                Long productId = productMapper.findIdByProductCode(dto.getProductCode());
                if (productId == null) {
                    System.err.println("产品代码不存在: " + dto.getProductCode());
                    continue;
                }
                
                // 解析日期，支持多种格式：yyyy-MM-dd, yyyy/M/d, yyyy/MM/dd
                LocalDate priceDate = parseDate(dto.getPriceDate());
                if (priceDate == null) {
                    System.err.println("日期格式错误: " + dto.getPriceDate());
                    continue;
                }
                
                // 检查是否已存在
                ProductPrice existing = productPriceMapper.findByProductCodeAndDate(dto.getProductCode(), priceDate);
                
                ProductPrice productPrice = new ProductPrice();
                productPrice.setProductId(productId);
                productPrice.setProductCode(dto.getProductCode());
                productPrice.setPriceDate(priceDate);
                productPrice.setClosePrice(dto.getClosePrice());
                productPrice.setOpenPrice(dto.getOpenPrice());
                productPrice.setHighPrice(dto.getHighPrice());
                productPrice.setLowPrice(dto.getLowPrice());
                productPrice.setVolume(dto.getVolume());
                
                if (existing != null) {
                    // 如果已存在，覆盖更新
                    productPrice.setId(existing.getId());
                    productPriceMapper.update(productPrice);
                } else {
                    // 如果不存在，插入新记录
                    productPriceMapper.insert(productPrice);
                }
                
                successCount++;
            } catch (Exception e) {
                // 记录错误，继续处理下一条
                System.err.println("导入失败: " + dto.getProductCode() + ", " + dto.getPriceDate() + ", 错误: " + e.getMessage());
            }
        }
        
        return successCount;
    }
    
    /**
     * 解析日期字符串，支持多种格式
     * 支持的格式：yyyy-MM-dd, yyyy/M/d, yyyy/MM/dd, yyyy-M-d
     */
    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        
        dateStr = dateStr.trim();
        
        // 尝试多种日期格式
        String[] patterns = {
            "yyyy-MM-dd",
            "yyyy/MM/dd",
            "yyyy/M/d",
            "yyyy-M-d"
        };
        
        for (String pattern : patterns) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
                return LocalDate.parse(dateStr, formatter);
            } catch (Exception e) {
                // 尝试下一种格式
            }
        }
        
        return null;
    }
}
