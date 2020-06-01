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
 * 同步代码块解决方案
 *
 * <p>使用 {@code synchronized} 关键字，让资源采用同步执行，这样可以解决并发请求接口，会排队获取资源</p>
 *
 * <p>
 *     存在的问题：如果是多个服务，还是会存在并发问题
 * </p>
 * @author Erwin Feng
 * @since 2020/6/2
 */
@RestController
@RequestMapping("/api/synchronized")
@Slf4j
public class ApiSynchronizedController {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @PostMapping("/buy")
    public String buy() {
        synchronized (this) {
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

}
