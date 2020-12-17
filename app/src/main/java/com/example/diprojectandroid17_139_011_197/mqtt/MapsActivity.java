package com.example.diprojectandroid17_139_011_197.mqtt;

import androidx.fragment.app.FragmentActivity;
import android.os.Bundle;

import com.example.diprojectandroid17_139_011_197.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback{

    private GoogleMap mMap;
    private int mapCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragmentreal = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapreal);
        mapFragmentreal.getMapAsync(this);
        mapCount=1;

//        SupportMapFragment mapFragmentpred = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mappred);
//        mapFragmentpred.getMapAsync(onMapReadyCallbackpred());
//        mapCount++;
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

        // Add a marker in Sydney and move the camera
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



}