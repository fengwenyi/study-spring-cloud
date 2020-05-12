package com.fengwenyi.spring_cloud.config_client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author Erwin Feng
 * @since 2020/5/12
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ConfigClientGitApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigClientGitApplication.class, args);
    }

}
