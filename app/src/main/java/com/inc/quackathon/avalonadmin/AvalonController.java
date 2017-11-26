package com.inc.quackathon.avalonadmin;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by antho on 2017-11-25.
 */

public class AvalonController {

    public Map<String, Refugee> refugeeMap;

    public void checkRefugees(){

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        refugeeMap = new HashMap<>();
        DatabaseReference setRef = database.getReference("people");

        setRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()){

                    String firstName = String.valueOf(child.child("First Name").getValue());
                    String lastName = String.valueOf(child.child("Last Name").getValue());
                    String phone = String.valueOf(child.child("Phone Number").getValue());
                    String email = String.valueOf(child.child("Email").getValue());
                    String latitude = String.valueOf(child.child("Latitude").getValue());
                    String longitude = String.valueOf(child.child("Longitude").getValue());
                    String marital = String.valueOf(child.child("Marital Status").getValue());
                    String children = String.valueOf(child.child("Children").getValue());
                    String changed = String.valueOf(child.child("Changed").getValue());

                    if(latitude.equals("null") || longitude.equals("null"))
                        break;

                    Refugee ref = new Refugee(firstName, lastName, phone, email, Double.parseDouble(latitude), Double.parseDouble(longitude), marital, children, changed);



                    refugeeMap.put(firstName+" "+lastName, ref);
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public Map<String, Refugee> getRefugeeMap(){
        return refugeeMap;
    }

    public void updateChanged(final String name) {

        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference setRef = database.getReference("people");

        setRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot child : dataSnapshot.getChildren()){

                    String databaserefugeeName = String.valueOf(child.child("First Name").getValue());

                    if (databaserefugeeName != null && databaserefugeeName.equals(name)){
                        DatabaseReference refugeeRef = database.getReference("people");
                        DatabaseReference refID = refugeeRef.child(child.getKey());
                        DatabaseReference changedRef = refID.child("Changed");
                        changedRef.setValue("false");
                            return;
                        }
                    }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}
