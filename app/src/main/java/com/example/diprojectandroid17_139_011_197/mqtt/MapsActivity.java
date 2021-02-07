package com.example.diprojectandroid17_139_011_197.mqtt;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import java.nio.charset.StandardCharsets;

import static android.graphics.Color.*;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback{

    private static final int FILE_OFFSET = 2;
    private static GoogleMap mMap=null;
    private static final int CSVPERMISSION_REQUEST_CODE=3;

    private LatLng prevRealPosition = null;
    private LatLng lastRealPosition = null;

    private LatLng prevPredPosition = null;
    private LatLng lastPredPosition = null;

    Marker realMarker = null;
    Marker predMarker = null;

    int row =FILE_OFFSET;

    int follow_vehicle = 6;

    private Bundle Arguments;
    private MqttClient clientsub=null;
    MqttClient clientpub=null;

    private CSVReadlines csvlines;
    private Button bs;
    //      T = 1s
    private int mInterval = 1000;
    //      Handler for repeated send
    private Handler mHandler;
    MqttMessage mess;



    //Receives a signal and handles mqtt messages
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getStringExtra("updateMap")!=null && intent.getStringExtra("updateMap").equals("true") ){

//                Toast.makeText(getApplicationContext(), intent.getStringExtra("messageArrived"), Toast.LENGTH_LONG).show();
                String[] predicted = intent.getStringExtra("messageArrived").split("@", 5);
                // 0:predtimestep 1:predlong 2:predlat 3:predRSSI 4:predThroughput

                int predtimestep = (int) Double.parseDouble(predicted[0]);

//                Toast.makeText(getApplicationContext(), predtimestep + "==" + csvlines.getField(predtimestep + FILE_OFFSET -1,0), Toast.LENGTH_LONG).show();

                double predlong = Double.parseDouble(predicted[1]);
                double predlat = Double.parseDouble(predicted[2]);
                double predRSSI = Double.parseDouble(predicted[3]);
                double predThroughput = Double.parseDouble(predicted[4]);

                double realong= Double.parseDouble(csvlines.getField(predtimestep + FILE_OFFSET -1 ,2));
                double reallat=  Double.parseDouble(csvlines.getField(predtimestep + FILE_OFFSET -1 ,3));
                double realRSSI=   Double.parseDouble(csvlines.getField(predtimestep + FILE_OFFSET -1 ,6));
                double realThroughput= Double.parseDouble(csvlines.getField(predtimestep + FILE_OFFSET -1 ,7));


//                Toast.makeText(getApplicationContext(), String.valueOf(DistanceSum) , Toast.LENGTH_LONG).show();


                updateRoutereal(reallat ,realong);
                updateRoutepred( predlat, predlong);
                updateInfoWindow(predRSSI,predThroughput,realRSSI,realThroughput,reallat,realong,predlat,predlong);

            }
        }
    };


    private void updateInfoWindow(double rssipre, double throughputpre, double rssireal, double throughputreal,double reallat,double realong, double predlat, double predlong){
        if (realMarker!=null) {
            mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(MapsActivity.this));

            realMarker.setTitle("Vehicle Info");
            realMarker.setSnippet("Predicted--> Lat:  " + predlat + "\nLong:  " + predlong +"\nRssi:  " + rssipre + "\nThroughput:  " + throughputpre +
                    "\nReal--> Lat:  " + reallat + "\nLong:  " + realong + "\nRSSI:  " + rssireal + "\nThroughput: " + throughputreal);

            realMarker.showInfoWindow();




        }
    }

    public void updateRoutereal(double lat,double lon) {
        LatLng point = new LatLng(lat, lon);
            if (prevRealPosition == null)
                prevRealPosition = point;
            else {
                try {
                    PolylineOptions lineOptions = new PolylineOptions();
                    lineOptions.add(point, prevRealPosition);
                    lineOptions.color(RED);
                    lineOptions.width(2);
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


    public void updateRoutepred(double lat,double lon) {
        LatLng point = new LatLng(lat, lon);
        if (follow_vehicle==6) {
            follow_vehicle = 0;
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 19));
        }
        follow_vehicle++;

        if (prevPredPosition == null) {
            prevPredPosition = point;
        }
        else {
            try {

                PolylineOptions lineOptions = new PolylineOptions();
                lineOptions.add(point, prevPredPosition);
                lineOptions.color(BLUE);
                lineOptions.width(2);
                lineOptions.geodesic(false);
                mMap.addPolyline(lineOptions);
                lastPredPosition = new LatLng(point.latitude, point.longitude);

                if (predMarker != null)
                    predMarker.remove();


                predMarker = mMap.addMarker(new MarkerOptions().position(lastPredPosition)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                        .rotation(180));

                prevPredPosition = point;
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


        //  GET FILE
        Intent intent = new Intent(this, CSVgetFile.class);
        startActivityForResult(intent,CSVPERMISSION_REQUEST_CODE);



        //register receiver
        IntentFilter filter = new IntentFilter("MAP_UPDATE");
        this.registerReceiver(receiver,filter);



        Intent i = getIntent();
        Arguments = i.getExtras();

        ((TextView)findViewById(R.id.textView2)).setText(Arguments.getString("topic"));




        mHandler = new Handler();

    }


    @Override
    protected void onDestroy(){
        super.onDestroy();
        unregisterReceiver(receiver);
        stopRepeatingTask();

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
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);




    }
//---------------------------------------------SUBSCRIBE----------------------------------------------
    private void Subscribe() throws MqttException {

        Toast.makeText(getApplicationContext(), "SUBSCRIBER to " + Arguments.getString("topic"), Toast.LENGTH_LONG).show();

        final String clientId = MqttClient.generateClientId();

        clientsub = new MqttClient("tcp://" + Arguments.getString("IP") + ":" + Arguments.getInt("Port"), clientId, new MemoryPersistence());

        clientsub.setCallback(new MqttCallback() {


            @Override
            public void connectionLost(Throwable cause) {
                Log.d("MqttCallback","Connection to MQTT broker lost!");
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {

//                setMarker(37.9986200,23.80539,"wpalakia");
//                mMap.addMarker(new MarkerOptions().position(new LatLng(0,0)).title("Marker piou").snippet("pioupioupiou"));
                String vectormsg = new String(message.getPayload());
                Log.d("MqttCallback", "message arrived!" + new String(message.getPayload()) );

                if (topic.equals(Arguments.getString("topic" ) + "/e2a") ){
                    Intent intent = new Intent("MAP_UPDATE");
                    intent.putExtra("updateMap", "true");
                    intent.putExtra("messageArrived", vectormsg);
                    sendBroadcast(intent);
                }

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                Log.d("MqttCallback","delivery complete!");
            }
        });

        clientsub.connect();

        clientsub.subscribe(Arguments.getString("topic") + "/e2a");

    }

//    -----------------------------PUBLISH--------------------------------------------------
    private void CancelSend(String message) throws MqttException {

        String stop = "@SendingStops@";
        mess.setPayload(stop.getBytes());
        clientpub.publish(Arguments.getString("topic") + "/a2e",mess);

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

    //Connect with IP nad port to start publishing
    private void Connect(String csvUriString) throws IOException, MqttException {
//        String IP="test.mosquitto.org";
//        String Port="1883";

            Log.d("checkip",Arguments.getString("IP"));
//            Log.d("checktopic",Arguments.getString("topic"));



        String clientId= MqttClient.generateClientId();


        clientpub = new MqttClient("tcp://" + Arguments.getString("IP") + ":" + Arguments.getInt("Port"), clientId, new MemoryPersistence());



        clientpub.setCallback(new SimpleMqttCallback());

        clientpub.connect();


        mess = new MqttMessage();


        csvlines = new CSVReadlines(csvUriString,getApplicationContext());

        row=FILE_OFFSET;

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
                clientsub.publish(Arguments.getString("topic") + "/a2e",mess);
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
    //next line of file
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
        if (clientsub!=null)
            clientsub.disconnect();
        if (clientpub!=null)
            clientpub.disconnect();

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