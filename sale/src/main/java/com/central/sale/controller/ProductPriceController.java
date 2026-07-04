package com.central.sale.controller;

import com.alibaba.excel.EasyExcel;
import com.central.common.result.Result;
import com.central.sale.dto.PageResult;
import com.central.sale.dto.ProductPriceImportDTO;
import com.central.sale.entity.ProductPrice;
import com.central.sale.service.ProductPriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/product-prices")
@RequiredArgsConstructor
public class ProductPriceController {

    private final ProductPriceService productPriceService;

    @GetMapping("/{id}")
    public Result<ProductPrice> findById(@PathVariable(name = "id") Long id) {
        ProductPrice productPrice = productPriceService.findById(id);
        return Result.success(productPrice);
    }

    @GetMapping("/product/{productId}")
    public Result<List<ProductPrice>> findByProductId(@PathVariable(name = "productId") Long productId) {
        List<ProductPrice> prices = productPriceService.findByProductId(productId);
        return Result.success(prices);
    }

    @GetMapping("/search")
    public Result<ProductPrice> findByProductCodeAndDate(
            @RequestParam String productCode,
            @RequestParam LocalDate priceDate) {
        ProductPrice productPrice = productPriceService.findByProductCodeAndDate(productCode, priceDate);
        return Result.success(productPrice);
    }

    @PostMapping
    public Result<ProductPrice> save(@RequestBody ProductPrice productPrice) {
        ProductPrice saved = productPriceService.save(productPrice);
        return Result.success(saved);
    }

    @PutMapping
    public Result<ProductPrice> update(@RequestBody ProductPrice productPrice) {
        ProductPrice updated = productPriceService.update(productPrice);
        return Result.success(updated);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteById(@PathVariable(name = "id") Long id) {
        productPriceService.deleteById(id);
        return Result.success();
    }

    @GetMapping("/list")
    public Result<PageResult<ProductPrice>> findPage(
            @RequestParam(required = false,name = "productName") String productName,
            @RequestParam(required = false,name = "productCode") String productCode,
            @RequestParam(required = false,name = "priceDate") String priceDate,
            @RequestParam(defaultValue = "1",name = "pageNum") Integer pageNum,
            @RequestParam(defaultValue = "10",name = "pageSize") Integer pageSize) {
        PageResult<ProductPrice> pageResult = productPriceService.findPage(productName, productCode, priceDate, pageNum, pageSize);
        return Result.success(pageResult);
    }

    @PostMapping("/import")
    public Result<String> importPrices(@RequestParam("file") MultipartFile file) {
        try {
            // 使用 EasyExcel 读取 Excel 文件
            List<ProductPriceImportDTO> importList = EasyExcel.read(file.getInputStream())
                    .head(ProductPriceImportDTO.class)
                    .sheet()
                    .doReadSync();
            
            // 批量导入
            int successCount = productPriceService.batchImport(importList);
            
            return Result.success("成功导入 " + successCount + " 条记录");
        } catch (IOException e) {
            return Result.error("文件读取失败: " + e.getMessage());
        } catch (Exception e) {
            return Result.error("导入失败: " + e.getMessage());
        }
    }

    @GetMapping("/template")
    public ResponseEntity<byte[]> downloadTemplate() {
        try {
            // 创建示例数据
            List<ProductPriceImportDTO> templateData = new ArrayList<>();
            ProductPriceImportDTO example = new ProductPriceImportDTO();
            example.setProductCode("000001");
            example.setPriceDate("2024-01-15");
            example.setClosePrice(new java.math.BigDecimal("3.50"));
            example.setOpenPrice(new java.math.BigDecimal("3.48"));
            example.setHighPrice(new java.math.BigDecimal("3.52"));
            example.setLowPrice(new java.math.BigDecimal("3.47"));
            example.setVolume(new java.math.BigDecimal("1000000"));
            templateData.add(example);

            // 生成 Excel 文件
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            EasyExcel.write(outputStream, ProductPriceImportDTO.class)
                    .sheet("行情导入模板")
                    .doWrite(templateData);

            // 设置响应头
            String fileName = URLEncoder.encode("行情导入模板.xlsx", StandardCharsets.UTF_8.toString());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", fileName);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(outputStream.toByteArray());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
