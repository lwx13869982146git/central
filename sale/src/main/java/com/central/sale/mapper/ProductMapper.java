package com.central.sale.mapper;

import com.central.sale.entity.Product;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ProductMapper {
    
    @Select("SELECT * FROM products WHERE id = #{id}")
    Product findById(Long id);

    @Select("SELECT * FROM products")
    List<Product> findAll();

    @Select("SELECT id FROM products WHERE product_code = #{productCode}")
    Long findIdByProductCode(String productCode);

    @Insert("INSERT INTO products(product_name, product_code, product_type, market, currency, " +
            "holding_price, current_price, quantity, current_value, " +
            "profit_loss, profit_loss_rate, target_price, stop_loss_price, notes, status) " +
            "VALUES(#{productName}, #{productCode}, #{productType}, #{market}, #{currency}, " +
            "#{holdingPrice}, #{currentPrice}, #{quantity}, #{currentValue}, " +
            "#{profitLoss}, #{profitLossRate}, #{targetPrice}, #{stopLossPrice}, #{notes}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Product product);

    @Update("UPDATE products SET product_name=#{productName}, product_code=#{productCode}, " +
            "product_type=#{productType}, market=#{market}, currency=#{currency}, " +
            "current_price=#{currentPrice}, quantity=#{quantity}, current_value=#{currentValue}, " +
            "profit_loss=#{profitLoss}, profit_loss_rate=#{profitLossRate}, " +
            "target_price=#{targetPrice}, stop_loss_price=#{stopLossPrice}, " +
            "notes=#{notes}, status=#{status} WHERE id=#{id}")
    int update(Product product);

    @Delete("DELETE FROM products WHERE id = #{id}")
    int deleteById(Long id);
}
