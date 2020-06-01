package com.fengwenyi.spring_cloud_lock;

import com.fengwenyi.spring_cloud_lock.constant.RedisKeyConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;

/**
 * @author Erwin Feng
 * @since 2020/6/2
 */
@SpringBootApplication
public class LockApplication {

    public static void main(String[] args) {
        SpringApplication.run(LockApplication.class, args);
    }

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @PostConstruct
    public void init() {
        String stock = stringRedisTemplate.opsForValue().get(RedisKeyConstant.KEY_STOCK);
        if (StringUtils.isEmpty(stock)) {
            stringRedisTemplate.opsForValue().set(RedisKeyConstant.KEY_STOCK, 100 + "");
        }
    }

}
