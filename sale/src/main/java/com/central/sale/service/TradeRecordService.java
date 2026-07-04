package com.central.sale.service;

import com.central.sale.dto.PageResult;
import com.central.sale.entity.TradeRecord;

import java.util.List;

public interface TradeRecordService {
    
    TradeRecord findById(Long id);

    List<TradeRecord> findByProductId(Long productId);

    TradeRecord save(TradeRecord tradeRecord);

    TradeRecord update(TradeRecord tradeRecord);

    void deleteById(Long id);

    PageResult<TradeRecord> findPage(String productName, String productCode, String tradeType, String tradeDate, Integer pageNum, Integer pageSize);
}
