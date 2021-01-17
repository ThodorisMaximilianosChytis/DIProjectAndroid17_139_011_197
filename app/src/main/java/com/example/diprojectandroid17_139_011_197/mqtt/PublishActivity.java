package com.example.diprojectandroid17_139_011_197.mqtt;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.diprojectandroid17_139_011_197.CSV.CSVReadlines;
import com.example.diprojectandroid17_139_011_197.CSV.CSVgetFile;
import com.example.diprojectandroid17_139_011_197.MainActivity;
import com.example.diprojectandroid17_139_011_197.R;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.io.IOException;


public class PublishActivity extends AppCompatActivity {

        private static final int CSVPERMISSION_REQUEST_CODE=2;
        private Bundle Arguments;
        private Button bs;
//      T = 1s
        private int mInterval = 1000;
//      Handler for repeated send
        private Handler mHandler;

        MqttClient client;
        MqttMessage mess;
        CSVReadlines csvlines;
        int row;



        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_publish);



            bs = findViewById(R.id.stop);
            bs.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        CancelSend("You cancelled the trasmission");
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }
            });


            Intent i = getIntent();
            Arguments = i.getExtras();

//            Log.d("checktopic", Arguments.getString("IP"));

//            Log.d("checkip",Arguments.getString("IP"));
//            Log.d("checktopic",Arguments.getString("topic1"));

            ((TextView)findViewById(R.id.textView2)).setText(Arguments.getString("topic"));

            //get file path
            Intent intent = new Intent(this, CSVgetFile.class);
            startActivityForResult(intent,CSVPERMISSION_REQUEST_CODE);

//            try {
//                Connect(Arguments.getString("csvURI"));
//            } catch (MqttException e) {
//                e.printStackTrace();
//                Toast.makeText(getApplicationContext(),"Could not connect to " + "tcp://" + Arguments.getString("IP") + ":" + Arguments.getInt("Port"), Toast.LENGTH_LONG).show();
//                this.finish();
//            }catch (IOException  e){
//                e.printStackTrace();
////                        Log.d("path",data.getStringExtra("csvURI"));
//                Toast.makeText(getApplicationContext(),"Could not read file", Toast.LENGTH_LONG).show();
//                this.finish();
//            }
            
            
            //handler for repeating task
            mHandler = new Handler();



        }


    @Override
    public void onDestroy() {
        super.onDestroy();
        stopRepeatingTask();
    }

    private void CancelSend(String message) throws MqttException {

            stopRepeatingTask();
            Toast.makeText(getApplicationContext(),message, Toast.LENGTH_LONG).show();
            Disconnect();
            this.finish();


    }

    //result from csvgetfile
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (CSVPERMISSION_REQUEST_CODE) : {
                if (resultCode == 1) {
//                    Toast.makeText(getApplicationContext(), Arguments.getString("IP"), Toast.LENGTH_LONG).show();


                    try {
                        Connect(data.getStringExtra("csvURI"));
                    } catch (MqttException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(),"Could not connect to " + "tcp://" + Arguments.getString("IP") + ":" + Arguments.getInt("Port"), Toast.LENGTH_LONG).show();
                        this.finish();
                    }catch (IOException  e){
                        e.printStackTrace();
//                        Log.d("path",data.getStringExtra("csvURI"));
                        Toast.makeText(getApplicationContext(),"Could not open file", Toast.LENGTH_LONG).show();
                        this.finish();
                    }

                }else{
                   Toast.makeText(getApplicationContext(),"No file selected", Toast.LENGTH_LONG).show();
                   this.finish();
                }
                break;
            }
        }
    }


    private void Connect(String csvUriString) throws IOException, MqttException {
//        String IP="test.mosquitto.org";
//        String Port="1883";

//            Log.d("checkip",Arguments.getString("IP"));
//            Log.d("checktopic",Arguments.getString("topic"));

        String clientId= MqttClient.generateClientId();


        try {
            client = new MqttClient("tcp://" + Arguments.getString("IP") + ":" + Arguments.getInt("Port"), clientId, new MemoryPersistence());
        } catch (MqttException e) {
            e.printStackTrace();
        }


        client.setCallback(new SimpleMqttCallback());

        client.connect();


        mess = new MqttMessage();

        csvlines = new CSVReadlines(csvUriString,getApplicationContext());

        row=2;

        startRepeatingTask();


    }




//repeated task
    Runnable mRepeatSend = new Runnable() {
        @Override
        public void run() {


            String [] line = csvlines.getLine(row);
            String sline=line[0];
            for (int i = 1; i < line.length ; i++){
                sline =  sline + "@" + line[i];
            }



            mess.setPayload(sline.getBytes());


            try {
                client.publish(Arguments.getString("topic"),mess);
            } catch (MqttException e) {
                e.printStackTrace();
            }

            try {
                updaterow();
            } catch (MqttException e) {
                e.printStackTrace();
            }


            mHandler.postDelayed(mRepeatSend, mInterval);
        }
    };


    private void updaterow() throws MqttException {
        row++;
        if(isNetworkAvailable()==false){
            startActivity(new Intent(getApplicationContext(), MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        }else if ( row==csvlines.getsize()) {
            CancelSend("EndOFile");
        }else if(Arguments.getInt("t")+1==row ){
            CancelSend("TimeOut");
        }
    }

    void startRepeatingTask() {
        mRepeatSend.run();
    }

    void stopRepeatingTask() {
        mHandler.removeCallbacks(mRepeatSend);
    }

    private void Disconnect() throws MqttException {
        client.disconnect();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()){
            return true;
        }else{
            return false;
        }
    }


}
