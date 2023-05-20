package com.schoolmarket.market_server.untils;

import com.alibaba.fastjson.JSONObject;

public class Re extends RuntimeException {

    public Re(String msg) {
        super(msg);
    }

    public static JSONObject SetStatus(int code, String status, String Msg){
        JSONObject obj = new JSONObject();

        obj.put("code",code);
        obj.put("status",status);
        obj.put("Msg",Msg);


        return obj;
    }

    public static JSONObject SetStatus(int code, String status, Object Msg){
        JSONObject obj = new JSONObject();

        obj.put("code",code);
        obj.put("status",status);
        obj.put("Msg",Msg);


        return obj;
    }


    public static JSONObject SetStatus(String msg){
        JSONObject obj = new JSONObject();

        obj.put("code",2);
        obj.put("Msg",msg);

        return obj;
    }
}
