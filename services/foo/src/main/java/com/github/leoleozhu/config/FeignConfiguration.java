package com.github.leoleozhu.config;

import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "com.github.leoleozhu")
public class FeignConfiguration {

}
