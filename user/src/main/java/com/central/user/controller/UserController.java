package com.central.user.controller;

import com.central.common.result.Result;
import com.central.common.entity.User;
import com.central.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/username/{username}")
    public Result<User> findByUsername(@PathVariable("username") String username) {
        User user = userService.findByUsername(username);
        return Result.success(user);
    }
}