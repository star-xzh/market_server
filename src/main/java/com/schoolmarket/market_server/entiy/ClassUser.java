package com.schoolmarket.market_server.entiy;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClassUser {
    @TableField("uuid")
    private String uuid;

    @TableField("name")
    private String name;

    @TableField("avatar")
    private String avatar;


    private String SchoolYear;


    private String EdName;

    @TableField("department")
    private String department;

    @TableField("cellphone")
    private String cellphone;

    @TableField("classname")
    private String classname;

    @TableField("marjor_name")
    private String MarjorName;

    private String SchoolName;

    @TableField("class_id")
    private String ClassId;

    @TableField("mail")
    private String mail;

    @TableField("id_no")
    private int IdNo;

    @TableField("school_number")
    private long SchoolNumber;

    @TableField("sex")
    private String sex;

    @TableField("userid")
    private String userid;

    @TableField("class_openid")
    private String ClassOpenid;

    @TableField("favorite")
    private String favorite;

    @TableField("points")
    private float points;

    @TableField("credit")
    private float credit;

    @TableField("honesty")
    private int honesty;
}
