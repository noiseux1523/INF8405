package com.cedricnoiseux.projetfinal;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;


public class ActivityManageEvent extends AppCompatActivity {
    private TextView mMenu;
    private TextView mCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        String username = extras.getString("user");
        User user = new User(username);

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
                createEvent();
            }
        });


    }

    public void goToMenu() {
        Intent intent = new Intent(this, ActivityMenu.class);
        startActivity(intent);
    }

    public void createEvent() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Event Information");
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        // id cest quoi?

        final EditText eventName = new EditText(this);
        eventName.setHint("Name");
        layout.addView(eventName);

        // host on l'a avec le username envoye en extra

        final EditText eventLocation = new EditText(this);
        eventLocation.setHint("Location");
        layout.addView(eventLocation);

        final EditText eventDate = new EditText(this);
        eventDate.setHint("Date");
        layout.addView(eventDate);

        dialog.setView(layout);
        dialog.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Add event to database and add event to activity window
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