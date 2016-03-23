package com.cedricnoiseux.tp2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Menu extends AppCompatActivity {
    private Button Profile;
    private Button Recommend;
    private Button Meeting;
    private Button Map;
    private Button Calendar;

    private String batteryLevelIni_;
    private String batteryLevelFinal_;
    //private TextView batteryLevelIni;
    //private TextView batteryLevelFinal;

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
        Recommend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToBattery();
            }
        });

        Map = (Button) findViewById(R.id.Map);
        Map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMap();
            }
        });

        Calendar = (Button) findViewById(R.id.Calendar);
        Calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToCalendar();
            }
        });

        Meeting = (Button) findViewById(R.id.Meeting);

        //batteryLevelIni = (TextView)findViewById(R.id.batterylevelini);
        //batteryLevelFinal = (TextView)findViewById(R.id.batterylevelfinal);

        this.registerReceiver(this.myBatteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    /**
     * Method to evaluate battery
     */
    private BroadcastReceiver myBatteryReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            if (arg1.getAction().equals(Intent.ACTION_BATTERY_CHANGED)){
                if (batteryLevelIni_ == null && batteryLevelFinal_ == null) {
                    batteryLevelFinal_ = "Level: " + String.valueOf(arg1.getIntExtra("level", 0)) + "%";
                    batteryLevelIni_ = batteryLevelFinal_;
                } else {
                    batteryLevelIni_ = batteryLevelFinal_;
                    batteryLevelFinal_ = "Level: " + String.valueOf(arg1.getIntExtra("level", 0)) + "%";
                }
                //batteryLevelIni.setText(batteryLevelIni_);
                //batteryLevelFinal.setText(batteryLevelFinal_);
            }
        }
    };

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
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }

    /**
     * Method to open Calendar
     */
    public void goToCalendar() {
        Intent intent = new Intent(this, CalendarActivity.class);
        startActivity(intent);
    }

    /**
     * Method to open Calendar
     */
    public void goToBattery() {
        Intent intent = new Intent(this, BatteryActivity.class);
        startActivity(intent);
    }

}