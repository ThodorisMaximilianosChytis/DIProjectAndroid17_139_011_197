package com.example.diprojectandroid17_139_011_197.mqtt;

import com.google.android.gms.maps.GoogleMap;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import static com.example.diprojectandroid17_139_011_197.mqtt.MapsActivity.setMarker;

public class MqttMapCallback implements MqttCallback {

    private GoogleMap mMap;

    void MqttCallback(GoogleMap _mMap){
        mMap = _mMap;
    }

    @Override
    public void connectionLost(Throwable cause) {

    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
//        mMap.setMarker(37.9986200,23.80539,"wpalakia");
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }
}
