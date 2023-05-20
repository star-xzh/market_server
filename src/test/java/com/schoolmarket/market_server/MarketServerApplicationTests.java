package com.schoolmarket.market_server;

import com.schoolmarket.market_server.config.GlobalComment;
import com.schoolmarket.market_server.untils.tools.RedisUtil;
import com.schoolmarket.market_server.untils.tools.twoclass.classApi;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest()
class MarketServerApplicationTests {

    private final classApi classApi;

    private final RedisUtil redisUtil;
    private final GlobalComment comment;

    @Autowired
    MarketServerApplicationTests(com.schoolmarket.market_server.untils.tools.twoclass.classApi classApi, RedisUtil redisUtil, GlobalComment comment) {
        this.classApi = classApi;
        this.redisUtil = redisUtil;
        this.comment = comment;
    }

    @Test
    void contextLoads() throws IOException {
        redisUtil.set("user1","5UiU59M1hZjJhICSo1pE1hWH224",comment.expiration);

    }

}
