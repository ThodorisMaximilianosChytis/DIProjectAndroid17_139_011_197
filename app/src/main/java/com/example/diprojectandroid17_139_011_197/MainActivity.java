package com.example.diprojectandroid17_139_011_197;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import android.content.DialogInterface;

import android.os.Build;
import android.os.Bundle;

import android.view.View;
import android.content.Intent;



import android.widget.Button;
import com.example.diprojectandroid17_139_011_197.CSV.CSVgetFile;
import com.example.diprojectandroid17_139_011_197.mqtt.MapsActivity;
import com.example.diprojectandroid17_139_011_197.mqtt.PublishActivity;
import com.example.diprojectandroid17_139_011_197.mqtt.SubscribeActivity;
import com.example.diprojectandroid17_139_011_197.network.NetworkChangeReceiver;
import com.example.diprojectandroid17_139_011_197.network.NetworkSettings;
import com.example.diprojectandroid17_139_011_197.settings.SettingsActivity;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.io.IOException;


public class MainActivity extends AppCompatActivity {


    private Button b1, b2, b3, b4;



    private static final int SETTINGS_REQUEST_CODE=2;
    private static final int CSVPERMISSION_REQUEST_CODE=3;

    Bundle Arguments;
    private NetworkChangeReceiver mNetworkReceiver;
    Context context;


    private String updateTopic(){

        EditText topic1 = (EditText) findViewById(R.id.topic1);
        String t1 = topic1.getText().toString();

//        Log.d("topic",t1);

        if (t1.isEmpty()){
            t1 = topic1.getHint().toString();
        }
        return t1;
    }


    public void Subscribe() {

        Intent intent = new Intent(this, MapsActivity.class);
//        intent.setData(Uri.parse(Arguments.getString("csvURI")));
//        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        String t1 = updateTopic();

        intent.putExtra("topic", t1);

        intent.putExtras(Arguments);

        startActivity(intent);

    }

    public void Settings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivityForResult(intent,SETTINGS_REQUEST_CODE);

    }

    //result from settings
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (SETTINGS_REQUEST_CODE): {
                if (resultCode == 1) {
//                    Toast.makeText(getApplicationContext(), Arguments.getString("IP"), Toast.LENGTH_LONG).show();

                    setArguments(data.getExtras());

                    Toast.makeText(getApplicationContext(), "SET IP: " + Arguments.getString("IP"), Toast.LENGTH_LONG).show();

                }
                break;
            }
//            case (CSVPERMISSION_REQUEST_CODE): {
//
//                if (resultCode ==1){
//                    Arguments.putString("csvURI",data.getStringExtra("csvURI"));
//                }else{
//                    Toast.makeText(getApplicationContext(),"No file selected", Toast.LENGTH_LONG).show();
//                }
//                break;
//            }
        }
    }


    public void exit(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            finishAndRemoveTask();

                        } else {
                            finishAffinity();
                        }
//                        System.exit(0);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //broadcast network receiver
        mNetworkReceiver = new NetworkChangeReceiver();
        registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        context=getApplicationContext();
        //set deafault values
        Arguments = new Bundle();
        Arguments.putString("IP","test.mosquitto.org");
        Arguments.putInt("Port",1883);
        Arguments.putInt("t",-1);
        Arguments.putString("csvURI","nadas");

        //WORK WITH BUTTONS

        b2 = findViewById(R.id.sub); //subscribe
        b3 = findViewById(R.id.sett); //settings
        b4 = findViewById(R.id.exit); //exit



        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isNetworkAvailable() == true){
                        Subscribe();
                }else{          //prompt to enable internet
                    startActivity(new Intent(getApplicationContext(),NetworkSettings.class) );
                }

            }
        });

        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Settings();
            }
        });

        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exit();
            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()){
            return true;
        }else{
            Toast.makeText(getApplicationContext(), "No Internet connection!", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    public Bundle getArguments() {
        return Arguments;
    }

    public void setArguments(Bundle arguments) {
        Arguments = arguments;
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mNetworkReceiver);

    }
}




