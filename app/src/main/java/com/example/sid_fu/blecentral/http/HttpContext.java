package com.example.sid_fu.blecentral.http;

import android.util.Base64;

import com.alibaba.fastjson.JSON;
import com.example.sid_fu.blecentral.db.entity.User;
import com.example.sid_fu.blecentral.http.base.HttpClient;
import com.example.sid_fu.blecentral.http.base.HttpResponseHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/7/1.
 */
public class HttpContext {

    public static final int PAGE_SIZE = 30;
    private static final String HTTP_DOMAIN = "http://api.xiaoan360.com/";
    private static final String register = "api/clientele/register"; // 注册
    private static final String login = "api/clientele/login"; // 登录
    private static final String findpwd = "api/clientele/updatePwd"; // 登录
    private static final String sendCode = "api/sendSms/sendCode";

    private static HttpContext ourInstance = new HttpContext();

    public static HttpContext getInstance() {
        return ourInstance;
    }

    private HttpContext() {

    }
    //*************************************************************//

    public  void login(User param, HttpResponseHandler httpResponseHandler) {

        Map<String, String> params = new HashMap<String, String>();
        params.put("userCode", param.getName());
        params.put("pwd", param.getPassWord());
        params.put("deviceInfo", param.getPhotoNumber());
        String paramStr = JSON.toJSONString(param);
        paramStr = Base64.encodeToString(paramStr.getBytes(), Base64.DEFAULT);

//        HashMap<String, String> rq = new HashMap<>();
//        rq.put("m", login);
//        rq.put("", paramStr);
//        String url = HTTP_DOMAIN + "?" + URLEncodedUtils.format(rq, UTF_8);
        HttpClient.get(HTTP_DOMAIN+login, params, httpResponseHandler);
    }

    public  void register(User param, HttpResponseHandler httpResponseHandler) {

        Map<String, String> params = new HashMap<String, String>();
        params.put("userCode", param.getName());
        params.put("nickName", param.getName());
        params.put("mobile", param.getPhotoNumber());
        params.put("pwd", param.getPassWord());
        String paramStr = JSON.toJSONString(param);
        paramStr = Base64.encodeToString(paramStr.getBytes(), Base64.DEFAULT);

//        HashMap<String, String> rq = new HashMap<>();
//        rq.put("m", register);
//        rq.put("", paramStr);
//        String url = HTTP_DOMAIN + "?" + URLEncodedUtils.format(rq, UTF_8);
        HttpClient.get(HTTP_DOMAIN+register, params, httpResponseHandler);
    }
    public  void findPwd(User param, HttpResponseHandler httpResponseHandler) {

        Map<String, String> params = new HashMap<String, String>();
        params.put("pwd", param.getPassWord());
        params.put("phone", param.getPhotoNumber());
        String paramStr = JSON.toJSONString(param);
        paramStr = Base64.encodeToString(paramStr.getBytes(), Base64.DEFAULT);

//        HashMap<String, String> rq = new HashMap<>();
//        rq.put("m", register);
//        rq.put("", paramStr);
//        String url = HTTP_DOMAIN + "?" + URLEncodedUtils.format(rq, UTF_8);
        HttpClient.get(HTTP_DOMAIN+findpwd, params, httpResponseHandler);
    }
    public  void sendCode(User param, HttpResponseHandler httpResponseHandler) {

        Map<String, String> params = new HashMap<String, String>();
        params.put("code", param.getPassWord());
        params.put("phone", param.getPhotoNumber());
        String paramStr = JSON.toJSONString(param);
        paramStr = Base64.encodeToString(paramStr.getBytes(), Base64.DEFAULT);

//        HashMap<String, String> rq = new HashMap<>();
//        rq.put("m", register);
//        rq.put("", paramStr);
//        String url = HTTP_DOMAIN + "?" + URLEncodedUtils.format(rq, UTF_8);
        HttpClient.get(HTTP_DOMAIN+sendCode, params, httpResponseHandler);
    }
    //*************************************************************//
}
