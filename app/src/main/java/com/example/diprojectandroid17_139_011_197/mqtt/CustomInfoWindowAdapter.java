package com.example.diprojectandroid17_139_011_197.mqtt;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.example.diprojectandroid17_139_011_197.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import org.w3c.dom.Text;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

   private final View mWindow;
   private Context mContext;

    public CustomInfoWindowAdapter(Context context) {
        this.mContext = context;
        mWindow = LayoutInflater.from(context).inflate(R.layout.custom_info_window,null);
    }

    private void renderWindowText(Marker marker,View view){
        String title  = marker.getTitle();
        TextView tvTitle = (TextView) view.findViewById(R.id.title);

        if(!title.equals("")){
            tvTitle.setText(title);
        }

        String snippet  = marker.getSnippet();
        TextView tvsnippet = (TextView) view.findViewById(R.id.snippet);

        if(!snippet.equals("")){
            tvsnippet.setText(snippet);
        }

    }

    @Override
    public View getInfoWindow(Marker marker) {
        renderWindowText(marker,mWindow);
        return mWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {
        renderWindowText(marker,mWindow);
        return mWindow;
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