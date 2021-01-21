package com.example.diprojectandroid17_139_011_197.mqtt;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.FragmentActivity;
import android.os.Bundle;

import com.example.diprojectandroid17_139_011_197.CSV.CSVReadlines;
import com.example.diprojectandroid17_139_011_197.CSV.CSVgetFile;
import com.example.diprojectandroid17_139_011_197.MainActivity;
import com.example.diprojectandroid17_139_011_197.R;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.io.IOException;

import static android.graphics.Color.*;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback{

    private static GoogleMap mMap=null;
    private static final int CSVPERMISSION_REQUEST_CODE=3;

    private LatLng prevRealPosition = null;
    private LatLng lastRealPosition = null;

    Marker realMarker = null;

    int row =2;
    private Bundle Arguments;
    private MqttClient clientsub;
    private CSVReadlines csvlines;

    private Button bs;
    //      T = 1s
    private int mInterval = 1000;
    //      Handler for repeated send
    private Handler mHandler;

    MqttClient clientpub;
    MqttMessage mess;

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getStringExtra("updateMap")!=null && row<csvlines.getsize()){
//                Log.d("tobad","tobad");
                Toast.makeText(getApplicationContext(), intent.getStringExtra("messageArrived"), Toast.LENGTH_LONG).show();
//                setMarker(Double.parseDouble(csvlines.getField(row,3)), Double.parseDouble(csvlines.getField(row,2) ),"success");
                //updateMap(Double.parseDouble(csvlines.getField(row,3)),Double.parseDouble(csvlines.getField(row,2) ),5);
                //row++;

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
    public void updateMap(double lat,double lon,double rssi) {
        LatLng point = new LatLng(lat, lon);
            if (prevRealPosition == null)
                prevRealPosition = point;
            else {
                try {
                    PolylineOptions lineOptions = new PolylineOptions();
                    lineOptions.add(point, prevRealPosition);
                    lineOptions.color(CYAN);
                    lineOptions.width(5);
                    lineOptions.geodesic(false);
                    mMap.addPolyline(lineOptions);
                    lastRealPosition = new LatLng(point.latitude, point.longitude);
                    if (realMarker != null)
                        realMarker.remove();

                    realMarker = mMap.addMarker(new MarkerOptions().position(lastRealPosition));

                    prevRealPosition = point;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.




        SupportMapFragment mapFragmentreal = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapreal);
        mapFragmentreal.getMapAsync(this);

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

        Intent intent = new Intent(this, CSVgetFile.class);
        startActivityForResult(intent,CSVPERMISSION_REQUEST_CODE);




        IntentFilter filter = new IntentFilter("MAP_UPDATE");
        this.registerReceiver(receiver,filter);



        Intent i = getIntent();
        Arguments = i.getExtras();

        ((TextView)findViewById(R.id.textView2)).setText(Arguments.getString("topic"));

        mHandler = new Handler();

//        SupportMapFragment mapFragmentpred = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mappred);
//        mapFragmentpred.getMapAsync(onMapReadyCallbackpred());
//        mapCount++;
    }


//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        switch(requestCode) {
//            case (CSVPERMISSION_REQUEST_CODE) : {
//                if (resultCode == 1) {
////                    Toast.makeText(getApplicationContext(), Arguments.getString("IP"), Toast.LENGTH_LONG).show();
//
//
//                    try {
//                        Readcsv(data.getStringExtra("csvURI"));
//                    }catch (IOException e){
//                        e.printStackTrace();
////                        Log.d("path",data.getStringExtra("csvURI"));
//                        Toast.makeText(getApplicationContext(),"Could not open file", Toast.LENGTH_LONG).show();
//                        this.finish();
//                    }
//
//                }else{
//                    Toast.makeText(getApplicationContext(),"No file selected", Toast.LENGTH_LONG).show();
//                    this.finish();
//                }
//                break;
//            }
//        }
//    }

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


        LatLng Maxlat_Maxlong = new LatLng(37.9686200,23.77539 );
        LatLng Minlat_Maxlong = new LatLng(37.9668800,23.77539 );
        LatLng Maxlat_Minlong = new LatLng(37.9686200,23.76476 );
        LatLng Minlat_Minlong = new LatLng(37.9668800,23.76476 );

//        LatLngBounds.Builder bounds = new LatLngBounds.Builder();

//        bounds.include(Maxlat_Maxlong);
//        bounds.include(Minlat_Maxlong);
//        bounds.include(Maxlat_Minlong);
//        bounds.include(Minlat_Minlong);
//        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds.build(), 18);
//        mMap.animateCamera(cu);

//        Marker MP1 = mMap.addMarker(new MarkerOptions().position(Maxlat_Maxlong).title("Maxlat_Maxlong").snippet("WAKA"));
//        Marker MP2 =mMap.addMarker(new MarkerOptions().position(Minlat_Maxlong).title("Minlat_Maxlong").snippet("MAKA"));
//        Marker MP3 =mMap.addMarker(new MarkerOptions().position(Maxlat_Minlong).title("Maxlat_Minlong").snippet("PHO"));
//        Marker MP4 =mMap.addMarker(new MarkerOptions().position(Minlat_Minlong).title("Minlat_Minlong").snippet("NE"));

//        MP1.showInfoWindow();
//        MP2.showInfoWindow();
//        MP3.showInfoWindow();
//        MP4.showInfoWindow();


        mMap.addPolyline(new PolylineOptions()
                .color(BLACK)
                .width(2)
                .geodesic(false)
                .add(   Maxlat_Minlong,
                        Maxlat_Maxlong,
                        Minlat_Maxlong,
                        Minlat_Minlong,
                        Maxlat_Minlong));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(Maxlat_Maxlong));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng((Maxlat_Maxlong.latitude + Minlat_Maxlong.latitude)/2 ,(Maxlat_Maxlong.longitude + Minlat_Minlong.longitude)/2 ),15));
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

        clientsub = new MqttClient("tcp://" + Arguments.getString("IP") + ":" + Arguments.getInt("Port"), clientId, new MemoryPersistence());

        clientsub.setCallback(new MqttCallback() {


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

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });

        clientsub.connect();

        clientsub.subscribe(Arguments.getString("topic"));

    }


        @Override
        protected void onDestroy(){
            super.onDestroy();
            unregisterReceiver(receiver);
            stopRepeatingTask();
            try {
                clientsub.disconnect();
                clientpub.disconnect();
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

//    private static final int CSVPERMISSION_REQUEST_CODE=2;
//    private Bundle Arguments;
//    private Button bs;
//    //      T = 1s
//    private int mInterval = 1000;
//    //      Handler for repeated send
//    private Handler mHandler;
//
//    MqttClient client;
//    MqttMessage mess;
//    CSVReadlines csvlines;
//    int row;
//
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_publish);
//
//
//
//        bs = findViewById(R.id.stop);
//        bs.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                try {
//                    CancelSend("You cancelled the trasmission");
//                } catch (MqttException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//
//
//        Intent i = getIntent();
//        Arguments = i.getExtras();
//
////            Log.d("checktopic", Arguments.getString("IP"));
//
////            Log.d("checkip",Arguments.getString("IP"));
////            Log.d("checktopic",Arguments.getString("topic1"));
//
//        ((TextView)findViewById(R.id.textView2)).setText(Arguments.getString("topic"));
//
//        //get file path
//        Intent intent = new Intent(this, CSVgetFile.class);
//        startActivityForResult(intent,CSVPERMISSION_REQUEST_CODE);
//

//
//
//        handler for repeating task
//        mHandler = new Handler();
//
//
//
//    }


//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        stopRepeatingTask();
//    }

    private void CancelSend(String message) throws MqttException {

        stopRepeatingTask();
        Toast.makeText(getApplicationContext(),message, Toast.LENGTH_LONG).show();
        //Disconnect();
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
            clientpub = new MqttClient("tcp://" + Arguments.getString("IP") + ":" + Arguments.getInt("Port"), clientId, new MemoryPersistence());
        } catch (MqttException e) {
            e.printStackTrace();
        }


        clientpub.setCallback(new SimpleMqttCallback());

        clientpub.connect();


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
                clientsub.publish(Arguments.getString("topic"),mess);
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
        clientsub.disconnect();
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