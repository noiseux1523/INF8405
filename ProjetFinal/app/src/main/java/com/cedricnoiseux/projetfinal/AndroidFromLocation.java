package com.cedricnoiseux.projetfinal;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class AndroidFromLocation extends Activity {

    double LATITUDE = 37.42233;
    double LONGITUDE = -122.083;

    final int maxResult =5;
    String addressList[] = new String[maxResult];
    private ArrayAdapter<String> adapter;

    TextView myReturnedAddress, myReturnedLatitude, myReturnedLongitude;

    Geocoder geocoder;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_android_from_location);
        TextView myLatitude = (TextView)findViewById(R.id.mylatitude);
        TextView myLongitude = (TextView)findViewById(R.id.mylongitude);
        TextView myAddress = (TextView)findViewById(R.id.myaddress);
        Spinner myAddressList = (Spinner)findViewById(R.id.addresslist);
        myReturnedAddress = (TextView)findViewById(R.id.returnedaddress);
        myReturnedLatitude = (TextView)findViewById(R.id.returnedlatitude);
        myReturnedLongitude = (TextView)findViewById(R.id.returnedlongitude);

        myLatitude.setText("Latitude: " + String.valueOf(LATITUDE));
        myLongitude.setText("Longitude: " + String.valueOf(LONGITUDE));

        geocoder = new Geocoder(this, Locale.ENGLISH);

        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, maxResult);

            if(addresses != null) {

                for (int j=0; j<maxResult; j++){
                    Address returnedAddress = addresses.get(j);
                    StringBuilder strReturnedAddress = new StringBuilder();
                    for(int i=0; i<returnedAddress.getMaxAddressLineIndex(); i++) {
                        strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                    }
                    addressList[j] = strReturnedAddress.toString();
                }

                adapter = new ArrayAdapter<String>(this,
                        android.R.layout.simple_spinner_item, addressList);
                adapter.setDropDownViewResource(android.R.layout.
                        simple_spinner_dropdown_item);
                myAddressList.setAdapter(adapter);

                myAddressList.setOnItemSelectedListener(myAddressListOnItemSelectedListener);
            }
            else{
                myAddress.setText("No Address returned!");
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            myAddress.setText("Canont get Address!");
        }
    }

    Spinner.OnItemSelectedListener myAddressListOnItemSelectedListener
            = new Spinner.OnItemSelectedListener(){

        @Override
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
                                   long arg3) {
            // TODO Auto-generated method stub

            //myReturnedAddress.setText(addressList[arg2]);
            myReturnedAddress.setText("195 de la providence, la prairie, canada, J5R5Y2");

            try {
                //List<Address> returnedaddresses = geocoder.getFromLocationName(addressList[arg2], 1);
                List<Address> returnedaddresses = geocoder.getFromLocationName("195 de la providence, la prairie, canada, J5R5Y2", 1);

                if(returnedaddresses != null){
                    myReturnedLatitude.setText(String.valueOf(returnedaddresses.get(0).getLatitude()));
                    myReturnedLongitude.setText(String.valueOf(returnedaddresses.get(0).getLongitude()));
                }

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub

        }};
}
