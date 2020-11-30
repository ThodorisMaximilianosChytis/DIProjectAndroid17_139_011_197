package com.example.diprojectandroid17_139_011_197.mqtt;

import android.util.Log;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class SimpleMqttCallback implements MqttCallback {

    @Override
    public void connectionLost(Throwable throwable) {
        Log.d("MqttCallback","Connection to MQTT broker lost!");
    }
    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {

        Log.d("MqttCallback","message arrived!" + new String(mqttMessage.getPayload()));

    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

        Log.d("MqttCallback","delivery complete!");

    }
}





/*
terminal 1 one edge server:
mosquitto -p <port>

....

sudo service mosquitto stop
sudo systemctl stop mosquitto.service


IP on edge server: 127.0.0.1

IP in Android App: find out from terminal 2 in EdgeSServer
Port everywhere the same

ifconfig
flag braodcast: inet IP

 */