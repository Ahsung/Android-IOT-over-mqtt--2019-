package com.example.hanium;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    //로그인한 아이디와 패스워드
    String loginId, loginPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //shared, 앱 내부에 저장된 ID와 PW
        SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
        loginId = auto.getString("inputId",null);
        loginPwd = auto.getString("inputPwd",null);


        // 로그인시 리스너 객체
        // Response received from the server 서버에서 내용을 받았을때 처리할 내용!
        final Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    //로그인 성공 여부 변수
                    int success = jsonResponse.getInt("success");

                    //로그인 성공했을때
                    if (success == 1) {

                        //로그인 성공시 개인정보를 받아옴
                        String name = jsonResponse.getString("name");
                        String username = jsonResponse.getString("username");
                        String password = jsonResponse.getString("password");
                        int age = jsonResponse.getInt("age");

                        //아이디,패스워드,Age,이름 개인정보 앱 안에 저장,
                        SharedPreferences auto = getSharedPreferences("auto",Activity.MODE_PRIVATE);
                        SharedPreferences.Editor autoLogin = auto.edit();
                        autoLogin.putString("inputId",username);
                        autoLogin.putString("inputPwd",password);
                        autoLogin.putInt("inputAge",age);
                        autoLogin.putString("inputName",name);
                        autoLogin.commit();

                        //로그인 성공시 MainActivity로 로그인정보를 담고 Intent한다.
                        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                        LoginActivity.this.startActivity(intent);
                        finish();
                    }
                    //비밀번호가 틀렸을 때
                    else if(success == 2){
                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                        builder.setMessage("Login Failed 비밀번호가 틀립니다.")
                                .setNegativeButton("Retry", null)
                                .create()
                                .show();
                    }
                    //아이디가 존재하지 않을때
                    else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                        builder.setMessage("Login Failed 아이디가 없습니다.")
                                .setNegativeButton("Retry", null)
                                .create()
                                .show();
                    }

                }
                // 서버 접근 에러
                catch (JSONException e) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setMessage("Login error")
                            .setNegativeButton("Retry", null)
                            .create()
                            .show();
                    e.printStackTrace();
                }
            }
        };


        setContentView(R.layout.activity_login);

        //앱바에 백버튼 추가
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // activity 객체들
        final EditText etUsername = (EditText) findViewById(R.id.etUsername);
        final EditText etPassword = (EditText) findViewById(R.id.etPassword);
        final TextView tvRegisterLink = (TextView) findViewById(R.id.tvRegisterLink);
        final Button bLogin = (Button) findViewById(R.id.bSignIn);

        //회원가입 버튼!
        tvRegisterLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(LoginActivity.this, com.example.hanium.RegisterActivity.class);
                LoginActivity.this.startActivity(registerIntent);
            }
        });


        //로그인 버튼!!
        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = etUsername.getText().toString();
                final String password = etPassword.getText().toString();

                // EditText에 들어온 ID와 PW로 로그인 시도!
                com.example.hanium.LoginRequest loginRequest = new com.example.hanium.LoginRequest(username, password, responseListener);
                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                queue.add(loginRequest);
            }
        });
    }

    //취소버튼 (벡버튼 눌렀을떄)
    @Override
    public void onBackPressed() {
        // Main에서 LoginActivity로 올때 MainActivity를 finish했음으로(로그인 성공시, 지워져야 함으로)
        // 인텐트해서 Main으로 넘어간다.
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        LoginActivity.this.startActivity(intent);
        finish();
    }


    // 매뉴상단의 뒤로가기버튼(홈버튼 눌렀을때 처리)
    @Override
    public  boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
            {
                //main에서 올때 main이 finish되고 login page로 왔음으로
                //Login page에서는 직접 main으로 intent 후 finish !
                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                LoginActivity.this.startActivity(intent);
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
