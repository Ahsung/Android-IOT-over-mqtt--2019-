package com.example.hanium;

import android.content.Context;
import android.widget.Switch;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MqttAndroid extends MqttAndroidClient {
    public MqttAndroid(Context context, final String topic, final Switch sw) {
        // 주소 변경
        super(context,IpPath.MQTTIP, MqttClient.generateClientId());
        try{
            //지금 이 객체 클래스에 연결!
            IMqttToken token = this.connect(new MqttConnectOptions());

            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    setBufferOpts(new DisconnectedBufferOptions());

                    // mqtt message arrived
                    try {
                        subscribe(topic+"_m",0);
                        setCallback(new MqttCallback() {
                            @Override
                            public void connectionLost(Throwable cause) {
                                //empty
                            }
                            @Override
                            public void messageArrived(String topic, MqttMessage message) throws Exception {
                                if(message.toString().equals("1") && !sw.isChecked()) sw.setChecked(true);
                                else if(message.toString().equals("0") && sw.isChecked()) sw.setChecked(false);
                            }
                            @Override
                            public void deliveryComplete(IMqttDeliveryToken token) {
                                //empty
                            }
                        });
                        // 연결 확인 메세지 전송.
                        publish(topic,new MqttMessage("2".getBytes()));
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }// mqtt message end;

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {

                }
            });
        }catch (MqttException e){
            e.printStackTrace();
        }
    }
}
