package com.schoolmarket.market_server.entiy;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField("name")
    private String name;

    @TableField("nickname")
    private String nickname;

    @TableField("age")
    private int age;

    @TableField("male")
    private String male;

    @TableField("department")
    private String department;

    @TableField("openid")
    private String openid;

    @TableField("phone")
    private long phone;

    @TableField("address")
    private String address;

    @TableField("date")
    private Date date;

    @TableField("uuid")
    private String uuid;

    @TableField("avatar")
    private String avatar;

    @TableField("role")
    private String role;


}
