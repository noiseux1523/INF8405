package com.cedricnoiseux.tp2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Menu extends AppCompatActivity {
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 8888;
    private static final String TAG = "Places";
    private Button Profile;
    private Button Recommend;
    private Button Meeting;
    private Button Map;
    private Button Calendar;

    private String Email;
    private String Group;
    private String Place;
    private boolean Organizer;

    protected CharSequence[] receivers = {"Receiver1", "Receiver2", "Receivers3"};
    protected ArrayList<CharSequence> selectedReceivers = new ArrayList<>();

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
                recommendations();
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
        Meeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findPlace(v);
            }
        });

        try {
            getProfile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void findPlace(View view) {
        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN).build(this).putExtra("Place", Place);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
    }

    // A place has been received; use requestCode to track the request.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.i(TAG, "Place: " + place.getName());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    public void getProfile() throws IOException {
        // Open existing file
        try {
            InputStream in = openFileInput(String.valueOf(R.string.PROFILE));
            if (in != null) {
                InputStreamReader tmp = new InputStreamReader(in);
                BufferedReader reader = new BufferedReader(tmp);
                String str;
                int counter = 0;
                // Fill each profile input and assign values to Position and Calendar
                while ((str = reader.readLine()) != null) {
                    switch (counter) {
                        case 1:
                            Group = str;
                            break;
                        case 2:
                            Email = str;
                            break;
                        case 3:
                            if (str.equals("true")) {
                                Organizer = true;
                            } else {
                                Organizer = false;
                            }
                            break;
                        default:
                            break;
                    }
                    counter++;
                }
                in.close();
            }
        }
        catch (java.io.FileNotFoundException e) {
            // File not created
        }
        catch (Throwable t) {
            Toast.makeText(this, "Exception: " + t.toString(), Toast.LENGTH_LONG).show();
        }
    }


    protected void onChangeSelected() {
    }


    public void recommendations() {
        if (!Organizer){
            Toast.makeText(this, "You can't schedule a meeting because you aren't an organizer.", Toast.LENGTH_LONG).show();
        } else {
            PointF position = User.getAveragePosition(Group);
            String positionStr = String.valueOf(position.x) + ", " + String.valueOf(position.y);
            List<ActivityType> activities = User.getCommonInterests(Group);
            for (int i = 0; i < activities.size(); i++) {
                receivers[i] = activities.get(i).toString() + " at " + positionStr;
            }

            boolean[] checkedReceivers = new boolean[receivers.length];
            int count = receivers.length;
            for(int i = 0; i < count; i++)
                checkedReceivers[i] = selectedReceivers.contains(receivers[i]);

            DialogInterface.OnMultiChoiceClickListener receiversDialogListener = new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                    if(isChecked){
                        selectedReceivers.add(receivers[which]);
                    } else {
                        selectedReceivers.remove(receivers[which]);
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select Receivers")
                    .setMultiChoiceItems(receivers, checkedReceivers, receiversDialogListener)
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if (selectedReceivers.size() != 1) {
                                Toast.makeText(getApplicationContext(), "You must choose only one option.", Toast.LENGTH_LONG).show();
                            } else {
                                Place = (String) selectedReceivers.get(0);
                                goToBattery();
                            }
                    }
        });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
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

    /**
     * Method to open Calendar
     */
    public void goToBattery() {
        Intent intent = new Intent(this, BatteryActivity.class);
        startActivity(intent);
    }

}