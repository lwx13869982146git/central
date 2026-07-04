package com.central.sale.service.impl;

import com.central.common.util.RedisUtil;
import com.central.sale.dto.PageResult;
import com.central.sale.entity.TradeRecord;
import com.central.sale.mapper.TradeRecordMapper;
import com.central.sale.service.TradeRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TradeRecordServiceImpl implements TradeRecordService {

    private final TradeRecordMapper tradeRecordMapper;
    private final RedisUtil redisUtil;

    @Override
    public TradeRecord findById(Long id) {
        return tradeRecordMapper.findById(id);
    }

    @Override
    public List<TradeRecord> findByProductId(Long productId) {
        return tradeRecordMapper.findByProductId(productId);
    }

    @Override
    public TradeRecord save(TradeRecord tradeRecord) {
        // 计算金额 = 净值 * 份额
        if (tradeRecord.getNetValue() != null && tradeRecord.getShares() != null) {
            tradeRecord.setAmount(tradeRecord.getNetValue().multiply(tradeRecord.getShares()));
        }
        tradeRecordMapper.insert(tradeRecord);
        // 清除该产品的交易记录缓存
        clearTradeRecordsCache(tradeRecord.getProductId());
        return tradeRecord;
    }

    @Override
    public TradeRecord update(TradeRecord tradeRecord) {
        // 计算金额 = 净值 * 份额
        if (tradeRecord.getNetValue() != null && tradeRecord.getShares() != null) {
            tradeRecord.setAmount(tradeRecord.getNetValue().multiply(tradeRecord.getShares()));
        }
        tradeRecordMapper.update(tradeRecord);
        // 清除该产品的交易记录缓存
        clearTradeRecordsCache(tradeRecord.getProductId());
        return tradeRecord;
    }

    @Override
    public void deleteById(Long id) {
        // 先查询获取 productId
        TradeRecord record = tradeRecordMapper.findById(id);
        tradeRecordMapper.deleteById(id);
        // 清除该产品的交易记录缓存
        if (record != null && record.getProductId() != null) {
            clearTradeRecordsCache(record.getProductId());
        }
    }
    
    /**
     * 清除指定产品的交易记录缓存
     */
    private void clearTradeRecordsCache(Long productId) {
        if (productId != null) {
            String cacheKey = "trade:records:product:" + productId;
            redisUtil.del(cacheKey);
        }
    }

    @Override
    public PageResult<TradeRecord> findPage(String productName, String productCode, String tradeType, String tradeDate, Integer pageNum, Integer pageSize) {
        Map<String, Object> params = new HashMap<>();
        params.put("productName", productName);
        params.put("productCode", productCode);
        params.put("tradeType", tradeType);
        params.put("tradeDate", tradeDate);
        params.put("offset", (pageNum - 1) * pageSize);
        params.put("pageSize", pageSize);

        List<TradeRecord> list = tradeRecordMapper.findPage(params);
        Long total = tradeRecordMapper.countPage(params);

        return new PageResult<>(list, total);
    }
}
