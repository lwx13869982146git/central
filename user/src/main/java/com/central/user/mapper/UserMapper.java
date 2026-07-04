package com.central.user.mapper;

import com.central.common.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
    
    @Select("SELECT * FROM users WHERE username = #{username}")
    User findByUsername(String username);
}
