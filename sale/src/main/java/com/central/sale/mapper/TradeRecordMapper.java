package com.central.sale.mapper;

import com.central.sale.entity.TradeRecord;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface TradeRecordMapper {
    
    @Select("SELECT * FROM trade_records WHERE id = #{id}")
    TradeRecord findById(Long id);

    @Select("SELECT * FROM trade_records WHERE product_id = #{productId} ORDER BY trade_date DESC")
    List<TradeRecord> findByProductId(Long productId);

    @Insert("INSERT INTO trade_records(product_id, product_code, trade_date, trade_type, " +
            "net_value, shares, amount, notes) " +
            "VALUES(#{productId}, #{productCode}, #{tradeDate}, #{tradeType}, " +
            "#{netValue}, #{shares}, #{amount}, #{notes})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(TradeRecord tradeRecord);

    @Update("UPDATE trade_records SET trade_date=#{tradeDate}, trade_type=#{tradeType}, " +
            "net_value=#{netValue}, shares=#{shares}, amount=#{amount}, notes=#{notes} " +
            "WHERE id=#{id}")
    int update(TradeRecord tradeRecord);

    @Delete("DELETE FROM trade_records WHERE id = #{id}")
    int deleteById(Long id);

    @Select("<script>" +
            "SELECT tr.*, p.product_name as productName " +
            "FROM trade_records tr " +
            "LEFT JOIN products p ON tr.product_id = p.id " +
            "WHERE 1=1 " +
            "<if test='productName != null and productName != \"\"'>" +
            "AND p.product_name LIKE CONCAT('%', #{productName}, '%') " +
            "</if>" +
            "<if test='productCode != null and productCode != \"\"'>" +
            "AND tr.product_code LIKE CONCAT('%', #{productCode}, '%') " +
            "</if>" +
            "<if test='tradeType != null and tradeType != \"\"'>" +
            "AND tr.trade_type = #{tradeType} " +
            "</if>" +
            "<if test='tradeDate != null and tradeDate != \"\"'>" +
            "AND tr.trade_date = #{tradeDate} " +
            "</if>" +
            "ORDER BY tr.trade_date DESC " +
            "LIMIT #{offset}, #{pageSize}" +
            "</script>")
    java.util.List<com.central.sale.entity.TradeRecord> findPage(java.util.Map<String, Object> params);

    @Select("<script>" +
            "SELECT COUNT(*) " +
            "FROM trade_records tr " +
            "LEFT JOIN products p ON tr.product_id = p.id " +
            "WHERE 1=1 " +
            "<if test='productName != null and productName != \"\"'>" +
            "AND p.product_name LIKE CONCAT('%', #{productName}, '%') " +
            "</if>" +
            "<if test='productCode != null and productCode != \"\"'>" +
            "AND tr.product_code LIKE CONCAT('%', #{productCode}, '%') " +
            "</if>" +
            "<if test='tradeType != null and tradeType != \"\"'>" +
            "AND tr.trade_type = #{tradeType} " +
            "</if>" +
            "<if test='tradeDate != null and tradeDate != \"\"'>" +
            "AND tr.trade_date = #{tradeDate} " +
            "</if>" +
            "</script>")
    Long countPage(java.util.Map<String, Object> params);
}
