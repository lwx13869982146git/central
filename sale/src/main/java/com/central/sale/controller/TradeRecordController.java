package com.central.sale.controller;

import com.central.common.result.Result;
import com.central.sale.dto.PageResult;
import com.central.sale.entity.TradeRecord;
import com.central.sale.service.TradeRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trade-records")
@RequiredArgsConstructor
public class TradeRecordController {

    private final TradeRecordService tradeRecordService;

    @GetMapping("/{id}")
    public Result<TradeRecord> findById(@PathVariable(name = "id") Long id) {
        TradeRecord tradeRecord = tradeRecordService.findById(id);
        return Result.success(tradeRecord);
    }

    @GetMapping("/product/{productId}")
    public Result<List<TradeRecord>> findByProductId(@PathVariable(name = "productId") Long productId) {
        List<TradeRecord> records = tradeRecordService.findByProductId(productId);
        return Result.success(records);
    }

    @PostMapping
    public Result<TradeRecord> save(@RequestBody TradeRecord tradeRecord) {
        TradeRecord saved = tradeRecordService.save(tradeRecord);
        return Result.success(saved);
    }

    @PutMapping
    public Result<TradeRecord> update(@RequestBody TradeRecord tradeRecord) {
        TradeRecord updated = tradeRecordService.update(tradeRecord);
        return Result.success(updated);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteById(@PathVariable(name = "id") Long id) {
        tradeRecordService.deleteById(id);
        return Result.success();
    }

    @GetMapping("/list")
    public Result<PageResult<TradeRecord>> findPage(
            @RequestParam(required = false, name = "productName") String productName,
            @RequestParam(required = false, name = "productCode") String productCode,
            @RequestParam(required = false, name = "tradeType") String tradeType,
            @RequestParam(required = false, name = "tradeDate") String tradeDate,
            @RequestParam(defaultValue = "1", name = "pageNum") Integer pageNum,
            @RequestParam(defaultValue = "10", name = "pageSize") Integer pageSize) {
        PageResult<TradeRecord> pageResult = tradeRecordService.findPage(productName, productCode, tradeType, tradeDate, pageNum, pageSize);
        return Result.success(pageResult);
    }
}
