package com.schoolmarket.market_server.untils.tools.twoclass;

import com.alibaba.fastjson.JSONObject;
import com.schoolmarket.market_server.config.GlobalComment;
import com.schoolmarket.market_server.entiy.ClassUser;
import com.schoolmarket.market_server.service.Impl.ClassUserServiceImpl;
import com.schoolmarket.market_server.untils.tools.RedisUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static com.schoolmarket.market_server.untils.tools.twoclass.request.getSecondTimestamp;

@Component
public class classApi {


    private final ClassUserServiceImpl service;
    private final request request;

    private final RedisUtil redisUtil;

    private final GlobalComment comment;

    @Autowired
    public classApi(ClassUserServiceImpl service, request request, RedisUtil redisUtil, GlobalComment comment) {
        this.service = service;
        this.request = request;
        this.redisUtil = redisUtil;
        this.comment = comment;
    }

    //登录2nd获取用户token
    public String get_user_token(String open_id) throws IOException, ExecutionException, InterruptedException {
        String Authorization = request.login_f2nd_accessToken().get();
        HashMap<String, String> paramsMap = new HashMap<>();
        paramsMap.put("openId", open_id);
        paramsMap.put("sysNo", "2nd_classroom");
        String response = request.send_ClassPost("https://wxms.cezone.cn/basic/user/wechat", paramsMap, Authorization).get();

        JSONObject jsonObject = JSONObject.parseObject(response);
        return jsonObject.get("token").toString();
    }

    //获取用户信息
    public ClassUser get_user(String open_id) throws IOException, ExecutionException, InterruptedException {
        String Authorization = request.login_f2nd_accessToken().get();
        HashMap<String, String> paramsMap = new HashMap<>();
        paramsMap.put("openId", open_id);
        paramsMap.put("sysNo", "2nd_classroom");
        String response = request.send_ClassPost("https://wxms.cezone.cn/basic/user/wechat", paramsMap, Authorization).get();
        JSONObject js = JSONObject.parseObject(JSONObject.parseObject(JSONObject.parseObject(response).toString()).get("studentFile").toString());
        JSONObject jsonObject = JSONObject.parseObject(response);
        ClassUser user = setClassUser(js);
        user.setUserid(jsonObject.getString("id"));
        user.setClassOpenid(open_id);
        service.updateUser(user);
        return user;
    }

    public ClassUser setClassUser(JSONObject js){
        ClassUser user = new ClassUser();
        user.setClassId(js.getString("classId"));
        user.setSchoolYear(js.getString("schoolYearName"));
        user.setClassname(js.getString("className"));
        user.setEdName(js.getString("educationName"));
        user.setName(js.getString("name"));
        user.setCellphone(js.getString("cellphone"));
        user.setAvatar(js.getString("headImg"));
        user.setSchoolNumber((js.getLong("no")));
        user.setMail(js.getString("mail"));
        user.setDepartment(js.getString("departmentName"));
        user.setMarjorName(js.getString("majorName"));
        user.setSchoolName(js.getString("schoolName"));
        user.setIdNo(js.getIntValue("idNo"));
        if (js.getIntValue("sex") == 1){
            user.setSex("男");
        }else {
            user.setSex("女");
        }
        return user;
    }


    public ClassUser getClassUserPoints(ClassUser user) throws IOException, ExecutionException, InterruptedException {
        String url = "https://wxms.cezone.cn/sc_basic/student_score/user/" + user.getUserid();
        String Authorization = getAuthorization(url,user.getUuid());
        String response = request.send_ClassGet(url,Authorization).get();
        JSONObject jsonObject = JSONObject.parseObject(response);
        user.setPoints(jsonObject.getFloat("points"));
        user.setCredit(jsonObject.getFloat("credit"));
        user.setHonesty(jsonObject.getIntValue("honesty"));
        service.updateUser(user);
        return user;
    }

    public ClassUser getClassUserCollect(ClassUser user) throws IOException, ExecutionException, InterruptedException {
        String url = "https://wxms.cezone.cn/activity/activity_collect/count/student_user/" + user.getUuid();
        String Authorization = getAuthorization(url,user.getUuid());
        String response = request.send_ClassGet(url,Authorization).get();
        user.setFavorite(response);
        service.updateUser(user);
        return user;
    }


    @Async("upActivity")
    public CompletableFuture<String> signUptoActivity(String activityId, ClassUser user) throws IOException, ExecutionException, InterruptedException {
        String url = "https://wxms.cezone.cn/activity/sign_up/activity/" + activityId +  "/role/" + comment.studentId + "/user/" + user.getUserid();
        String Authorization = getAuthorization(url,user.getUuid());
        String response = request.activeSignUp_Post(url,Authorization).get();
        return CompletableFuture.completedFuture(response);

    }


    //TODO:获取缓存token
    public String getCacheToken(String uuid) throws IOException, ExecutionException, InterruptedException {
        if (redisUtil.get(uuid) == null){
            String open_id = service.getOpenid(uuid);
            String _temp = get_user_token(open_id);
            redisUtil.set(uuid,_temp,comment.expiration);
        }
        return  redisUtil.get(uuid).toString();
    }

    //TODO:获取页面认证令牌
    public String getAuthorization(String url,String uuid) throws IOException, ExecutionException, InterruptedException {
        String timeMill = String.valueOf(getSecondTimestamp(new Date()));
        String token = getCacheToken(uuid);
        String Au_toke = timeMill + ":" + DigestUtils.md5Hex( comment.api_key + url + timeMill) + ":" + token;
        Au_toke = Base64.getEncoder().encodeToString(Au_toke.getBytes(StandardCharsets.UTF_8));
        return Au_toke;
    }


    //TODO:获取用户信息
    public ClassUser getUserFile(String openid,String uuid) throws IOException, ExecutionException, InterruptedException {
        ClassUser user = get_user(openid);
        user.setUuid(uuid);
        user = getClassUserCollect(user);
        user = getClassUserPoints(user);
        return user;
    }

    //TODO:获取第二课堂活动列表 进行转换处理
    public ArrayList<HashMap<String,Object>> getActivity(String userid,String classId,String uuid) throws IOException, ExecutionException, InterruptedException {
        String url = "https://wxms.cezone.cn/activity/activity/page/for_sign/" + userid + "/class/"+ classId;
        String Authorization = getAuthorization(url,uuid);
        ArrayList<HashMap<String,Object>> activityList = new ArrayList<>();
        //TODO: 两个列表相加 最后通过stream流去重
        activityList.addAll(waitingActivity(activityList,url,Authorization));
        activityList.addAll(registeringActivity(activityList,url,Authorization));
        ArrayList<HashMap<String,Object>> acList = (ArrayList<HashMap<String, Object>>) activityList.stream().distinct().collect(Collectors.toList());
        activityList.clear();
        activityList.addAll(acList);

        return activityList;
    }


    //TODO:待报名的活动 ,"progressState":1
    public ArrayList<HashMap<String,Object>> waitingActivity(ArrayList<HashMap<String,Object>> acList,String url,String Authorization) throws IOException, ExecutionException, InterruptedException {
        String response = request.active_Post(url,"{\"type\":1}",Authorization).get();
        JSONObject jsonObject = JSONObject.parseObject(response);
        jsonObject.getJSONArray("list").forEach((e) ->{
            int count = 0;
            count++;
            HashMap<String,Object> value = new HashMap<>();
            JSONObject _i = JSONObject.parseObject(e.toString());
            getRWActivity(acList, count, value, _i);
        });
        return acList;
    }

    //TODO:设置活动信息
    private void getRWActivity(ArrayList<HashMap<String, Object>> activityList, int count, HashMap<String, Object> value, JSONObject _i) {
        String name = _i.getString("name");
        if (!value.containsValue(_i.getString(name))){
            value.put("id",count);
            value.put("acid",_i.getString("id"));
            value.put("name",changeToLength(name));
            value.put("signUpNum","报名人数:"+_i.get("signUpNum") + "/限制报名人数:" + _i.getString("signUpNumLimit"));
            value.put("supBeginTime",ConversionTime(_i.getString("supBeginTime")));
            value.put("total",ConversionLongtime(_i.getString("supBeginTime")));
            value.put("cover",_i.getString("cover"));
            value.put("bigCateName",_i.getString("bigCateName"));
            value.put("smallCateName",_i.getString("smallCateName"));
            value.put("sponsor",_i.getString("sponsor"));
            activityList.add(value);
        }


    }

    //TODO:获取正在报名的活动
    public ArrayList<HashMap<String,Object>> registeringActivity(ArrayList<HashMap<String,Object>> acList,String url,String Authorization) throws IOException, ExecutionException, InterruptedException {
        String response = request.active_Post(url,"{\"type\":1,\"progressState\":4}",Authorization).get();
        JSONObject jsonObject = JSONObject.parseObject(response);
        jsonObject.getJSONArray("list").forEach((e) ->{
            JSONObject _i = JSONObject.parseObject(e.toString());
            if (!_i.getString("signUpNum").equals(_i.getString("signUpNumLimit"))){
                int count = 0;
                count++;
                HashMap<String,Object> value = new HashMap<>();
                getRWActivity(acList, count, value, _i);
            }
        });

        return acList;
    }


    //TODO:获取所有活动
    public ArrayList<HashMap<String,Object>> getAllActivity(String userid,String classId,String uuid) throws IOException, ExecutionException, InterruptedException {
        String url = "https://wxms.cezone.cn/activity/activity/page/for_sign/" + userid + "/class/"+ classId;
        String Authorization = getAuthorization(url,uuid);
        ArrayList<HashMap<String,Object>> activityList = new ArrayList<>();
        String response = request.active_Post(url,"{\"type\":1}",Authorization).get();
        JSONObject jsonObject = JSONObject.parseObject(response);
        jsonObject.getJSONArray("list").forEach( e  ->{
            HashMap<String,Object> value = new HashMap<>();
            int count = 0;
            count++;
            JSONObject _i = JSONObject.parseObject(e.toString());
            value.put("id",count);
            value.put("acid",_i.getString("id"));
            value.put("name",changeToLength(_i.getString("name")));
            value.put("signUpNum","报名人数:"+_i.get("signUpNum") + "/限制报名人数:" + _i.getString("signUpNumLimit"));
            value.put("supBeginTime",ConversionTime(_i.getString("supBeginTime")));
            value.put("actBeginTime",ConversionTime(_i.getString("actBeginTime")));
            value.put("cover",_i.getString("cover"));
            value.put("bigCateName",_i.getString("bigCateName"));
            value.put("smallCateName",_i.getString("smallCateName"));
            value.put("sponsor",_i.getString("sponsor"));
            activityList.add(value);
        });

        return activityList;
    }

    //TODO:获取所有课程
    public ArrayList<HashMap<String,Object>> getAllCourse(String userid,String classId,String uuid) throws IOException, ExecutionException, InterruptedException {
        String url = "https://wxms.cezone.cn/activity/activity/page/for_sign/" + userid + "/class/"+ classId;
        String Authorization = getAuthorization(url,uuid);
        ArrayList<HashMap<String,Object>> courseList = new ArrayList<>();
        String response = request.active_Post(url,"{\"type\":0}",Authorization).get();
        JSONObject jsonObject = JSONObject.parseObject(response);
        for (Object e : jsonObject.getJSONArray("list")) {
            HashMap<String, Object> value = new HashMap<>();
            int count = 0;
            count++;
            JSONObject _i = JSONObject.parseObject(e.toString());
            value.put("id", count);
            value.put("acid", _i.getString("id"));
            value.put("name", changeToLength(_i.getString("name")));
            value.put("signUpNum", "报名人数:" + _i.get("signUpNum") + "/限制报名人数:" + _i.getString("signUpNumLimit"));
            value.put("supBeginTime", ConversionTime(_i.getString("supBeginTime")));
            value.put("actBeginTime", ConversionTime(_i.getString("actBeginTime")));
            value.put("cover", _i.getString("cover"));
            value.put("bigCateName", _i.getString("bigCateName"));
            value.put("smallCateName", _i.getString("smallCateName"));
            courseList.add(value);
        }

        return courseList;
    }

    //TODO:转换时间到北京时间
    public String ConversionTime(String time){
        // 解析时间字符串
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        LocalDateTime localDateTime = LocalDateTime.parse(time, formatter);

        // 转换为北京时间
        ZoneId sourceZone = ZoneId.of("UTC");
        ZoneId targetZone = ZoneId.of("Asia/Shanghai");
        ZonedDateTime sourceDateTime = ZonedDateTime.of(localDateTime, sourceZone);
        ZonedDateTime targetDateTime = sourceDateTime.withZoneSameInstant(targetZone);

        // 输出日期精确到小时和分钟
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return targetDateTime.format(outputFormatter);
    }

    //TODO:获取剩余时间
    public Long ConversionLongtime(String time){
        // 将字符串解析为 ZonedDateTime 对象
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        ZonedDateTime dateTime = ZonedDateTime.parse(time, formatter);

        // 转换时区为北京时区
        ZoneId beijingZone = ZoneId.of("Asia/Shanghai");
        ZonedDateTime beijingDateTime = dateTime.withZoneSameInstant(beijingZone);

        // 获取当前时间
        ZonedDateTime now = ZonedDateTime.now(beijingZone);

        // 计算时间差并输出相差的秒数
        return ChronoUnit.SECONDS.between(now, beijingDateTime);

    }

    public String changeToLength(String name){
        if (name.length() >= 15){
            return name.substring(0,15)+"...";
        }
        return name;

    }

}
