package com.fengwenyi.spring_cloud.config_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * @author Erwin Feng
 * @since 2020/5/12
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableConfigServer
public class ConfigServerGitApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigServerGitApplication.class, args);
    }

}
