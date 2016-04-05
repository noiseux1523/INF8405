package com.cedricnoiseux.projetfinal;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.LinkedList;

public class ActivityEventsList extends AppCompatActivity {
    private TextView mMenu;
    private ScrollView mScroll;
    private LinearLayout mList;

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
            mParticipation.setText("Not Attending - Click to join");
            int i = 0;
            while (i < e2.size()) {
                if (e2.get(i).id == event.id) {
                    mParticipation.setTextColor(Color.GREEN);
                    mParticipation.setText("Attending - Click to cancel");
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
            mOutput.setTextColor(Color.WHITE);
            mOutput.setTypeface(null, Typeface.BOLD);
            mOutput.setGravity(Gravity.CENTER);
            mOutput.setTextSize(18);
            mOutput.setPadding(0, 0, 0, 10);
            if (mParticipation.getText() == "Not Attending - Click to join") {
                mOutput.setBackgroundResource(R.drawable.back_red);
            } else {
                mOutput.setBackgroundResource(R.drawable.back_green);
            }


            // Set onClickListener
            mOutput.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mParticipation.getCurrentTextColor() == Color.RED) {
                        SqlUtility.addParticipation(user, event);
                        mParticipation.setTextColor(Color.GREEN);
                        mParticipation.setText("Attending - Click to cancel");
                        mOutput.setBackgroundResource(R.drawable.back_green);
                    } else {
                        SqlUtility.removeParticipation(user, event);
                        mParticipation.setTextColor(Color.RED);
                        mParticipation.setText("Not Attending - Click to join");
                        mOutput.setBackgroundResource(R.drawable.back_red);
                    }
                }
            });

            // Add textviews
            mList.addView(mParticipation);
            mList.addView(mOutput);
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
 */

/*
Edit mes events -> Fenetre pour editer un evenement existant
                   Field pour modifier Date, nom lieu, (x,y), titre, id(?) ou nb max participants
 */