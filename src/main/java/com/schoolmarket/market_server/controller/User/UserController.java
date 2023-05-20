package com.schoolmarket.market_server.controller.User;


import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import com.alibaba.fastjson.JSONObject;
import com.schoolmarket.market_server.entiy.User;
import com.schoolmarket.market_server.service.Impl.UserServiceImpl;
import com.schoolmarket.market_server.service.UserService;
import com.schoolmarket.market_server.untils.Re;
import com.schoolmarket.market_server.untils.annotations.PassToken;
import com.schoolmarket.market_server.untils.annotations.UserLoginToken;
import com.schoolmarket.market_server.untils.tools.JwtToken;
import com.schoolmarket.market_server.untils.tools.UuidUntil;
import io.swagger.annotations.ApiOperation;
import jakarta.servlet.http.HttpServletRequest;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;


@RestController
@CrossOrigin
@NotBlank
@NotNull
@NotEmpty
@RequestMapping("/user")
public class UserController {


    private final UserService userService;
    private final WxMaService wxMaService;


    @Autowired
    public UserController(WxMaService wxMaService,UserServiceImpl userService) {
        this.wxMaService = wxMaService;
        this.userService = userService;
    }


    @PassToken
    @RequestMapping(value = "/login",method = RequestMethod.GET)
    @ApiOperation("获取微信授权信息")
    @ResponseBody
    public JSONObject login(@RequestParam String code) throws WxErrorException {
        WxMaJscode2SessionResult sessionInfo = wxMaService.getUserService().getSessionInfo(code);
        if (sessionInfo != null){
            if (userService.findWxUser(sessionInfo.getOpenid()) == null){
                UuidUntil until = new UuidUntil();
                String rid = until.createUUid();
                User user = new User();
                user.setUuid(rid);
                user.setRole("user");
                user.setOpenid(sessionInfo.getOpenid());
                user.setDate(new Date());
                userService.InsertUserOpenId(user);
                String authorize = JwtToken.createToken(sessionInfo.getOpenid(),"user",rid);
                return Re.SetStatus(100,"success",authorize);
            }
            String fid = userService.getWxUserUUID(sessionInfo.getOpenid());
            String authorize = JwtToken.createToken(sessionInfo.getOpenid(),"user",fid);
            return Re.SetStatus(200,"success",authorize);
        }

        return Re.SetStatus(500, "failed", "违法登录");

    }



    @UserLoginToken
    @ResponseBody
    @ApiOperation("更新用户信息")
    @RequestMapping(value = "/update")
    public JSONObject updateUser(@RequestBody User user, HttpServletRequest requestHandler){
        String openid = JwtToken.getUserOpenidByJwtToken(requestHandler);
        user.setOpenid(openid);
        userService.updateUser(user);
        return Re.SetStatus(200,"success","用户配置更新成功！");
    }


    @UserLoginToken
    @ResponseBody
    @ApiOperation("更新用户头像")
    @RequestMapping("/updateAvatar")
    public JSONObject setUserAvatarUrl(@RequestBody String url, HttpServletRequest requestHandler){
        String openid = JwtToken.getUserOpenidByJwtToken(requestHandler);
        User user = new User();
        user.setAvatar(url);
        user.setOpenid(openid);
        userService.updateUser(user);
        return Re.SetStatus(200,"success","用户更新头像成功！");

    }


    @UserLoginToken
    @ResponseBody
    @ApiOperation("获取用户信息")
    @RequestMapping("/getUser")
    public JSONObject getUserInfo(HttpServletRequest requestHandler){
        String openid = JwtToken.getUserOpenidByJwtToken(requestHandler);
        User user = userService.findWxUser(openid);
        if (user != null){
            return Re.SetStatus(200,"success",user);
        }
        return Re.SetStatus(500,"failed","没有该用户");

    }







}
