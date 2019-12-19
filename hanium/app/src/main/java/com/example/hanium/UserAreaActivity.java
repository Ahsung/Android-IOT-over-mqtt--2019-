package com.example.hanium;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.view.MenuItem;
import android.widget.TextView;


public class UserAreaActivity extends AppCompatActivity {
    String loginId,loginPwd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
        loginId = auto.getString("inputId", null);
        loginPwd = auto.getString("inputPwd", null);
        String name = auto.getString("inputName", null);
        String username = loginId;
        int age = auto.getInt("inputAge", 0);

        setContentView(R.layout.activity_user_area);

        // xml 객체
        TextView tvWelcomeMsg = (TextView) findViewById(R.id.tvWelcomeMsg);
        TextView etUsername = (TextView) findViewById(R.id.etUsername);
        TextView etAge = (TextView) findViewById(R.id.etAge);

        // 유저 정보 출력
        String message = name + " welcome to your user area";
        tvWelcomeMsg.setText(message);
        etUsername.setText(username);
        etAge.setText(age + "");


    }


    //앱바의 홈버튼(백버튼) 처리
    @Override
    public  boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
            {
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }


}

