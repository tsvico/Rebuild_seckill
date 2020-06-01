package cn.peoplevip.miaosha;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author tsvico
 * @email tsxygwj@gmail.com
 * @time 2020/4/13 17:57
 */
@SpringBootApplication
@EnableDiscoveryClient
public class miaoshaApplication {
    public static void main(String[] args) {
        SpringApplication.run(miaoshaApplication.class);
    }
}
