package com.schoolmarket.market_server.controller;


import com.schoolmarket.market_server.untils.annotations.UserLoginToken;
import com.schoolmarket.market_server.untils.tools.QiniuUntil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@RestController
@CrossOrigin
@NotBlank
@NotNull
public class Controller {

    private final QiniuUntil qiniuUntil;

    @Autowired
    public Controller(QiniuUntil qiniuUntil) {
        this.qiniuUntil = qiniuUntil;

    }


    @UserLoginToken
    @RequestMapping("/getUploadToken")
    public String getUploadToken(@RequestBody String filename){

        return qiniuUntil.getUpToken(filename);
    }
}
