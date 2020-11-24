package com.example.diprojectandroid17_139_011_197.settings;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.diprojectandroid17_139_011_197.R;

public class SettingsActivity extends AppCompatActivity {

    private Button ok;

    public void send_data_back() {


        Intent intent = new Intent();



        EditText ip = (EditText) findViewById(R.id.ip);
        EditText port = (EditText) findViewById(R.id.port);
        EditText time = (EditText) findViewById(R.id.time);

        String IP = ip.getText().toString();
        IP = DoifEmpty(IP,ip);

        String Port = port.getText().toString();
        Port = DoifEmpty(Port,port);

        String st;
        st = time.getText().toString();
        st = DoifEmpty(st,time);


        intent.putExtra("IP", IP);
        intent.putExtra("Port", Port);
        intent.putExtra("t", Integer.parseInt(st));

        setResult(1, intent);

        finish();

    }

    private String DoifEmpty(String s, EditText e){
        if ( s.isEmpty() ){
            return e.getHint().toString();
        }
        return s;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ok = findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send_data_back();
            }
        });
    }

}