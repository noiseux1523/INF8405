package com.cedricnoiseux.projetfinal;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.RequiresPermission;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 *
 */
public class ActivityManageEvent extends AppCompatActivity {
    private TextView mMenu;
    private TextView mCreate;
    private LinearLayout mList;
    private ScrollView mScroll;
    Geocoder geocoder;

    // private DatePicker mDP;
    private int year;
    private int month;
    private int day;
    static final int DATE_DIALOG_ID = 999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
//        final User user = extras.getParcelable("user");
        String username = extras.getString("user");
        final User user = new User(username, 0, 0);

        geocoder = new Geocoder(this, Locale.ENGLISH);
        mScroll = (ScrollView) findViewById(R.id.scroll);
        mList = (LinearLayout) findViewById(R.id.list);

        mMenu = (TextView) findViewById(R.id.menu);
        mMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMenu();
            }
        });

        mCreate = (TextView) findViewById(R.id.create);
        mCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createEvent(user);
            }
        });

        // Search and show all events created by user
        try {
            getEvents(user);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     *
     */
    public void goToMenu() {
        Intent intent = new Intent(this, ActivityMenu.class);
        startActivity(intent);
    }

    /**
     *
     * @param user
     * @throws Exception
     */
    public void getEvents(User user) throws Exception {
        // Initialize linked list for events
        LinkedList<Event> e1 = SqlUtility.getCreatedEvents(user);

        // Remove views if not null
        if (mList.getChildCount() > 0) {
            mList.removeAllViews();
        }

        // Show events on screen
        for (Event event : e1) {
            showEvent(event, user);
        }
    }

    /**
     *
     * @param user
     */
    public void createEvent(final User user) {
        // Create event window
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Event Information (Follow Formats)");
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        // Event Name
        TextView eventNameTitle = new TextView(this);
        eventNameTitle.setText("Name");
        eventNameTitle.setTextSize(16);
        eventNameTitle.setPadding(0, 15, 0, 0);
        eventNameTitle.setGravity(Gravity.CENTER);
        layout.addView(eventNameTitle);

        final EditText eventName = new EditText(this);
        eventName.setTextSize(16);
        layout.addView(eventName);

        // Event Location
        TextView eventLocationTitle = new TextView(this);
        eventLocationTitle.setText("Location (Adress, City, Country, Zip Code)");
        eventLocationTitle.setTextSize(16);
        eventLocationTitle.setPadding(0, 15, 0, 0);
        eventLocationTitle.setGravity(Gravity.CENTER);
        layout.addView(eventLocationTitle);

        final EditText eventLocation = new EditText(this);
        eventLocation.setTextSize(16);
        layout.addView(eventLocation);

        // Event Time
        TextView eventTimeTitle = new TextView(this);
        eventTimeTitle.setText("Time");
        eventTimeTitle.setTextSize(16);
        eventTimeTitle.setPadding(0, 15, 0, 0);
        eventTimeTitle.setGravity(Gravity.CENTER);
        layout.addView(eventTimeTitle);

        LayoutInflater inflaterTime = LayoutInflater.from(this);
        final TimePicker eventTimePicker = (TimePicker)inflaterTime.inflate(R.xml.timepicker, null);
        eventTimePicker.setIs24HourView(true);
        layout.addView(eventTimePicker);

        // Event Date
        TextView eventDateTitle = new TextView(this);
        eventDateTitle.setText("Date");
        eventDateTitle.setTextSize(16);
        eventDateTitle.setPadding(0, 15, 0, 0);
        eventDateTitle.setGravity(Gravity.CENTER);
        layout.addView(eventDateTitle);

        LayoutInflater inflaterDate = LayoutInflater.from(this);
        final DatePicker eventDatePicker = (DatePicker)inflaterDate.inflate(R.xml.datepicker, null);
        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        eventDatePicker.init(year, month, day, null);
        eventDatePicker.setCalendarViewShown(false);
        eventDatePicker.setSpinnersShown(true);
        layout.addView(eventDatePicker);

        dialog.setView(layout);

        dialog.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    // ADD EVENT TO DATABASE
                    int id = -1;
                    String name = eventName.getText().toString();
                    String host = user.email;
                    String location = eventLocation.getText().toString();
                    Date dateOfEvent = new Date();
                    dateOfEvent.setHours(eventTimePicker.getCurrentHour());
                    dateOfEvent.setMinutes(eventTimePicker.getCurrentMinute());
                    dateOfEvent.setSeconds(0);
                    dateOfEvent.setDate(eventDatePicker.getDayOfMonth());
                    dateOfEvent.setMonth(eventDatePicker.getMonth());
                    dateOfEvent.setYear(eventDatePicker.getYear() - 1900);
                    List<Address> adresse = geocoder.getFromLocationName(location, 1);
                    double latitude = adresse.get(0).getLatitude();
                    double longitude = adresse.get(0).getLongitude();

                    Event newEvent = new Event(-1, name, host, location, dateOfEvent, latitude, longitude);
                    SqlUtility.addEvent(newEvent);

                    // SHOW EVENT ON SCREEN
                    showEvent(newEvent, user);

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Dismiss window
            }
        });

        dialog.show();
    }

    /**
     *
     * @param event
     */
    public void showEvent(final Event event, final User user) {
        try {
            // Initialize textviews
            final TextView mEdit = new TextView(getApplicationContext());
            final TextView mOutput = new TextView(getApplicationContext());

            // Set participation or not
            mEdit.setTextColor(Color.GREEN);
            mEdit.setText("Click to Edit or Delete");
            mEdit.setTypeface(null, Typeface.BOLD);
            mEdit.setGravity(Gravity.CENTER);
            mEdit.setTextSize(18);
            mEdit.setPadding(0, 20, 0, 10);

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
            mOutput.setBackgroundResource(R.drawable.back_green);

            // Set onClickListener
            mOutput.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // WINDOW TO EDIT
                    // Create event window
                    AlertDialog.Builder dialog = new AlertDialog.Builder(ActivityManageEvent.this);
                    dialog.setTitle("Event Information (Follow Formats)");
                    LinearLayout layout = new LinearLayout(ActivityManageEvent.this);
                    layout.setOrientation(LinearLayout.VERTICAL);

                    // Event Name
                    TextView eventNameTitle = new TextView(ActivityManageEvent.this);
                    eventNameTitle.setText("Name");
                    eventNameTitle.setTextSize(16);
                    eventNameTitle.setPadding(0, 15, 0, 0);
                    eventNameTitle.setGravity(Gravity.CENTER);
                    layout.addView(eventNameTitle);

                    final EditText eventName = new EditText(ActivityManageEvent.this);
                    eventName.setTextSize(16);
                    eventName.setText(event.name);
                    layout.addView(eventName);

                    // Event Location
                    TextView eventLocationTitle = new TextView(ActivityManageEvent.this);
                    eventLocationTitle.setText("Location (Adress, City, Country, Zip Code)");
                    eventLocationTitle.setTextSize(16);
                    eventLocationTitle.setPadding(0, 15, 0, 0);
                    eventLocationTitle.setGravity(Gravity.CENTER);
                    layout.addView(eventLocationTitle);

                    final EditText eventLocation = new EditText(ActivityManageEvent.this);
                    eventLocation.setTextSize(16);
                    eventLocation.setText(event.locationName);
                    layout.addView(eventLocation);

                    // Event Time
                    TextView eventTimeTitle = new TextView(ActivityManageEvent.this);
                    eventTimeTitle.setText("Time");
                    eventTimeTitle.setTextSize(16);
                    eventTimeTitle.setPadding(0, 15, 0, 0);
                    eventTimeTitle.setGravity(Gravity.CENTER);
                    layout.addView(eventTimeTitle);

                    LayoutInflater inflaterTime = LayoutInflater.from(ActivityManageEvent.this);
                    final TimePicker eventTimePicker = (TimePicker)inflaterTime.inflate(R.xml.timepicker, null);
                    eventTimePicker.setIs24HourView(true);
                    eventTimePicker.setCurrentHour(event.date.getHours());
                    eventTimePicker.setCurrentMinute(event.date.getMinutes());
                    layout.addView(eventTimePicker);

                    // Event Date
                    TextView eventDateTitle = new TextView(ActivityManageEvent.this);
                    eventDateTitle.setText("Date");
                    eventDateTitle.setTextSize(16);
                    eventDateTitle.setPadding(0, 15, 0, 0);
                    eventDateTitle.setGravity(Gravity.CENTER);
                    layout.addView(eventDateTitle);

                    LayoutInflater inflaterDate = LayoutInflater.from(ActivityManageEvent.this);
                    final DatePicker eventDatePicker = (DatePicker)inflaterDate.inflate(R.xml.datepicker, null);
                    eventDatePicker.init(event.date.getYear(), event.date.getMonth(), event.date.getDay(), null);
                    eventDatePicker.setCalendarViewShown(false);
                    eventDatePicker.setSpinnersShown(true);
                    layout.addView(eventDatePicker);

                    // Display the window
                    dialog.setView(layout);

                    dialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                // Update event attributes
                                event.name = eventName.getText().toString();
                                String location = eventLocation.getText().toString();
                                event.locationName = location;
                                Date dateOfEvent = new Date();
                                dateOfEvent.setHours(eventTimePicker.getCurrentHour());
                                dateOfEvent.setMinutes(eventTimePicker.getCurrentMinute());
                                dateOfEvent.setSeconds(0);
                                dateOfEvent.setDate(eventDatePicker.getDayOfMonth());
                                dateOfEvent.setMonth(eventDatePicker.getMonth());
                                dateOfEvent.setYear(eventDatePicker.getYear() - 1900);
                                event.date = dateOfEvent;
                                List<Address> adresse = geocoder.getFromLocationName(location, 1);
                                event.locX = adresse.get(0).getLatitude();
                                event.locY = adresse.get(0).getLongitude();

                                // Update event in database
                                SqlUtility.updateEvent(event);

                                // Update event info on screen
                                getEvents(user);

                            } catch (ParseException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    dialog.setNeutralButton("Exit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Dismiss window
                        }
                    });

                    dialog.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                // Remove event from database
                                SqlUtility.removeEvent(event);

                                // Update events to remove event from screen
                                getEvents(user);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    dialog.show();
                }
            });

            // Add textviews
            mList.addView(mEdit);
            mList.addView(mOutput);

        } catch (Exception e) {
            e.printStackTrace();
        }

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
                        Afficher pop up window avec 3 options:
                            Edit ou Delete ou Rien
                        Bouton pour create
 */

/*
Edit mes events -> Fenetre pour editer un evenement existant
                   Field pour modifier Date, nom lieu, (x,y), titre, id(?) ou nb max participants
 */