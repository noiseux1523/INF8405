package com.cedricnoiseux.projetfinal;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

//
// MODIFY FOR GOOGLE MAPS ACTIVITY
//


public class ActivityEventsList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eventslist);

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