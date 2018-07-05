package com.jianxun.eureka.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@EnableEurekaClient
@SpringBootApplication
@RestController
public class ClientApplication {
    @Autowired
    DiscoveryClient discoveryClient;
    public static void main(String[] args) {
        SpringApplication.run(ClientApplication.class, args);
    }
    @RequestMapping("/hi")
    public String hi() {
        return "hi, i am eureka client";
    }

}
