package com.central.sale.controller;

import com.central.common.result.Result;
import com.central.sale.entity.Product;
import com.central.sale.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/{id}")
    public Result<Product> findById(@PathVariable(name = "id") Long id) {
        Product product = productService.findById(id);
        return Result.success(product);
    }

    @GetMapping
    public Result<List<Product>> findAll() {
        List<Product> products = productService.findAll();
        return Result.success(products);
    }

    @PostMapping
    public Result<Product> save(@RequestBody Product product) {
        Product saved = productService.save(product);
        return Result.success(saved);
    }

    @PutMapping
    public Result<Product> update(@RequestBody Product product) {
        Product updated = productService.update(product);
        return Result.success(updated);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteById(@PathVariable(name = "id") Long id) {
        productService.deleteById(id);
        return Result.success();
    }
}
