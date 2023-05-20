package com.schoolmarket.market_server.untils.tools.twoclass;

import com.schoolmarket.market_server.config.GlobalComment;
import com.schoolmarket.market_server.untils.annotations.ExceptionCatch;
import okhttp3.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Component
public class request {


    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");
    private final OkHttpClient client;

    private final GlobalComment comment;

    @Autowired
    public request(OkHttpClient client, GlobalComment comment) {
        this.client = client;
        this.comment = comment;
    }


    public static int getSecondTimestamp(Date date) {
        if (null == date) {
            return 0;
        }
        String timestamp = String.valueOf(date.getTime());
        int length = timestamp.length();
        if (length > 3) {
            return Integer.parseInt(timestamp.substring(0, length - 3));
        } else {
            return 0;
        }

    }


    //post请求第二课堂
    @Async
    @ExceptionCatch
    public CompletableFuture<String> send_ClassPost(String url, HashMap<String, String> paramsMap, String Authorization) throws IOException {
        client.newBuilder().callTimeout(20, TimeUnit.SECONDS);
        client.newBuilder().connectTimeout(60, TimeUnit.SECONDS);
        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        Set<String> keySet = paramsMap.keySet();
        for (String key : keySet) {
            String value = paramsMap.get(key);
            formBodyBuilder.add(key, value);
        }
        FormBody formBody = formBodyBuilder.build();
        Request request = new Request.Builder()
                .header("Host", "wxms.cezone.cn")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.116 Safari/537.36 QBCore/4.0.1301.400 QQBrowser/9.0.2524.400 Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2875.116 Safari/537.36 NetType/WIFI MicroMessenger/7.0.5 WindowsWechat")
                .header("Authorization", Authorization)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .url(url)
                .post(formBody)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return CompletableFuture.completedFuture(Objects.requireNonNull(response.body()).string());
        }
    }


    //获取第二课堂活动
    @Async
    public CompletableFuture<String> active_Post(String url, String json, String Authorization) throws IOException {
        client.newBuilder().callTimeout(20, TimeUnit.SECONDS);
        client.newBuilder().connectTimeout(60, TimeUnit.SECONDS);
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .header("Host", "wxms.cezone.cn")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.116 Safari/537.36 QBCore/4.0.1301.400 QQBrowser/9.0.2524.400 Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2875.116 Safari/537.36 NetType/WIFI MicroMessenger/7.0.5 WindowsWechat")
                .header("Authorization", Authorization)
                .header("Content-Type", "application/json;charset=UTF-8")
                .url(url)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return CompletableFuture.completedFuture(Objects.requireNonNull(response.body()).string());
        }
    }


    @Async
    public CompletableFuture<String> activeSignUp_Post(String url, String Authorization) throws IOException {
        client.newBuilder().callTimeout(20, TimeUnit.SECONDS);
        client.newBuilder().connectTimeout(60, TimeUnit.SECONDS);
        RequestBody body = RequestBody.create("", JSON);
        Request request = new Request.Builder()
                .header("Host", "wxms.cezone.cn")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.116 Safari/537.36 QBCore/4.0.1301.400 QQBrowser/9.0.2524.400 Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2875.116 Safari/537.36 NetType/WIFI MicroMessenger/7.0.5 WindowsWechat")
                .header("Authorization", Authorization)
                .url(url)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return CompletableFuture.completedFuture(Objects.requireNonNull(response.body()).string());
        }
    }


    //TODO:get请求第二课堂
    @Async
    public CompletableFuture<String> send_ClassGet(String url, String Authorization) throws IOException {
        client.newBuilder().callTimeout(20, TimeUnit.SECONDS);
        client.newBuilder().connectTimeout(60, TimeUnit.SECONDS);
        Request request = new Request.Builder()
                .header("Host", "wxms.cezone.cn")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.116 Safari/537.36 QBCore/4.0.1301.400 QQBrowser/9.0.2524.400 Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2875.116 Safari/537.36 NetType/WIFI MicroMessenger/7.0.5 WindowsWechat")
                .header("Authorization", Authorization)
                .header("Accept", "application/json, text/plain, */*")
                .url(url)
                .get()
                .build();
        try (Response response = client.newCall(request).execute()) {
            return CompletableFuture.completedFuture(Objects.requireNonNull(response.body()).string());
        }
    }



    //拼接第二课堂第一次登录token
    @Async
    public CompletableFuture<String> login_f2nd_accessToken() {
        String url = "https://wxms.cezone.cn/basic/user/wechat";
        String timeMill = String.valueOf(getSecondTimestamp(new Date()));
        String aui = timeMill + ":" + DigestUtils.md5Hex(comment.api_key + url + timeMill) + ":";
        aui = Base64.getEncoder().encodeToString(aui.getBytes(StandardCharsets.UTF_8));
        return CompletableFuture.completedFuture(aui);
    }










}
