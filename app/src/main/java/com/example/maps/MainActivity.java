package com.example.maps;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.SSLEngineResult;

public class MainActivity extends AppCompatActivity {

    EditText source,dest;
    TextView text;
    String sType;
    double lat1=0, long1=0, lat2=0, long2=0;
    int flag=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        source= findViewById(R.id.et_source);
        dest=findViewById(R.id.et_destination);
        text=findViewById(R.id.text_view);


        Places.initialize(getApplicationContext(),"AIzaSyDsCajahsc8WImomqKF_S94xwfijkWgZNQ");

        source.setFocusable(false);
        source.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sType="source";
                List<Place.Field>fields= Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG);
                Intent intent= new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields).build(MainActivity.this);
                startActivityForResult(intent,100);

            }
        });

        dest.setFocusable(false);
        dest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sType="destination ";
                List<Place.Field>fields= Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG);
                Intent intent= new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields).build(MainActivity.this);
                startActivityForResult(intent,100);
            }
        });

        text.setText("0.0 kilometers");

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==100 && resultCode==RESULT_OK){
            Place place= Autocomplete.getPlaceFromIntent(data);
            if(sType.equals("source")){
                flag++;
                source.setText(place.getAddress());
                String sSource =String.valueOf(place.getLatLng());
                sSource=sSource.replaceAll("lat/lng: ", "");
                sSource=sSource.replace("(", "");
                sSource=sSource.replace(")", "");
                String[] split =sSource.split(",");
                lat1= Double.parseDouble(split[0]);
                long1= Double.parseDouble(split[1]);

            }else {
                flag++;
                dest.setText(place.getAddress());
                String Sdest =String.valueOf(place.getLatLng());
                Sdest=Sdest.replaceAll("lat/lng: ", "");
                Sdest=Sdest.replace("(", "");
                Sdest=Sdest.replace(")", "");
                String[] split =Sdest.split(",");
                lat2= Double.parseDouble(split[0]);
                long2= Double.parseDouble(split[1]);

            }

            if(flag >=2){
                distance(lat1,long1,lat2,long2);
            }
        }else if(requestCode== AutocompleteActivity.RESULT_ERROR){
            Status status = Autocomplete.getStatusFromIntent(data);
            Toast.makeText(getApplicationContext(),status.getStatusMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void distance(double lat1, double long1, double lat2, double long2) {
        Double longDiff = long1-long2;
        Double distance= Math.sin(deg2rad(lat1))
                *Math.sin(deg2rad(lat2))
                +Math.cos(deg2rad(lat1))
                *Math.cos(deg2rad(lat2))
                *Math.cos(deg2rad(longDiff));

        distance= Math.acos(distance);
        distance=rad2deg(distance);
        distance=distance *60 * 1.1515; //mile
        distance=distance*1.609344; //kilo
        text.setText(String.format(Locale.JAPAN,"%2f Kilometers", distance));
    }

    private Double rad2deg(Double distance) {
        return (distance*180.0/Math.PI);
    }

    private double deg2rad(double lat1) {
        return (lat1*Math.PI/180.0);
    }
}