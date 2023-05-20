package com.schoolmarket.market_server.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.schoolmarket.market_server.dao.UserMapper;
import com.schoolmarket.market_server.entiy.User;
import com.schoolmarket.market_server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {


    private final UserMapper userMapper;

    @Autowired
    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }


    @Override
    public User findWxUser(String openid) {
        return userMapper.selectOne(new QueryWrapper<User>().eq("openid",openid));
    }

    @Override
    public void InsertUserOpenId(User user) {
        userMapper.insert(user);
    }

    @Override
    public void updateUser(User user) {
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("openid",user.getOpenid());
        userMapper.update(user,updateWrapper);
    }

    @Override
    public String getWxUserUUID(String openid) {
        return userMapper.selectOne(new QueryWrapper<User>().eq("openid",openid)).getUuid();
    }



}
