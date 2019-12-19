package com.example.hanium;

import android.content.Context;
import android.widget.TextView;

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

public class MqttHome extends MqttAndroidClient {
    public MqttHome(Context context, final String topic, final TextView textView) {
        // 주소 변경
        super(context,IpPath.MQTTIP, MqttClient.generateClientId());
        if(textView != null) {
            try {
                //지금 이 객체 클래스에 연결!
                IMqttToken token = this.connect(new MqttConnectOptions());

                token.setActionCallback(new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        setBufferOpts(new DisconnectedBufferOptions());
                        try {
                            textView.setText("연결됨");
                            subscribe(topic + "_m", 0);
                            setCallback(new MqttCallback() {
                                @Override
                                public void connectionLost(Throwable cause) {
                                }

                                @Override
                                public void messageArrived(String topic, MqttMessage message) throws Exception {
                                    textView.setText(message.toString());
                                }

                                @Override
                                public void deliveryComplete(IMqttDeliveryToken token) {

                                }
                            });

                            publish(topic, new MqttMessage("2".getBytes()));

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


    }

}
