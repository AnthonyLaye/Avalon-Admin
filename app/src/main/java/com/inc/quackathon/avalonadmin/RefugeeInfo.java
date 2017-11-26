package com.inc.quackathon.avalonadmin;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import org.w3c.dom.Text;

/**
 * Created by antho on 2017-11-25.
 */

public class RefugeeInfo implements GoogleMap.InfoWindowAdapter{

    private Activity context;

    public RefugeeInfo(Activity context){

        this.context = context;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {

        View myView = context.getLayoutInflater().inflate(R.layout.refugeewindow, null);

        TextView fName = (TextView) myView.findViewById(R.id.Firstname);
        TextView phoneT = (TextView) myView.findViewById(R.id.phone);
        TextView emailT = (TextView) myView.findViewById(R.id.email);
        TextView maritalT = (TextView) myView.findViewById(R.id.marital);
        TextView childrenT = (TextView) myView.findViewById(R.id.children);
        TextView address = (TextView) myView.findViewById(R.id.address);

        String firstName = marker.getTitle();

        if(firstName.equals("Refugee Centre")){

            fName.setText(firstName);
            phoneT.setText("");
            emailT.setText("");
            maritalT.setText("");
            childrenT.setText("");
            address.setText("");
            return myView;
        }

        String refInformation[] = splitSnippets(marker.getSnippet());

        String lastName = refInformation[1];
        String phone = refInformation[2];
        String email = refInformation[3];
        String marital = refInformation[4];
        String children = refInformation[5];
        String addressName = refInformation[6];


        fName.setText(firstName + " " + lastName);
        phoneT.setText("Phone Number: " + phone);
        emailT.setText("Email: " + email);
        address.setText(addressName);
        maritalT.setText("Marital Status: " + marital);
        childrenT.setText(children + " children");

        return myView;

    }

    public static String[] splitSnippets(String snippet){
        String[] arr=snippet.split("\\^");
        return arr;
    }
}
