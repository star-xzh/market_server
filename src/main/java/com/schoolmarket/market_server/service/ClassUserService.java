package com.schoolmarket.market_server.service;

import com.schoolmarket.market_server.entiy.ClassUser;
import org.springframework.stereotype.Service;

@Service
public interface ClassUserService {

    void setOpenid(String uuid,String classOpenid);

    String getOpenid(String uuid);

    void updateUser(ClassUser user);

    ClassUser getClassUSer(String uuid);
}
