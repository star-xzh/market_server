package com.schoolmarket.market_server.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.schoolmarket.market_server.entiy.User;
import org.mapstruct.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User>{
}
