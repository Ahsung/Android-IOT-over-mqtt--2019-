package com.example.hanium;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import android.view.View;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import com.google.android.material.navigation.NavigationView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    // 로그인시 ID와 PW
    String loginId, loginPwd;
    boolean loginSuccess;
    Response.Listener<String> responseListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loginSuccess = false;
        // Nav_drawer 객체들 설정
        //----------------------------------------
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    //-----------------------------------------------------------------------------------------------

        // SharedPrefernces 내용 받아올 객체!!
        SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
        loginId = auto.getString("inputId",null);
        loginPwd = auto.getString("inputPwd",null);

        // Response received from the server 서버에서 내용을 받았을때 처리할 내용!
        responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    int success = jsonResponse.getInt("success");
                    SharedPreferences auto = getSharedPreferences("auto",Activity.MODE_PRIVATE);
                    SharedPreferences.Editor autoLogin = auto.edit();

                    //로그인 성공!
                    if (success == 1) {
                        String name = jsonResponse.getString("name");
                        String username = jsonResponse.getString("username");
                        String password = jsonResponse.getString("password");
                        int age = jsonResponse.getInt("age");
                        loginSuccess = true;
                        //개인정보 저장 or 업데이트,
                        autoLogin.putString("inputId",username);
                        autoLogin.putString("inputPwd",password);
                        autoLogin.putInt("inputAge",age);
                        autoLogin.putString("inputName",name);
                        autoLogin.commit();

                        // 매뉴바에 로그인 정보 추가!
                        // TextiView에 로그인 개인정보들을 띄워줌
                        TextView loginUsername = (TextView)findViewById(R.id.loginUsername);
                        loginUsername.setText(username);
                        TextView loginName = (TextView)findViewById(R.id.loginName);
                        loginName.setText(name);

                        // 로그인 성공시 Nav_drawer 매뉴안의 "로그인"창을 "로그아웃"으로 변경
                        NavigationView navigationView = findViewById(R.id.nav_view);
                        navigationView.getMenu().findItem(R.id.nav_login).setTitle("Logout");

                        //로그인 성공시, Nav_drawer 매뉴의 header사진 변경
                        ImageView loginImage = (ImageView)findViewById(R.id.loginImage);
                        loginImage.setImageResource(R.drawable.beast);
                    }
                    //기존 앱안에 저장되어 있던 정보가 비밀번호 틀림
                    else if(success == 2){
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setMessage("Login Failed 비밀번호가 틀립니다.")
                                .setNegativeButton("Retry", null)
                                .create()
                                .show();

                        //비밀번호 NULL 업데이트
                        autoLogin.putString("inputPwd",null);
                        autoLogin.commit();
                        loginPwd = null;
                    }
                    //아이디 틀림
                    else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setMessage("Login Failed 아이디가 없습니다.")
                                .setNegativeButton("Retry", null)
                                .create()
                                .show();

                        //아이디 비번 null 업데이트
                        autoLogin.putString("inputId",null);
                        autoLogin.putString("inputPwd",null);
                        autoLogin.commit();
                        loginId = null;
                        loginPwd = null;
                    }

                }
                //로그인 오류
                catch (JSONException e) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("Login error")
                            .setNegativeButton("Retry", null)
                            .create()
                            .show();
                    e.printStackTrace();
                }
            }
        };

        // 저장된 ID와 PW가 있다면 로그인 시도
        if(loginPwd != null && loginId != null) {
            com.example.hanium.LoginRequest loginRequest = new com.example.hanium.LoginRequest(loginId, loginPwd, responseListener);
            RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
            queue.add(loginRequest);
            Toast.makeText(MainActivity.this,"로그인 Id \""+loginId+"\"",Toast.LENGTH_SHORT).show();
        }
    }


    // back 버튼 누를때
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        // drawer가 켜져있으면 drawer를 끈다.
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //아니라면 종료
            super.onBackPressed();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    // Nav 매뉴바안의 item클릭 리스너
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        // 로그인 버튼 눌렀을 때
        // intent LoginActivity
        if (id == R.id.nav_login) {

            //이미 로그인 정보가 있을때는 로그아웃 버튼으로 변경!
            if(loginPwd != null && loginId != null && loginSuccess) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("로그아웃 하시겠습니까?")
                        .setPositiveButton("아니오", null)
                        .setNeutralButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
                                SharedPreferences.Editor autoLogout = auto.edit();
                                autoLogout.putString("inputId",null);
                                autoLogout.putString("inputPwd",null);
                                autoLogout.putInt("inputAge",0);
                                autoLogout.putString("inputName",null);
                                autoLogout.commit();

                                //로그아웃 메세지
                                Toast.makeText(MainActivity.this,"\""+loginId+"\" 로그아웃",Toast.LENGTH_SHORT).show();

                                // logout한 mainactivity page로 이동
                                Intent intent = new Intent(MainActivity.this,MainActivity.class);
                                MainActivity.this.startActivity(intent);
                                finish();
                            }
                        })
                        .create()
                        .show();
            }
            // 로그인 정보가 없다면.
            // 로그인 패이지로 이동
            else{
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                MainActivity.this.startActivity(intent);
                //로그인 성공시, 로그인되지 않은 MainActivity는 필요없음으로 지운다.
                finish();
            }
        }
        // intent UserAreaActivity
        else if (id == R.id.nav_control) {
            if(loginPwd != null && loginId != null && loginSuccess) {
                Intent intent = new Intent(MainActivity.this, ControlActivity.class);
                MainActivity.this.startActivity(intent);
            }
            //로그인 아이디가 없으면 Alert
            else{
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("로그인 아이디가 없습니다.")
                        .setNegativeButton("Retry", null)
                        .create()
                        .show();
            }
        } else if (id == R.id.nav_home_status) {
            if(loginPwd != null && loginId != null && loginSuccess) {
                Intent intent = new Intent(MainActivity.this, HomeStatusActivity.class);
                MainActivity.this.startActivity(intent);
            }
            //로그인 아이디가 없으면 Alert
            else{
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("로그인 아이디가 없습니다.")
                        .setNegativeButton("Retry", null)
                        .create()
                        .show();
            }
        }  else if (id == R.id.nav_userinfo) {
            if(loginPwd != null && loginId != null && loginSuccess) {
                Intent intent = new Intent(MainActivity.this, UserAreaActivity.class);
                MainActivity.this.startActivity(intent);
            }
            //로그인 아이디가 없으면 Alert
            else{
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("로그인 아이디가 없습니다.")
                        .setNegativeButton("Retry", null)
                        .create()
                        .show();
            }
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
