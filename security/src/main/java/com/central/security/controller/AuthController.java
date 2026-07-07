package com.central.security.controller;

import com.central.common.result.Result;
import com.central.security.dto.LoginRequest;
import com.central.security.dto.LoginResponse;
import com.central.security.feign.UserFeignClient;
import com.central.security.util.JwtUtil;
import com.central.common.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserFeignClient userFeignClient;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody LoginRequest request) {
        Result<User> userResult = userFeignClient.findByUsername(request.getUsername());
        if (userResult == null || userResult.getData() == null) {
            return Result.error("用户名或密码错误");
        }

        User user = userResult.getData();

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return Result.error("用户名或密码错误");
        }

        if (user.getStatus() != null && user.getStatus() == 0) {
            return Result.error("账号已被禁用");
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getId());

        LoginResponse response = new LoginResponse(
                token,
                user.getUsername(),
                user.getNickname(),
                user.getId()
        );

        return Result.success(response);
    }
}
