package com.fengwenyi.spring_cloud_lock.controller;

import com.fengwenyi.spring_cloud_lock.constant.RedisKeyConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

/**
 * 示例接口
 * <p>
 *     业务逻辑：请求一次接口，减一次库存，直到库存为0
 * </p>
 * <p>
 *     当前接口的问题：多个线程同时读到当前库存值（stock）有可能是一样
 * </p>
 * @author Erwin Feng
 * @since 2020/6/2
 */
@RestController
@RequestMapping("/api")
@Slf4j
public class ApiController {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @PostMapping("/buy")
    public String buy() {
        int stock = Integer.parseInt(Objects.requireNonNull(stringRedisTemplate.opsForValue().get(RedisKeyConstant.KEY_STOCK)));
        if (stock > 0) {
            log.info("你购买的是第 {} 号库存商品", stock);
            stock -= 1;
            log.info("商品库存：{}", stock);
            stringRedisTemplate.opsForValue().set(RedisKeyConstant.KEY_STOCK, stock + "");
            return "购买成功";
        }
        log.info("-------商品库存不足------------");
        return "购买失败";
    }

}
