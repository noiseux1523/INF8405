package com.cedricnoiseux.projetfinal;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
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

import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

// Quand on pese sur le bouton de compas, l`image apparait et les events disparaissent
// avec removeAllViews du layout ou de quoi. On donne l'option de quand on clique sur le compas
// le compas disparait et les events re-apparaissent avec getAllEvents. À voir, on veut pointer vers les events ou
// juste le nord?

public class ActivityEventsList extends AppCompatActivity implements SensorEventListener {
    private TextView mMenu;
    private ScrollView mScroll;
    private LinearLayout mList;
    private RelativeLayout mMain;
    private ImageView mPointer;
    private TextView tvHeading;

    private SensorManager mSensorManager;
    private Sensor mCompass;
    private Sensor mAccelerometer;
    private Sensor mMagnetometer;
    private Sensor mMagnet;
    private float[] mLastAccelerometer = new float[3];
    private float[] mLastMagnetometer = new float[3];
    private boolean mLastAccelerometerSet = false;
    private boolean mLastMagnetometerSet = false;
    private float[] mR = new float[9];
    private float[] mOrientation = new float[3];
    private float mCurrentDegree = 0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eventslist);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        String username = extras.getString("user");
        User user = new User(username, 0, 0);

        mMenu = (TextView) findViewById(R.id.menu);
        mMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMenu();
            }
        });
        mScroll = (ScrollView) findViewById(R.id.scroll);
        mList = (LinearLayout) findViewById(R.id.list);
        mMain = (RelativeLayout) findViewById(R.id.main);
        mPointer = (ImageView) findViewById(R.id.imageDisplay);
        tvHeading = (TextView) findViewById(R.id.tvHeading);

        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mCompass = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        mMagnet = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        List<Sensor> msensorlist = mSensorManager.getSensorList(Sensor.TYPE_ALL);
        String sSensList = new String("");
        Sensor tmp;
        int x,i;
        for (i=0;i<msensorlist.size();i++){
            tmp = msensorlist.get(i);
            sSensList = " "+sSensList+tmp.getName(); // Add the sensor name to the string of sensors available
        }
        tvHeading.setText(sSensList);
//        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

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



//                    if (mParticipation.getCurrentTextColor() == Color.RED) {
//                        SqlUtility.addParticipation(user, event);
//                        mParticipation.setTextColor(Color.GREEN);
//                        mParticipation.setText("Attending - Click to interact");
//                        mOutput.setBackgroundResource(R.drawable.back_green);
//                    } else {
//                        SqlUtility.removeParticipation(user, event);
//                        mParticipation.setTextColor(Color.RED);
//                        mParticipation.setText("Not Attending - Click to interact");
//                        mOutput.setBackgroundResource(R.drawable.back_red);
//                    }
                }
            });

            // Add textviews
            mList.addView(mParticipation);
            mList.addView(mOutput);
        }
    }

    protected void onResume() {
        super.onResume();
//      mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
//      mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_GAME);
//      mPointer.setImageResource(R.drawable.pointer);
        mSensorManager.registerListener(this, mCompass, SensorManager.SENSOR_DELAY_GAME);
        mPointer.setImageResource(R.drawable.pointer);
    }

    protected void onPause() {
        super.onPause();
//      mSensorManager.unregisterListener(this, mAccelerometer);
//      mSensorManager.unregisterListener(this, mMagnetometer);
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // get the angle around the z-axis rotated
        float degree = Math.round(event.values[0]);

        tvHeading.setText("Heading: " + Float.toString(degree) + " degrees");

        // create a rotation animation (reverse turn degree degrees)
        RotateAnimation ra = new RotateAnimation(
                mCurrentDegree,
                -degree,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f);

        // how long the animation will take place
        ra.setDuration(210);

        // set the animation after the end of the reservation status
        ra.setFillAfter(true);

        // Start the animation
        mPointer.startAnimation(ra);
        mCurrentDegree = -degree;

//        if (event.sensor == mAccelerometer) {
//            System.arraycopy(event.values, 0, mLastAccelerometer, 0, event.values.length);
//            mLastAccelerometerSet = true;
//        }
//        if (event.sensor == mMagnetometer) {
//            System.arraycopy(event.values, 0, mLastMagnetometer, 0, event.values.length);
//            mLastMagnetometerSet = true;
//        }
//        if (mLastAccelerometerSet && mLastMagnetometerSet) {
//            SensorManager.getRotationMatrix(mR, null, mLastAccelerometer, mLastMagnetometer);
//            SensorManager.getOrientation(mR, mOrientation);
//            float azimuthInRadians = mOrientation[0];
//            float azimuthInDegress = (float)(Math.toDegrees(azimuthInRadians)+360)%360;
//            RotateAnimation ra = new RotateAnimation(
//                    mCurrentDegree,
//                    -azimuthInDegress,
//                    Animation.RELATIVE_TO_SELF, 0.5f,
//                    Animation.RELATIVE_TO_SELF, 0.5f);
//
//            ra.setDuration(250);
//            ra.setFillAfter(true);
//            mPointer.startAnimation(ra);
//            mCurrentDegree = -azimuthInDegress;
//        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}

/*
Menu
Liste evenement -> google maps ou format liste
Gerer mes evenments -> format liste evenement -> fenetre edit des evenements
*/

/*
Menu -> deux textviews pour les deux options
        Liste evenements ouvrent une 2e fenetre avec les deux options possibles (afficher evenement google maps ou en liste)
        Gerer evenements ouvrent une liste des evenements crees par le user
*/

/*
Liste Evenement Maps -> Google Maps affiche des markers avec les events (que tu participent ou non) et les infos + la position utilisateur
                        Indicateur que tu participe ou non
                        Indicateur different pour user
                        Date, (x,y), titre, id, nb max participants
*/

/*
Liste Evenement Liste -> Liste les differents events (que tu participes ou non) en format liste
                         Indicateur que tu participes ou non
                         Date, nom lieu, titre, id, nb max participants
*/

/*
Gerer mes evenements -> Liste de mes evenements que jai cree
                        Date, nom lieu, (x,y), titre, id, nb max participants
                        Options pour edit ou delete un event existant ou cree un nouveau
 */

/*
Edit mes events -> Fenetre pour editer un evenement existant
                   Field pour modifier Date, nom lieu, (x,y), titre, id(?) ou nb max participants
 */