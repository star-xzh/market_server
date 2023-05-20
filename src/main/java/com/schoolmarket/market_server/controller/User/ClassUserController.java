package com.schoolmarket.market_server.controller.User;


import com.alibaba.fastjson.JSONObject;
import com.schoolmarket.market_server.config.GlobalComment;
import com.schoolmarket.market_server.entiy.ClassUser;
import com.schoolmarket.market_server.service.ClassUserService;
import com.schoolmarket.market_server.untils.Re;
import com.schoolmarket.market_server.untils.annotations.UserLoginToken;
import com.schoolmarket.market_server.untils.tools.JwtToken;
import com.schoolmarket.market_server.untils.tools.RedisUtil;
import com.schoolmarket.market_server.untils.tools.twoclass.LoopTaskEx;
import com.schoolmarket.market_server.untils.tools.twoclass.classApi;
import io.swagger.annotations.ApiOperation;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

@NotNull
@NotBlank
@RestController
@RequestMapping("/twoClass")
public class ClassUserController {

    private final RedisUtil redisUtil;
    private final ClassUserService userService;
    private final classApi api;
    private final GlobalComment comment;

    private final LoopTaskEx taskEx;

    @Autowired
    public ClassUserController(RedisUtil redisUtil, ClassUserService userService, classApi api, GlobalComment comment, LoopTaskEx taskEx) {
        this.redisUtil = redisUtil;
        this.userService = userService;
        this.api = api;
        this.comment = comment;
        this.taskEx = taskEx;
    }


    @UserLoginToken
    @ResponseBody
    @ApiOperation("更新第二堂课用户token")
    @RequestMapping("/updateClassUser")
    public JSONObject updateClassUser(@RequestBody String account, HttpServletRequest httpServletRequest) throws IOException, ExecutionException, InterruptedException {
        String token = api.get_user_token(account);
        if (token.equals("")){
            return Re.SetStatus(500,"failed","账号验证失败,请重新输入");
        }
        String uuid = JwtToken.getUserUUIDByJwtToken(httpServletRequest);
        userService.setOpenid(uuid,account);
        redisUtil.set(uuid,token,comment.expiration);
        return Re.SetStatus(200,"success","账号配置成功！");
    }


    @UserLoginToken
    @ResponseBody
    @ApiOperation("获取第二课堂用户openid")
    @RequestMapping("/getToken")
    public JSONObject GetToken(HttpServletRequest httpServletRequest) {
        String uuid = JwtToken.getUserUUIDByJwtToken(httpServletRequest);
        return Re.SetStatus(200,"success",userService.getOpenid(uuid));
    }


    @UserLoginToken
    @ResponseBody
    @ApiOperation("获取第二课堂用户信息")
    @RequestMapping("/getClassUser")
    public JSONObject GetClassUserInfo(HttpServletRequest httpServletRequest) throws IOException, ExecutionException, InterruptedException {
        String uuid = JwtToken.getUserUUIDByJwtToken(httpServletRequest);
        String classOpenid = userService.getOpenid(uuid);
        ClassUser user = api.getUserFile(classOpenid,uuid);
        api.getActivity(user.getUserid(),user.getClassId(),user.getUuid());
        return Re.SetStatus(200,"success",user);
    }


    @UserLoginToken
    @ResponseBody
    @ApiOperation("获取第二课堂报名活动列表")
    @RequestMapping("/getSignActivityList")
    public JSONObject GetActivityList(HttpServletRequest httpServletRequest) throws IOException, ExecutionException, InterruptedException {
        String uuid = JwtToken.getUserUUIDByJwtToken(httpServletRequest);
        ClassUser user = userService.getClassUSer(uuid);
        return Re.SetStatus(200,"success",api.getActivity(user.getUserid(),user.getClassId(),user.getUuid()));
    }

    @UserLoginToken
    @ResponseBody
    @ApiOperation("获取第二课堂所有活动")
    @RequestMapping("/getActivityListAll")
    public JSONObject GetActivityListALl(HttpServletRequest httpServletRequest) throws IOException, ExecutionException, InterruptedException {
        String uuid = JwtToken.getUserUUIDByJwtToken(httpServletRequest);
        ClassUser user = userService.getClassUSer(uuid);
        return Re.SetStatus(200,"success",api.getAllActivity(user.getUserid(),user.getClassId(),user.getUuid()));

    }

    @UserLoginToken
    @ResponseBody
    @ApiOperation("获取第二课堂所有课程")
    @RequestMapping("/getCourseAll")
    public JSONObject GetCourseListALl(HttpServletRequest httpServletRequest) throws IOException, ExecutionException, InterruptedException {
        String uuid = JwtToken.getUserUUIDByJwtToken(httpServletRequest);
        ClassUser user = userService.getClassUSer(uuid);
        return Re.SetStatus(200,"success",api.getAllCourse(user.getUserid(),user.getClassId(),user.getUuid()));

    }


    @UserLoginToken
    @ResponseBody
    @ApiOperation("多线程报名第二课堂活动")
    @RequestMapping("/SignUpActivity")
    public JSONObject SigUpActivity(@RequestBody HashMap<String,String> acMap,HttpServletRequest httpServletRequest) throws IOException, ExecutionException, InterruptedException {
        String uuid = JwtToken.getUserUUIDByJwtToken(httpServletRequest);
        ClassUser user = userService.getClassUSer(uuid);
        String id = acMap.get("acid");
        String response = null;
        long startTime = System.currentTimeMillis();
        long endTime = startTime + 10000; // 10秒后结束循环
        taskEx.startLoop(id,user);
        return Re.SetStatus(200,"success","sss");
    }
}
