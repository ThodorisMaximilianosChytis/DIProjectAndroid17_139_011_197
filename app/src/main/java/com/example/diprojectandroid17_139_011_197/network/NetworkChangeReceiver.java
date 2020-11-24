package com.example.diprojectandroid17_139_011_197.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.widget.Toast;

import static android.os.SystemClock.sleep;


public class NetworkChangeReceiver extends BroadcastReceiver {

    

    @Override
    public void onReceive(final Context context, final Intent intent) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if ( activeNetworkInfo != null && activeNetworkInfo.isConnected()) {

            Toast.makeText(context, "Internet connected", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context, "No internet connection! ", Toast.LENGTH_SHORT).show();

        }
    }
}