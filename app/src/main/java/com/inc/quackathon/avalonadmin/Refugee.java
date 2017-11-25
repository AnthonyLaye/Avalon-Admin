package com.inc.quackathon.avalonadmin;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by antho on 2017-11-25.
 */

class Refugee {


    public String firstName;
    public String lastName;
    public String phone;
    public String email;
    public double latitude;
    public double longitude;

    public Refugee(String firstName, String lastName, String phone, String email, Double latitude, Double longitude){

        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.email = email;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
