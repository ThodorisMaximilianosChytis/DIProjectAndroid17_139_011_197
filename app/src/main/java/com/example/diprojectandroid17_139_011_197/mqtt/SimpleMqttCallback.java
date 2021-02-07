package com.example.diprojectandroid17_139_011_197.mqtt;

import android.content.Intent;
import android.util.Log;
import com.example.diprojectandroid17_139_011_197.settings.SettingsActivity;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import static androidx.core.app.ActivityCompat.startActivityForResult;

//Info about MQTT messages
public class SimpleMqttCallback implements MqttCallback {

    @Override
    public void connectionLost(Throwable throwable) {
        Log.d("MqttCallback","Connection to MQTT broker lost!");
    }
    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {


        Log.d("MqttCallback","message arrived!" + new String(mqttMessage.getPayload()));
//        MapsActivity.setMarker();




    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

        Log.d("MqttCallback","delivery complete!");

    }
}





