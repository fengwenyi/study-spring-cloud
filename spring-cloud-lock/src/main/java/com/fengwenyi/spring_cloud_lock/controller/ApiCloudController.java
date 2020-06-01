package com.fengwenyi.spring_cloud_lock.controller;

import com.fengwenyi.spring_cloud_lock.constant.RedisKeyConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 微服务接口方案
 * <p>
 *     依然存在的问题：业务相对较复杂，难度较大，可能存在的问题较多
 * </p>
 * @author Erwin Feng
 * @since 2020/6/2
 */
@RestController
@RequestMapping("/api/cloud")
@Slf4j
public class ApiCloudController {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @PostMapping("/buy")
    public String buy() {
        String PRODUCT_ID = "1";
        String CLIENT_ID = UUID.randomUUID().toString();
        try {
//        stringRedisTemplate.opsForValue().setIfAbsent(PRODUCT_ID, CLIENT_ID);
//        stringRedisTemplate.expire(PRODUCT_ID, 30, TimeUnit.MILLISECONDS); // 解决防止服务挂掉，分布式锁没有被删掉，接口被锁死
            // 但是这两步不是原子操作
            stringRedisTemplate.opsForValue().setIfAbsent(PRODUCT_ID, CLIENT_ID, 30, TimeUnit.MILLISECONDS); // 原子操作，交给Redis

            // 这里有个问题：有可能程序没有执行完，设定的默认失效时间到了，分布式锁已经失效
            // 解决方案：开启一个线程，失效时间的三分之一执行一次

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
        } finally {
            // 在这里删掉锁
            if (CLIENT_ID.equals(stringRedisTemplate.opsForValue().get(PRODUCT_ID))) { // 防止删掉其他线程的锁
                stringRedisTemplate.delete(PRODUCT_ID);
            }
        }
    }

}
