package com.inc.quackathon.avalonadmin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class MapActivity extends FragmentActivity implements OnMapReadyCallback {


    private GoogleMap mMap;
    public AvalonController avalonController;
    public Map<String, Refugee> refugeeMap;
    public Bitmap imageBitmap;
    public Bitmap resizedBitmap;
    public ArrayList<Marker> markerList;
    public SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        avalonController = new AvalonController();
        avalonController.checkRefugees();
        markerList = new ArrayList<>();
        searchView = (SearchView) findViewById(R.id.searchview);
        searchView.setQueryHint("Search by Last Name");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney, Australia, and move the camera.
        LatLng mtl = new LatLng(45.494511, -73.578505);
        LatLng italy = new LatLng(41.927434, 12.488970);
        LatLng asia = new LatLng(13.764606, 100.508212);

        imageBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.refugee);
        resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, 150, 150, false);

        mMap.addMarker(new MarkerOptions().position(mtl).title("Refugee Centre").icon(BitmapDescriptorFactory.fromBitmap(resizedBitmap)));

        imageBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.unicon);
        resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, 150, 150, false);
        mMap.addMarker(new MarkerOptions().position(italy).title("Refugee Centre").icon(BitmapDescriptorFactory.fromBitmap(resizedBitmap)));
        mMap.addMarker(new MarkerOptions().position(asia).title("Refugee Centre").icon(BitmapDescriptorFactory.fromBitmap(resizedBitmap)));

        String cent = getIntent().getStringExtra("Center");

        if(cent.equals("The Refugee Center MTL"))
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mtl, 14.0f));

        if(cent.equals("United Nations High Commissioner for Refugees - Italy"))
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(italy, 14.0f));

        if(cent.equals("United Nations High Commissioner for Refugees - Thailand"))
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(asia, 14.0f));

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                avalonController.updateChanged(marker.getTitle());
                return false;
            }
        });
        mMap.setOnInfoWindowCloseListener(new GoogleMap.OnInfoWindowCloseListener() {
            @Override
            public void onInfoWindowClose(Marker marker) {

                avalonController.checkRefugees();
                imageBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.refugeeicon);
                resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, 60, 60, false);
                if(marker.getTag() != null)
                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(resizedBitmap));
            }
        });
    }

    public void showLocation(Refugee ref) {

        if (ref.latitude == 0 || ref.longitude == 0)
            return;

        imageBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.refugeeicon);
        resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, 60, 60, false);

        LatLng location = new LatLng(ref.latitude, ref.longitude);

        String address = getAddress(ref.latitude, ref.longitude);

        //avalonController.checkChanged(ref);

        Marker refugee;

        if(ref.changed.equals("false")){
            imageBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.refugeeicon);
            resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, 60, 60, false);
            refugee = mMap.addMarker(new MarkerOptions().position(location).title(ref.firstName).snippet(ref.firstName + "^" + ref.lastName + "^"+ ref.phone + "^"+ ref.email + "^" + ref.marital + "^" + ref.children + "^" + address).icon(BitmapDescriptorFactory.fromBitmap(resizedBitmap)));
        }
        else {
            imageBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.changedrefugee);
            resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, 60, 60, false);
            refugee = mMap.addMarker(new MarkerOptions().position(location).title(ref.firstName).snippet(ref.firstName + "^" + ref.lastName + "^"+ ref.phone + "^"+ ref.email + "^" + ref.marital + "^" + ref.children + "^" + address).icon(BitmapDescriptorFactory.fromBitmap(resizedBitmap)));
        }
        refugee.setTag(ref.lastName);
        markerList.add(refugee);

        RefugeeInfo adapter = new RefugeeInfo(MapActivity.this);
        mMap.setInfoWindowAdapter(adapter);

        //refMarkerList.add(session);
    }

    private String getAddress(double latitude, double longitude) {

        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(this);

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); 
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (addresses == null){
            return "null";
        }

        return addresses.get(0).getAddressLine(0);
    }

    public void onRefresh(View v){


        refugeeMap = avalonController.getRefugeeMap();

        if(refugeeMap != null){
            for (Refugee refugee : refugeeMap.values()) {
                showLocation(refugee);
            }
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                searchQuery(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if(newText.equals(""))
                    showAllMarkers();

                return true;
            }
        });

        checkNotifications();
    }

    public void showAllMarkers(){

        if(markerList.size() > 0) {

            for (Marker marker : markerList) {

                marker.setVisible(true);
            }
        }
    }

    public void searchQuery(String query){

        String markerName;

        for(Marker marker: markerList){

            markerName = (String) marker.getTag();
            assert markerName!= null;

            if(query != null && !(markerName.toLowerCase().equals(query.toLowerCase()))){
                marker.setVisible(false);
            }
            else
                marker.setVisible(true);
        }

    }

    public void checkNotifications(){
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference setRef = database.getReference("notifications");

        setRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot child : dataSnapshot.getChildren()){

                    DataSnapshot nameR = child.child("name");
                    DataSnapshot phoneNumberR = child.child("phone");
                    DataSnapshot messageR = child.child("message");

                    String name = String.valueOf(nameR.getValue());
                    String phoneNumber = String.valueOf(phoneNumberR.getValue());
                    String message = String.valueOf(messageR.getValue());

                    sendAlert(name,phoneNumber,message);
                    child.getRef().removeValue();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void sendAlert(String name, String phone, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Notification From: " + name);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final TextView phoneNumber = new TextView(getApplicationContext());
        phoneNumber.setText("Phone Number: " + phone);
        final TextView messageSent = new TextView(getApplicationContext());
        messageSent.setText(message);
        phoneNumber.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f);
        messageSent.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f);
        messageSent.setGravity(Gravity.CENTER_HORIZONTAL);
        phoneNumber.setGravity(Gravity.CENTER_HORIZONTAL);

        layout.addView(phoneNumber);
        layout.addView(messageSent);

        builder.setView(layout);
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        builder.show();
    }
}
