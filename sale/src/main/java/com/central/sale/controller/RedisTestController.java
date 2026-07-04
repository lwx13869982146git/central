package com.central.sale.controller;

import com.central.common.result.Result;
import com.central.common.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * Redis 测试控制器
 */
@RestController
@RequestMapping("/api/redis")
@RequiredArgsConstructor
public class RedisTestController {

    private final RedisUtil redisUtil;

    /**
     * 测试设置缓存
     */
    @PostMapping("/set")
    public Result<String> setCache(@RequestParam String key, @RequestParam String value) {
        boolean success = redisUtil.set(key, value);
        return success ? Result.success("设置成功") : Result.error("设置失败");
    }

    /**
     * 测试获取缓存
     */
    @GetMapping("/get")
    public Result<Object> getCache(@RequestParam String key) {
        Object value = redisUtil.get(key);
        return Result.success(value);
    }

    /**
     * 测试删除缓存
     */
    @DeleteMapping("/del")
    public Result<String> deleteCache(@RequestParam String key) {
        redisUtil.del(key);
        return Result.success("删除成功");
    }

    /**
     * 测试设置带过期时间的缓存
     */
    @PostMapping("/setWithExpire")
    public Result<String> setCacheWithExpire(@RequestParam String key, 
                                             @RequestParam String value,
                                             @RequestParam long expireSeconds) {
        boolean success = redisUtil.set(key, value, expireSeconds);
        return success ? Result.success("设置成功") : Result.error("设置失败");
    }

    /**
     * 测试判断 key 是否存在
     */
    @GetMapping("/exists")
    public Result<Boolean> exists(@RequestParam String key) {
        boolean exists = redisUtil.hasKey(key);
        return Result.success(exists);
    }
}
