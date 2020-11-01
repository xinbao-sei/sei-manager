package com.changhong.sei.manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * <strong>实现功能:</strong>
 * <p>REST服务主程序</p>
 *
 * @author 王锦光 wangj
 * @version 1.0.1 2019-12-18 10:41
 */
//@EnableAdminServer
@SpringBootApplication
//@EnableFeignClients(basePackages = {"com.changhong.sei.manager.service.client"})
public class RestApplication {
    public static void main(String[] args) {
        SpringApplication.run(RestApplication.class, args);
    }
}
