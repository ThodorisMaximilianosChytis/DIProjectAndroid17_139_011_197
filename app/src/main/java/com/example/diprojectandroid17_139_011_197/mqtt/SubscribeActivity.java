package com.example.diprojectandroid17_139_011_197.mqtt;

import android.content.Intent;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.diprojectandroid17_139_011_197.R;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class SubscribeActivity extends AppCompatActivity {
    private Bundle Arguments;
    private MqttClient client;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribe);

        Intent i = getIntent();
        Arguments = i.getExtras();

        ((TextView)findViewById(R.id.textView4)).setText(Arguments.getString("topic"));

        Intent mapintent = new Intent(this, MapsActivity.class);
        startActivity(mapintent);




        try {
            Subscribe();
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }


    private void Subscribe() throws MqttException {

        Toast.makeText(getApplicationContext(), "SUBSCRIBER to " + Arguments.getString("topic"), Toast.LENGTH_LONG).show();

        String clientId= MqttClient.generateClientId();

        client = new MqttClient("tcp://" + Arguments.getString("IP") + ":" + Arguments.getInt("Port"), clientId, new MemoryPersistence());

        client.setCallback( new SimpleMqttCallback() );
        client.connect();

        client.subscribe(Arguments.getString("topic"));



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            client.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }



}