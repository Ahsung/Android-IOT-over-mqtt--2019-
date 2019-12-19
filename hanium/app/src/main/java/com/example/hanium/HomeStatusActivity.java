package com.example.hanium;


import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import androidx.appcompat.app.AppCompatActivity;


public class HomeStatusActivity extends AppCompatActivity {
    String loginId;
    String loginPwd;
    TextView temp;
    TextView humid;
    ProgressBar lumi;
    MqttHome mqttTemp;
    MqttHome mqttHumid;
    MqttHome mqttElec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //로그인 정보 얻기.
        SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
        loginId = auto.getString("inputId",null);
        loginPwd = auto.getString("inputPwd",null);
        String name = auto.getString("inputName",null);
        setContentView(R.layout.activity_home_status);
        TextView tem = findViewById(R.id.temp);

        //textview 설정
        temp = findViewById(R.id.temp);
        humid = findViewById(R.id.humid);
        lumi = findViewById(R.id.elec);

        //mqtt connect
        mqttHomeAllsettind();

       // 앱바에 백버튼 추가
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void mqttHomeAllsettind(){
        mqttTemp = new MqttHome(this,loginId+"_temp",temp);
        mqttHumid = new MqttHome(this,loginId+"_humidy",humid);
        mqttElec = new MqttHome(this,loginId+"_lumi", null);

        try {

            //조명은 custom progress로 view 셋팅
            IMqttToken token = mqttElec.connect(new MqttConnectOptions());
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    mqttElec.setBufferOpts(new DisconnectedBufferOptions());
                    lumi.setProgress(10);
                    try {
                        mqttElec.subscribe(loginId+"_lumi" + "_m", 0);
                        mqttElec.setCallback(new MqttCallback() {
                            @Override
                            public void connectionLost(Throwable cause) {
                            }
                            @Override
                            public void messageArrived(String topic, MqttMessage message) throws Exception {
                                lumi.setProgress(Integer.parseInt(message.toString()));
                            }
                            @Override
                            public void deliveryComplete(IMqttDeliveryToken token) {
                            }
                        });
                        mqttElec.publish(loginId+"_lumi", new MqttMessage("2".getBytes()));
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
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
    protected void onRestart(){
        super.onRestart();
        mqttHomeAllsettind();
    }
}
