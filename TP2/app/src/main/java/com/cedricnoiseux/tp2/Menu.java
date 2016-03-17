package com.cedricnoiseux.tp2;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Menu extends AppCompatActivity {
    public final static String PROFILE = "profile.txt";
    private Button Profile;
    private Button Recommend;
    private Button Map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Profile = (Button) findViewById(R.id.Profile);
        Profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToProfile();
            }
        });

        Recommend = (Button) findViewById(R.id.Recommend);

        Map = (Button) findViewById(R.id.Map);
        Map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMap();
            }
        });
    }

    /**
     * Method to open Profile activity
     */
    public void goToProfile() {
            Intent intent = new Intent(this, Profile.class);
            startActivity(intent);
    }

    /**
     * Method to open Google Maps
     */
    public void goToMap() {
        Intent intent = new Intent(this, Map.class);
        startActivity(intent);
    }


}