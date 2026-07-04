package com.central.sale.mapper;

import com.central.sale.entity.ProductPrice;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Mapper
public interface ProductPriceMapper {
    
    @Select("SELECT * FROM product_prices WHERE id = #{id}")
    ProductPrice findById(Long id);

    @Select("SELECT * FROM product_prices WHERE product_id = #{productId} ORDER BY price_date DESC")
    List<ProductPrice> findByProductId(Long productId);

    @Select("SELECT * FROM product_prices WHERE product_code = #{productCode} AND price_date = #{priceDate}")
    ProductPrice findByProductCodeAndDate(@Param("productCode") String productCode, @Param("priceDate") java.time.LocalDate priceDate);

    @Insert("INSERT INTO product_prices(product_id, product_code, price_date, open_price, close_price, " +
            "high_price, low_price, volume) " +
            "VALUES(#{productId}, #{productCode}, #{priceDate}, #{openPrice}, #{closePrice}, " +
            "#{highPrice}, #{lowPrice}, #{volume})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ProductPrice productPrice);

    @Update("UPDATE product_prices SET open_price=#{openPrice}, close_price=#{closePrice}, " +
            "high_price=#{highPrice}, low_price=#{lowPrice}, volume=#{volume} " +
            "WHERE id=#{id}")
    int update(ProductPrice productPrice);

    @Update("UPDATE product_prices SET open_price=#{openPrice}, close_price=#{closePrice}, " +
            "high_price=#{highPrice}, low_price=#{lowPrice}, volume=#{volume} " +
            "WHERE product_code=#{productCode} AND price_date=#{priceDate}")
    int updateByCodeAndDate(ProductPrice productPrice);

    @Delete("DELETE FROM product_prices WHERE id = #{id}")
    int deleteById(Long id);

    @Select("<script>" +
            "SELECT pp.*, p.product_name as productName " +
            "FROM product_prices pp " +
            "LEFT JOIN products p ON pp.product_id = p.id " +
            "WHERE 1=1 " +
            "<if test='productName != null and productName != \"\"'>" +
            "AND p.product_name LIKE CONCAT('%', #{productName}, '%') " +
            "</if>" +
            "<if test='productCode != null and productCode != \"\"'>" +
            "AND pp.product_code LIKE CONCAT('%', #{productCode}, '%') " +
            "</if>" +
            "<if test='priceDate != null and priceDate != \"\"'>" +
            "AND pp.price_date = #{priceDate} " +
            "</if>" +
            "ORDER BY pp.price_date DESC " +
            "LIMIT #{offset}, #{pageSize}" +
            "</script>")
    List<ProductPrice> findPage(Map<String, Object> params);

    @Select("<script>" +
            "SELECT COUNT(*) " +
            "FROM product_prices pp " +
            "LEFT JOIN products p ON pp.product_id = p.id " +
            "WHERE 1=1 " +
            "<if test='productName != null and productName != \"\"'>" +
            "AND p.product_name LIKE CONCAT('%', #{productName}, '%') " +
            "</if>" +
            "<if test='productCode != null and productCode != \"\"'>" +
            "AND pp.product_code LIKE CONCAT('%', #{productCode}, '%') " +
            "</if>" +
            "<if test='priceDate != null and priceDate != \"\"'>" +
            "AND pp.price_date = #{priceDate} " +
            "</if>" +
            "</script>")
    Long countPage(Map<String, Object> params);

    @Select("SELECT pp.product_id as productId, pp.close_price as price FROM product_prices pp " +
            "INNER JOIN (" +
            "  SELECT product_id, MAX(price_date) as max_date " +
            "  FROM product_prices " +
            "  GROUP BY product_id" +
            ") latest ON pp.product_id = latest.product_id AND pp.price_date = latest.max_date")
    List<Map<String, Object>> findLatestPriceList();
}
