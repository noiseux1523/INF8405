package com.cedricnoiseux.projetfinal;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationListener;

import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

// Quand on pese sur le bouton de compas, l`image apparait et les events disparaissent
// avec removeAllViews du layout ou de quoi. On donne l'option de quand on clique sur le compas
// le compas disparait et les events re-apparaissent avec getAllEvents. À voir, on veut pointer vers les events ou
// juste le nord?

public class ActivityEventsList extends AppCompatActivity implements
        SensorEventListener,
        android.location.LocationListener{

    private TextView mMenu;
    private TextView mDismiss;
    private ScrollView mScroll;
    private LinearLayout mList;
    private RelativeLayout mMain;
    private ImageView mPointer;
    private Boolean compassOn = false;

    private SensorManager mSensorManager;
    private Sensor mCompass;
    private float mCurrentDegree = 0f;
    private Event selectedEvent;
    private final User user = new User("", 0, 0);

    protected LocationManager locationManager;
    protected LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eventslist);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        String username = extras.getString("user");
        user.email = username;

        mMenu = (TextView) findViewById(R.id.menu);
        mMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMenu();
            }
        });
        mDismiss = (TextView) findViewById(R.id.dismiss);
        mDismiss.setVisibility(View.GONE);
        mDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    getEvents(ActivityEventsList.this, user);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mDismiss.setVisibility(View.GONE);
                mPointer.setImageResource(0);
                mPointer.setVisibility(View.GONE);
                compassOn = false;
            }
        });
        mScroll = (ScrollView) findViewById(R.id.scroll);
        mList = (LinearLayout) findViewById(R.id.list);
        mMain = (RelativeLayout) findViewById(R.id.main);
        mPointer = (ImageView) findViewById(R.id.imageDisplay);
        mPointer.setImageResource(R.drawable.pointer);
        mPointer.setVisibility(View.GONE);

        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mCompass = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        // Search and show all events
        try {
            getEvents(this, user);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void goToMenu() {
        Intent intent = new Intent(this, ActivityMenu.class);
        startActivity(intent);
    }

    public void getEvents(Context context, final User user) throws Exception {
        // Initialize linked list for events
        LinkedList<Event> e1 = SqlUtility.getAllEvents();
        LinkedList<Event> e2 = SqlUtility.getParticipatingEvent(user);

        for (final Event event : e1) {
            // Initialize textviews
            final TextView mParticipation = new TextView(context);
            final TextView mOutput = new TextView(context);

            // Set participation or not
            mParticipation.setTextColor(Color.RED);
            mParticipation.setText("Not Attending - Click to interact");
            int i = 0;
            while (i < e2.size()) {
                if (e2.get(i).id == event.id) {
                    mParticipation.setTextColor(Color.GREEN);
                    mParticipation.setText("Attending - Click to interact");
                }
                i++;
            }
            mParticipation.setTypeface(null, Typeface.BOLD);
            mParticipation.setGravity(Gravity.CENTER);
            mParticipation.setTextSize(18);
            mParticipation.setPadding(0, 20, 0, 10);

            // Set event information
            SimpleDateFormat format = new SimpleDateFormat("MMM dd, yyyy  hh:mm a");
            String date = format.format(event.date);
            String info = event.name.toUpperCase() + "\n"
                    + " HOSTED BY " + event.host + "\n"
                    + " ON " + date + "\n"
                    + " AT " + event.locationName;
            mOutput.setText(info);
            mOutput.setLineSpacing(0.0f, 1.15f);
            mOutput.setTextColor(Color.WHITE);
            mOutput.setTypeface(null, Typeface.BOLD);
            mOutput.setGravity(Gravity.CENTER);
            mOutput.setTextSize(18);
            mOutput.setPadding(0, 0, 0, 10);
            if (mParticipation.getText() == "Not Attending - Click to interact") {
                mOutput.setBackgroundResource(R.drawable.back_red);
            } else {
                mOutput.setBackgroundResource(R.drawable.back_green);
            }


            // Set onClickListener
            mOutput.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ActivityEventsList.this);
                    builder.setTitle("Choose an option");
                    builder.setItems(new CharSequence[]{"Join Event", "Leave Event", "Show Direction"},
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // The 'which' argument contains the index position
                                    // of the selected item
                                    switch (which) {
                                        case 0:
                                            if (mParticipation.getCurrentTextColor() == Color.RED) {
                                                SqlUtility.addParticipation(user, event);
                                                mParticipation.setTextColor(Color.GREEN);
                                                mParticipation.setText("Attending - Click to interact");
                                                mOutput.setBackgroundResource(R.drawable.back_green);
                                            } else {
                                                Toast.makeText(ActivityEventsList.this,
                                                        "You are already attending this event", Toast.LENGTH_LONG).show();
                                            }
                                            break;
                                        case 1:
                                            if (mParticipation.getCurrentTextColor() == Color.GREEN) {
                                                SqlUtility.removeParticipation(user, event);
                                                mParticipation.setTextColor(Color.RED);
                                                mParticipation.setText("Not Attending - Click to interact");
                                                mOutput.setBackgroundResource(R.drawable.back_red);
                                            } else {
                                                Toast.makeText(ActivityEventsList.this,
                                                        "You are already not attending this event", Toast.LENGTH_LONG).show();
                                            }
                                            break;
                                        case 2:
                                            if (user.lastY == 0 && user.lastX == 0) {
                                                Toast.makeText(ActivityEventsList.this,
                                                        "Wait until your device is located", Toast.LENGTH_LONG).show();
                                            } else {
                                                mList.removeAllViews();
                                                mDismiss.setVisibility(View.VISIBLE);
                                                mPointer.setImageResource(R.drawable.pointer);
                                                mPointer.setVisibility(View.VISIBLE);
                                                selectedEvent = event;
                                                compassOn = true;

                                            }
                                            break;
                                    }
                                }
                            }

                    );
                    builder.create().show();
                }
            });

            // Add textviews
            mList.addView(mParticipation);
            mList.addView(mOutput);
        }
    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mCompass, SensorManager.SENSOR_DELAY_GAME);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this, mCompass);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (compassOn) {
            // get the angle around the z-axis rotated
            float degree = Math.round(event.values[0]);

            double eventX = selectedEvent.locY;
            double eventY = selectedEvent.locX;
            double userX = user.lastY;
            double userY = user.lastX;
            double[] vectorUserEvent = new double[]{eventX-userX, eventY-userY};
            double[] vectorNorth = new double[]{0, 1};
            double cos = (((vectorUserEvent[0]*vectorNorth[0]) + (vectorUserEvent[1]*vectorNorth[1])) /
                            (Math.sqrt(vectorUserEvent[0]*vectorUserEvent[0] + vectorUserEvent[1]*vectorUserEvent[1]) *
                            Math.sqrt(vectorNorth[0]*vectorNorth[0] + vectorNorth[1]*vectorNorth[1])));
            double angleWithNorth = 0;
            if (vectorUserEvent[0] > 0 && vectorUserEvent[1] > 0) {
                angleWithNorth = 360 - Math.toDegrees(Math.acos(cos));
            }
            if (vectorUserEvent[0] < 0 && vectorUserEvent[1] > 0) {
                angleWithNorth = Math.toDegrees(Math.acos(cos));
            }
            if (vectorUserEvent[0] < 0 && vectorUserEvent[1] < 0) {
                angleWithNorth = Math.toDegrees(Math.acos(cos));
            }
            if (vectorUserEvent[0] > 0 && vectorUserEvent[1] < 0) {
                angleWithNorth = 360 - Math.toDegrees(Math.acos(cos));
            }

            float mEventDegree = (float) (-angleWithNorth + mCurrentDegree);

            // create a rotation animation (reverse turn degree degrees)
            RotateAnimation ra = new RotateAnimation(
                    mEventDegree,
                    -degree,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            // how long the animation will take place
            ra.setDuration(210);
            // set the animation after the end of the reservation status
            ra.setFillAfter(true);
            // Start the animation
            mPointer.startAnimation(ra);
            mCurrentDegree = -degree;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (user.lastY == 0 && user.lastX == 0) {
            Toast.makeText(ActivityEventsList.this,
                    "Your device is located", Toast.LENGTH_LONG).show();
        }
        user.lastY = location.getLongitude();
        user.lastX = location.getLatitude();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(ActivityEventsList.this,
                provider + " is enabled", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(ActivityEventsList.this,
                provider + " is disabled", Toast.LENGTH_LONG).show();
    }
}