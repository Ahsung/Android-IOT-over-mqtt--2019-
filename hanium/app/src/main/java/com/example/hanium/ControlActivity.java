package com.example.hanium;


import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;
import androidx.appcompat.app.AppCompatActivity;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class ControlActivity extends AppCompatActivity {

    //로그인 ID, password
    String loginId;
    String loginPwd;

    // MQTT Appliances
    MqttAndroid mqttsFan;
    MqttAndroid mqttsLight;
    MqttAndroid mqttsLight2;
    MqttAndroid mqttsHumid;

    //switch icon
    Switch Fan;
    Switch Light;
    Switch Light2;
    Switch Humid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //회원정보 get
        SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
        loginId = auto.getString("inputId",null);
        loginPwd = auto.getString("inputPwd",null);
        String name = auto.getString("inputName",null);
        setContentView(R.layout.activity_control);

        //switch 객체 connect
        Fan = (Switch)findViewById(R.id.fan);
        Light =(Switch)findViewById(R.id.light);
        Light2 =(Switch)findViewById(R.id.light2);
        Humid =(Switch)findViewById(R.id.humid);

        //앱바에 백버튼 추가
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //mqtt connect
        MqttAllSetting();
    }


    //MQTT 객체들 선언과 pub sub연결 및 커넥트,,
    //안드로이드 액티비티가 Restart시 , 연결이 끊겼을때 잘못된 정보를 방지하기위해
    //연결을 다시 처음부터 해줄 수 있게 함수로 모듈화

    public void MqttAllSetting(){
        //subcribe는 기기_m으로 설정,
        // 처음 연결됬을떄 기기 topic으로 2를 보냄,, 기기의 전원여부를 파악하기위해서
        mqttsFan = new MqttAndroid(this,loginId+"_fan",Fan);
        mqttsLight = new MqttAndroid(this,loginId+"_light",Light);
        mqttsLight2 = new MqttAndroid(this,loginId+"_light2",Light2);
        mqttsHumid = new MqttAndroid(this,loginId+"_humid",Humid);


        Fan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isCheck) {
                try {
                    if (isCheck) {
                        mqttsFan.publish(loginId+"_fan", new MqttMessage("1".getBytes()));
                    }
                    else {
                        mqttsFan.publish(loginId+"_fan",new MqttMessage("0".getBytes()));
                    }
                }catch (MqttException e) {
                    e.printStackTrace();
                }
                //Toast.makeText(ControlActivity.this,"체크상태 = "+isCheck+": ",Toast.LENGTH_SHORT).show();
            }
        });
        Light.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isCheck) {
                try {
                    if (isCheck) {
                        mqttsFan.publish(loginId+"_light", new MqttMessage("1".getBytes()));
                    }
                    else {
                        mqttsFan.publish(loginId+"_light",new MqttMessage("0".getBytes()));
                    }
                }catch (MqttException e) {
                    e.printStackTrace();
                }
                //Toast.makeText(ControlActivity.this,"체크 = "+isCheck+": ",Toast.LENGTH_SHORT).show();
            }
        });

        Light2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isCheck) {
                try {
                    if (isCheck) {
                        mqttsFan.publish(loginId+"_light2", new MqttMessage("1".getBytes()));
                    }
                    else {
                        mqttsFan.publish(loginId+"_light2",new MqttMessage("0".getBytes()));
                    }
                }catch (MqttException e) {
                    e.printStackTrace();
                }
                //Toast.makeText(ControlActivity.this,"체크 = "+isCheck+": ",Toast.LENGTH_SHORT).show();
            }
        });

        Humid.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isCheck) {
                try {
                    if (isCheck) {
                        mqttsFan.publish(loginId+"_humid", new MqttMessage("1".getBytes()));
                    }
                    else {
                        mqttsFan.publish(loginId+"_humid",new MqttMessage("0".getBytes()));
                    }
                }catch (MqttException e) {
                    e.printStackTrace();
                }
                //Toast.makeText(ControlActivity.this,"체크상태 = "+isCheck+": ",Toast.LENGTH_SHORT).show();
            }
        });
    }

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

    @Override
    protected void onRestart() {
        super.onRestart();
        MqttAllSetting();
    }
}
