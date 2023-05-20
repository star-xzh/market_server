package com.schoolmarket.market_server.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.schoolmarket.market_server.dao.ClassUserMapper;
import com.schoolmarket.market_server.entiy.ClassUser;
import com.schoolmarket.market_server.service.ClassUserService;
import com.schoolmarket.market_server.untils.tools.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClassUserServiceImpl extends ServiceImpl<ClassUserMapper,ClassUser> implements ClassUserService  {
    private final ClassUserMapper userMapper;


    @Autowired
    public ClassUserServiceImpl(ClassUserMapper userMapper) {
        this.userMapper = userMapper;
    }


    @Override
    public void setOpenid(String uuid,String openid) {
        ClassUser user = userMapper.selectOne(new QueryWrapper<ClassUser>().eq("uuid",uuid));
        if (user == null){
            ClassUser regUser = new ClassUser();
            regUser.setUuid(uuid);
            regUser.setClassOpenid(openid);
            userMapper.insert(regUser);
        }else {
            ClassUser upUser = new ClassUser();
            upUser.setClassOpenid(openid);
            userMapper.update(upUser,new UpdateWrapper<ClassUser>().eq("uuid",uuid));
        }
    }

    @Override
    public String getOpenid(String uuid) {
        ClassUser user =  userMapper.selectOne(new QueryWrapper<ClassUser>().eq("uuid",uuid));
        if (user != null){
            return user.getClassOpenid();
        }
        return null;
    }

    @Override
    public void updateUser(ClassUser user) {
        userMapper.update(user,new UpdateWrapper<ClassUser>().eq("class_openid",user.getClassOpenid()));
    }

    @Override
    public ClassUser getClassUSer(String uuid) {
       return userMapper.selectOne(new QueryWrapper<ClassUser>().eq("uuid",uuid));
    }


}
