package com.central.sale.service.impl;

import com.central.common.util.RedisUtil;
import com.central.sale.dto.ProductHoldingInfo;
import com.central.sale.entity.Product;
import com.central.sale.entity.TradeRecord;
import com.central.sale.mapper.ProductMapper;
import com.central.sale.mapper.ProductPriceMapper;
import com.central.sale.mapper.TradeRecordMapper;
import com.central.sale.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;
    private final ProductPriceMapper productPriceMapper;
    private final TradeRecordMapper tradeRecordMapper;
    private final RedisUtil redisUtil;
    
    private static final long CACHE_EXPIRE_SECONDS = 300; // 缓存5分钟

    @Override
    public Product findById(Long id) {
        return productMapper.findById(id);
    }

    @Override
    public List<Product> findAll() {
        // 直接查询数据库，不缓存产品列表
        List<Product> products = productMapper.findAll();
        
        // 1. 查询每个产品的最新价格（从 Redis 缓存获取）
        List<Map<String, Object>> priceList = getLatestPriceListFromCache();
        Map<Long, BigDecimal> priceMap = new HashMap<>();
        for (Map<String, Object> priceData : priceList) {
            Long productId = ((Number) priceData.get("productId")).longValue();
            BigDecimal price = (BigDecimal) priceData.get("price");
            priceMap.put(productId, price != null ? price : BigDecimal.ZERO);
        }
        
        // 2. 查询所有交易记录并按产品ID分组（从 Redis 缓存获取）
        Map<Long, List<TradeRecord>> tradeRecordsByProduct = new HashMap<>();
        for (Product product : products) {
            List<TradeRecord> records = getTradeRecordsFromCache(product.getId());
            tradeRecordsByProduct.put(product.getId(), records);
        }
        
        // 3. 为每个产品计算持仓信息并设置到产品对象中
        for (Product product : products) {
            // 获取当前价格
            BigDecimal currentPrice = priceMap.getOrDefault(product.getId(), BigDecimal.ZERO);
            product.setCurrentPrice(currentPrice);
            
            // 计算持仓信息
            ProductHoldingInfo holdingInfo = calculateHoldingInfo(tradeRecordsByProduct.get(product.getId()), currentPrice);
            
            // 设置持有价格、持有数量、盈亏
            product.setHoldingPrice(holdingInfo.getHoldingPrice());
            product.setQuantity(holdingInfo.getQuantity());
            product.setProfitLoss(holdingInfo.getProfitLoss());
            product.setProfitLossRate(holdingInfo.getProfitLossRate());
        }
        
        return products;
    }
    
    /**
     * 根据交易记录计算持仓信息
     */
    private ProductHoldingInfo calculateHoldingInfo(List<TradeRecord> tradeRecords, BigDecimal currentPrice) {
        if (tradeRecords == null || tradeRecords.isEmpty()) {
            return new ProductHoldingInfo(BigDecimal.ZERO, BigDecimal.ZERO, currentPrice, BigDecimal.ZERO, BigDecimal.ZERO);
        }
        
        BigDecimal totalShares = BigDecimal.ZERO; // 总份额
        BigDecimal totalCost = BigDecimal.ZERO; // 总成本
        
        for (TradeRecord record : tradeRecords) {
            if ("BUY".equals(record.getTradeType())) {
                // 买入：增加份额和成本
                totalShares = totalShares.add(record.getShares() != null ? record.getShares() : BigDecimal.ZERO);
                totalCost = totalCost.add(record.getAmount() != null ? record.getAmount() : BigDecimal.ZERO);
            } else if ("SELL".equals(record.getTradeType())) {
                // 卖出：减少份额，按比例减少成本
                BigDecimal sellShares = record.getShares() != null ? record.getShares() : BigDecimal.ZERO;
                BigDecimal sellAmount = record.getAmount() != null ? record.getAmount() : BigDecimal.ZERO;
                
                if (totalShares.compareTo(BigDecimal.ZERO) > 0) {
                    // 计算卖出比例
                    BigDecimal sellRatio = sellShares.divide(totalShares, 10, BigDecimal.ROUND_HALF_UP);
                    // 按比例减少成本
                    totalCost = totalCost.subtract(totalCost.multiply(sellRatio));
                }
                totalShares = totalShares.subtract(sellShares);
            }
        }
        
        // 计算加权平均持有价格
        BigDecimal holdingPrice = BigDecimal.ZERO;
        if (totalShares.compareTo(BigDecimal.ZERO) > 0) {
            holdingPrice = totalCost.divide(totalShares, 4, BigDecimal.ROUND_HALF_UP);
        }
        
        // 计算盈亏
        BigDecimal profitLoss = BigDecimal.ZERO;
        BigDecimal profitLossRate = BigDecimal.ZERO;
        
        if (totalShares.compareTo(BigDecimal.ZERO) > 0 && currentPrice.compareTo(BigDecimal.ZERO) > 0) {
            // 当前市值 = 持有数量 * 当前价格
            BigDecimal currentValue = totalShares.multiply(currentPrice);
            // 盈亏 = 当前市值 - 总成本
            profitLoss = currentValue.subtract(totalCost);
            // 盈亏比例 = 盈亏 / 总成本 * 100%
            if (totalCost.compareTo(BigDecimal.ZERO) > 0) {
                profitLossRate = profitLoss.divide(totalCost, 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100"));
            }
        }
        
        return new ProductHoldingInfo(holdingPrice, totalShares, currentPrice, profitLoss, profitLossRate);
    }

    @Override
    public Product save(Product product) {
        productMapper.insert(product);
        return product;
    }

    @Override
    public Product update(Product product) {
        productMapper.update(product);
        return product;
    }

    @Override
    public void deleteById(Long id) {
        productMapper.deleteById(id);
    }
    
    /**
     * 从缓存获取最新价格列表
     */
    private List<Map<String, Object>> getLatestPriceListFromCache() {
        String cacheKey = "product:price:latest";
        Object cached = redisUtil.get(cacheKey);
        if (cached != null && cached instanceof List) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> priceList = (List<Map<String, Object>>) cached;
            return priceList;
        }
        
        // 缓存未命中，查询数据库
        List<Map<String, Object>> priceList = productPriceMapper.findLatestPriceList();
        // 存入缓存，有效期5分钟
        redisUtil.set(cacheKey, priceList, CACHE_EXPIRE_SECONDS);
        return priceList;
    }
    
    /**
     * 从缓存获取交易记录
     */
    private List<TradeRecord> getTradeRecordsFromCache(Long productId) {
        String cacheKey = "trade:records:product:" + productId;
        Object cached = redisUtil.get(cacheKey);
        if (cached != null && cached instanceof List) {
            @SuppressWarnings("unchecked")
            List<TradeRecord> records = (List<TradeRecord>) cached;
            return records;
        }
        
        // 缓存未命中，查询数据库
        List<TradeRecord> records = tradeRecordMapper.findByProductId(productId);
        // 存入缓存，有效期5分钟
        redisUtil.set(cacheKey, records, CACHE_EXPIRE_SECONDS);
        return records;
    }
}
