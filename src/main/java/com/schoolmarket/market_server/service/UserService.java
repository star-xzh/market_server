package com.schoolmarket.market_server.service;

import com.schoolmarket.market_server.entiy.User;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    User findWxUser(String openid);

    void InsertUserOpenId(User user);

    void updateUser(User user);

    String getWxUserUUID(String openid);


}
