package com.example.diprojectandroid17_139_011_197.mqtt;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;
import androidx.fragment.app.FragmentActivity;
import android.os.Bundle;

import com.example.diprojectandroid17_139_011_197.CSV.CSVReadlines;
import com.example.diprojectandroid17_139_011_197.CSV.CSVgetFile;
import com.example.diprojectandroid17_139_011_197.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.io.IOException;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback{

    private static GoogleMap mMap=null;
    private static final int CSVPERMISSION_REQUEST_CODE=3;

    float lat = 0;
    float lng = 0;
    int row =2;
    private Bundle Arguments;
    private MqttClient client;
    private CSVReadlines csvlines;

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getStringExtra("updateMap")!=null){
//                Log.d("tobad","tobad");
                Toast.makeText(getApplicationContext(), intent.getStringExtra("messageArrived"), Toast.LENGTH_LONG).show();
                setMarker(Double.parseDouble(csvlines.getField(row,3)), Double.parseDouble(csvlines.getField(row,2) ),"success");
                row++;
            }
        }
    };



    public static void setMarker(double Lat, double Long,String info) {


        if(mMap!=null){

            LatLng P = new LatLng(Lat,Long );

            Marker MP1 = mMap.addMarker(new MarkerOptions().position(P).title("Marker piou").snippet("pioupioupiou"));

            Log.d("tobad","tobad");
        }
        else{

        }


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragmentreal = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapreal);
        mapFragmentreal.getMapAsync(this);

        Intent intent = new Intent(this, CSVgetFile.class);
        startActivityForResult(intent,CSVPERMISSION_REQUEST_CODE);


        IntentFilter filter = new IntentFilter("MAP_UPDATE");
        this.registerReceiver(receiver,filter);



        Intent i = getIntent();
        Arguments = i.getExtras();


//        SupportMapFragment mapFragmentpred = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mappred);
//        mapFragmentpred.getMapAsync(onMapReadyCallbackpred());
//        mapCount++;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (CSVPERMISSION_REQUEST_CODE) : {
                if (resultCode == 1) {
//                    Toast.makeText(getApplicationContext(), Arguments.getString("IP"), Toast.LENGTH_LONG).show();


                    try {
                        Readcsv(data.getStringExtra("csvURI"));
                    }catch (IOException e){
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

    void Readcsv(String csvUriString) throws IOException {
        csvlines = new CSVReadlines(csvUriString,getApplicationContext());

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        try {
            Subscribe();
        } catch (MqttException e) {
            e.printStackTrace();
        }

        LatLng P1 = new LatLng(37.9686200,23.77539 );
        LatLng P2 = new LatLng(37.9668800,23.77539 );
        LatLng P3 = new LatLng(37.9686200,23.76476 );
        LatLng P4 = new LatLng(37.9668800,23.76476 );

        Marker MP1 = mMap.addMarker(new MarkerOptions().position(P1).title("Marker P1").snippet("WAKA"));
        Marker MP2 =mMap.addMarker(new MarkerOptions().position(P2).title("Marker P2").snippet("MAKA"));
        Marker MP3 =mMap.addMarker(new MarkerOptions().position(P3).title("Marker P3").snippet("PHO"));
        Marker MP4 =mMap.addMarker(new MarkerOptions().position(P4).title("Marker P4").snippet("NE"));

//        MP1.showInfoWindow();
//        MP2.showInfoWindow();
//        MP3.showInfoWindow();
//        MP4.showInfoWindow();


//        mMap.moveCamera(CameraUpdateFactory.newLatLng(P1));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng((P1.latitude + P2.latitude)/2 ,(P1.longitude + P3.longitude)/2 ),15));
        mMap.animateCamera(CameraUpdateFactory.zoomIn());
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);




    }

//    public OnMapReadyCallback onMapReadyCallbackreal(){
//        return new OnMapReadyCallback() {
//            @Override
//            public void onMapReady(GoogleMap googleMap) {
//                mMap = googleMap;
//                LatLng vannes = new LatLng(37.9686200,23.77539 );
//                mMap.addMarker(new MarkerOptions().position(vannes).title("Vannes"));
//                mMap.moveCamera(CameraUpdateFactory.newLatLng(vannes));
//            }
//        };
//    }
//
//    public OnMapReadyCallback onMapReadyCallbackpred(){
//        return new OnMapReadyCallback() {
//            @Override
//            public void onMapReady(GoogleMap googleMap) {
//                mMap = googleMap;
//                LatLng bordeaux = new LatLng(44.833328, -0.56667);
//                mMap.addMarker(new MarkerOptions().position(bordeaux).title("Bordeaux"));
//                mMap.moveCamera(CameraUpdateFactory.newLatLng(bordeaux));
//
//            }
//        };
//    }


    private void Subscribe() throws MqttException {

        Toast.makeText(getApplicationContext(), "SUBSCRIBER to " + Arguments.getString("topic"), Toast.LENGTH_LONG).show();

        String clientId = MqttClient.generateClientId();

        client = new MqttClient("tcp://" + Arguments.getString("IP") + ":" + Arguments.getInt("Port"), clientId, new MemoryPersistence());

        client.setCallback(new MqttCallback() {


            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {

//                setMarker(37.9986200,23.80539,"wpalakia");
//                mMap.addMarker(new MarkerOptions().position(new LatLng(0,0)).title("Marker piou").snippet("pioupioupiou"));
                String vectormsg = new String(message.getPayload());
                Log.d("MqttCallback","message arrived!" + new String(message.getPayload()));

                Intent intent = new Intent("MAP_UPDATE");
                intent.putExtra("updateMap","true");
                intent.putExtra("messageArrived",vectormsg);
                sendBroadcast(intent);
                lat++;
                lng++;
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });

        client.connect();

        client.subscribe(Arguments.getString("topic"));

    }


        @Override
        protected void onDestroy(){
            super.onDestroy();
            unregisterReceiver(receiver);
            try {
                client.disconnect();
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }


}