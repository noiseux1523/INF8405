package com.cedricnoiseux.tp2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class Menu extends AppCompatActivity {
    private Button Profile;
    private Button Recommend;
    private Button Map;
    private Button Calendar;

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

        Calendar = (Button) findViewById(R.id.Calendar);
        Calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToCalendar();
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

}