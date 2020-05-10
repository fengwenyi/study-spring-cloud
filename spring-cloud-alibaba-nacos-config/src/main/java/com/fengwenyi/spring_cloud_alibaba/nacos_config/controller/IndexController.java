package com.fengwenyi.spring_cloud_alibaba.nacos_config.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * @author Erwin Feng
 * @since 2020/5/11 5:21 上午
 */
@RestController
public class IndexController {

    @Value("${name:World}")
    public String name;

    @GetMapping("/hello")
    public Mono<String> hello() {
        return Mono.just("Hello " + name);
    }

}
