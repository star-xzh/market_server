package com.schoolmarket.market_server.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GlobalComment {

    @Value("${code.expiration}")
    public Long expiration;

    @Value("${QiniuConfig.AK}")
    public String ak;

    @Value("${QiniuConfig.SK}")
    public String sk;

    @Value("${QiniuConfig.BK}")
    public String bk;

    @Value("${twoClass.StudentId}")
    public String studentId;

    @Value("${twoClass.ApiKey}")
    public String api_key;

}
