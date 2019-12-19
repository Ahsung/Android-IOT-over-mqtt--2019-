package com.example.hanium;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import java.util.HashMap;
import java.util.Map;

public class LoginRequest extends StringRequest {
    //서버에 접속할 php 주소
    private static final String LOGIN_REQUEST_URL = IpPath.WEBIP + "/login.php";
    // string,string 해쉬맵
    private Map<String, String> params;

    //생성자
    public LoginRequest(String username, String password, Response.Listener<String> listener) {
        //post형식으로 전송
        super(Method.POST, LOGIN_REQUEST_URL, listener, null);
        params = new HashMap<>();
        //매개변수 저장
        params.put("username", username);
        params.put("password", password);
    }

    //매개변수
    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
