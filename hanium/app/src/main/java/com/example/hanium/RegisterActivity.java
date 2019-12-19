package com.example.hanium;

import android.app.AlertDialog;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;



public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //앱바에 홈버튼(백버튼) 추가
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final EditText etAge = (EditText) findViewById(R.id.etAge);
        final EditText etName = (EditText) findViewById(R.id.etName);
        final EditText etUsername = (EditText) findViewById(R.id.etUsername);
        final EditText etPassword = (EditText) findViewById(R.id.etPassword);
        final Button bRegister = (Button) findViewById(R.id.bRegister);

        //회원가입 완료 버튼 리스너
        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = etName.getText().toString();
                final String username = etUsername.getText().toString();
                final int age = Integer.parseInt(etAge.getText().toString());
                final String password = etPassword.getText().toString();

                // 서버에 전송하는 리스너
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");

                            // 회원가입 성공!
                            if (success) {
                                Toast.makeText(RegisterActivity.this,"회원가입 성공!",Toast.LENGTH_SHORT).show();
                                finish();
                            }
                            // 아이디가 중복되었다면 success가 false로 반환
                            else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                builder.setMessage("회원가입 실패(아이디 중복)!")
                                        .setNegativeButton("Retry", null)
                                        .create()
                                        .show();
                            }
                        } catch (JSONException e) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                            builder.setMessage("회원가입 에러!")
                                    .setNegativeButton("Retry", null)
                                    .create()
                                    .show();
                            e.printStackTrace();
                        }
                    }
                };

                // RegisterRequest 객체를 사용해서 회원가입.
                com.example.hanium.RegisterRequest registerRequest = new com.example.hanium.RegisterRequest(name, username, age, password, responseListener);
                RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                queue.add(registerRequest);
            }
        });
    }

    // 앱바의 홈버튼 눌렀을때 처리
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
