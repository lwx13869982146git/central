package com.central.user.service;

import com.central.common.entity.User;

public interface UserService {
    
    User findByUsername(String username);
}
