package com.central.security.feign;

import com.central.common.result.Result;
import com.central.common.entity.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service")
public interface UserFeignClient {

    @GetMapping("/api/users/username/{username}")
    Result<User> findByUsername(@PathVariable("username") String username);
}