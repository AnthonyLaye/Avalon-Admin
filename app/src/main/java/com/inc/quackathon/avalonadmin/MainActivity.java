package com.inc.quackathon.avalonadmin;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public Spinner spin;
    public String itemSelected;
    public String[] centreNames;
    public TextView centerName;
    public TextView centerPasscode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        centerPasscode = (TextView) findViewById(R.id.passcode);

        spin = (Spinner) findViewById(R.id.simpleSpinner);
        spin.setOnItemSelectedListener(this);
        centreNames = new String[]{"The Refugee Center MTL",
                "United Nations High Commissioner for Refugees - Italy",
                "United Nations High Commissioner for Refugees - Thailand"};

        ArrayAdapter myArray = new ArrayAdapter(this,android.R.layout.simple_spinner_item,centreNames);
        myArray.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(myArray);
    }

    public void onLogin(View v){

        String centerpass = String.valueOf(centerPasscode.getText());
        String cent = itemSelected;

        if(centerpass.equals("BLOCHACKS2017")) {

            Intent map = new Intent(MainActivity.this, MapActivity.class);
            map.putExtra("Center", cent);
            MainActivity.this.startActivity(map);
        }
        else {
            Context context = getApplicationContext();
            CharSequence text = "Wrong Passcode!";
            int duration = Toast.LENGTH_LONG;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        itemSelected = centreNames[position];
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
