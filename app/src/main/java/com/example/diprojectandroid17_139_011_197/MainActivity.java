package com.example.diprojectandroid17_139_011_197;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import com.example.diprojectandroid17_139_011_197.mqtt.PublishActivity;
import com.example.diprojectandroid17_139_011_197.mqtt.SubscribeActivity;
import com.example.diprojectandroid17_139_011_197.network.NetworkChangeReceiver;
import com.example.diprojectandroid17_139_011_197.network.NetworkSettings;
import com.example.diprojectandroid17_139_011_197.settings.SettingsActivity;


public class MainActivity extends AppCompatActivity {

    private Button b1, b2, b3, b4;



    private static final int SETTINGS_REQUEST_CODE=2;

    Bundle Arguments;
    private NetworkChangeReceiver mNetworkReceiver;



    private String updateTopic(){

        EditText topic1 = (EditText) findViewById(R.id.topic1);
        String t1 = topic1.getText().toString();

        Log.d("topic",t1);

        if (t1.isEmpty()){
            t1 = topic1.getHint().toString();
        }
        return t1;
    }

    public void Publish() {

        Intent intent = new Intent(this, PublishActivity.class);

        String t1 = updateTopic();

        intent.putExtra("topic", t1);
        intent.putExtras(Arguments);

        startActivity(intent);

    }

    public void Subscribe() {

        Intent intent = new Intent(this, SubscribeActivity.class);

        String t1 = updateTopic();

        intent.putExtra("topic", t1);

        intent.putExtras(Arguments);

        startActivity(intent);

    }

    public void Settings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivityForResult(intent,SETTINGS_REQUEST_CODE);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (SETTINGS_REQUEST_CODE) : {
                if (resultCode == 1) {
//                     TODO Extract the data returned from the child Activity.
                    Toast.makeText(getApplicationContext(), Arguments.getString("IP"), Toast.LENGTH_LONG).show();

                    setArguments(data.getExtras());

                    Toast.makeText(getApplicationContext(), Arguments.getString("IP"), Toast.LENGTH_LONG).show();

                }
                break;
            }
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

        mNetworkReceiver = new NetworkChangeReceiver();
        registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        //set deafault values
        Arguments = new Bundle();
        Arguments.putString("IP","test.mosquitto.org");
        Arguments.putInt("Port",1883);
        Arguments.putInt("t",-1);

        //WORK WITH BUTTONS

        b1 = findViewById(R.id.pub); //publish
        b2 = findViewById(R.id.sub); //subscribe
        b3 = findViewById(R.id.sett); //settings
        b4 = findViewById(R.id.exit); //exit

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable() == true){
                    Publish();
                }else{
                    startActivity(new Intent(getApplicationContext(), NetworkSettings.class) );
                }
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isNetworkAvailable() == true){
                    Subscribe();
                }else{
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




