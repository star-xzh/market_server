package com.schoolmarket.market_server;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@MapperScan("com.schoolmarket.market_server.dao")
@SpringBootApplication()
@EnableScheduling
public class MarketServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MarketServerApplication.class, args);
    }

}
