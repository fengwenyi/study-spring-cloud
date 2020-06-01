package com.fengwenyi.spring_cloud_lock.controller;

import com.fengwenyi.spring_cloud_lock.config.RedissonManager;
import com.fengwenyi.spring_cloud_lock.constant.RedisKeyConstant;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Redisson解决方案
 *
 * <P>
 *     问题：本来是并发请求，现在改成了同步请求，效率变低了，如何提交效率？
 * </P>
 *
 * <p>
 *     解决方案：将商品的ID分段，比如某个商品有100个，则可以分成：
 * </p>
 *
 * <ul>
 *     <li>p_1_10</li>
 *     <li>p_1_20</li>
 *     <li>p_1_30</li>
 *     <li>p_1_40</li>
 *     <li>p_1_50</li>
 *     <li>p_1_60</li>
 *     <li>p_1_70</li>
 *     <li>p_1_80</li>
 *     <li>p_1_90</li>
 *     <li>p_1_100</li>
 * </ul>
 *
 * <p>
 *     将这几个分段ID作为商品ID进行加锁（思路来源：CurrentHashMap，分段加锁），如果某个分段商品库存取完了，就删掉，下一次，就不会再分配
 * </p>
 *
 * <p>
 *     力度越细，并发能力越高，效率越高
 * </p>
 *
 * @author Erwin Feng
 * @since 2020/6/2
 */
@RestController
@RequestMapping("/api/redisson")
@Slf4j
public class ApiRedissonController {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static Redisson redisson = RedissonManager.getRedisson();

    @PostMapping("/buy")
    public String buy() {
        String PRODUCT_ID = "1";
        try {
            //获取锁对象
            RLock mylock = redisson.getLock(PRODUCT_ID);
            //加锁，并且设置锁过期时间，防止死锁的产生
            mylock.lock(30, TimeUnit.MINUTES);

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
            //获取所对象
            RLock mylock = redisson.getLock(PRODUCT_ID);
            //释放锁（解锁）
            mylock.unlock();
        }
    }

}
