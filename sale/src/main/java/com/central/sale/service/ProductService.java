package com.central.sale.service;

import com.central.sale.entity.Product;

import java.util.List;

public interface ProductService {
    
    Product findById(Long id);

    List<Product> findAll();

    Product save(Product product);

    Product update(Product product);

    void deleteById(Long id);
}
