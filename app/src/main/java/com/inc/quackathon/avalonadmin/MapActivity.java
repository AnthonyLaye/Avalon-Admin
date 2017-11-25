package com.inc.quackathon.avalonadmin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.sql.Ref;
import java.util.Map;


public class MapActivity extends FragmentActivity implements OnMapReadyCallback {


    private GoogleMap mMap;
    public AvalonController avalonController;
    public Map<String, Refugee> refugeeMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        avalonController = new AvalonController();
        avalonController.checkRefugees();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney, Australia, and move the camera.
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    public void showLocation(Refugee ref) {

        if (ref.latitude == 0 || ref.longitude == 0)
            return;

        LatLng location = new LatLng(ref.latitude, ref.longitude);
        Marker refugee = mMap.addMarker(new MarkerOptions().position(location).title(ref.firstName + ref.lastName).snippet(ref.email));

        //refMarkerList.add(session);
    }

    public void onRefresh(View v){


        refugeeMap = avalonController.getRefugeeMap();

        if(refugeeMap != null){
            for (Refugee refugee : refugeeMap.values()) {
                showLocation(refugee);
            }
        }
    }


}
