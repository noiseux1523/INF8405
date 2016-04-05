package com.cedricnoiseux.projetfinal;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;

import java.util.Arrays;


public class ActivityMenu extends AppCompatActivity {
    GoogleAccountCredential mCredential;
    private TextView mOutputText; // Montre l'account ou les erreurs
    private TextView mShow;
    private TextView mManage;
    ProgressDialog mProgress;

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = { CalendarScopes.CALENDAR_READONLY };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // SqlUtility.runTest();

        // Check permissions
        requestPermissions();

        // Initialize variables
        mOutputText = (TextView) findViewById(R.id.mOutputText);
        mShow = (TextView) findViewById(R.id.show);
        mShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEvents();
            }
        });
        mManage = (TextView) findViewById(R.id.manage);
        mManage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manageEvents();
            }
        });
        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Waiting to connect ...");

        // Initialize credentials and service object.
        SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff())
                .setSelectedAccountName(settings.getString(PREF_ACCOUNT_NAME, null));
        SqlUtility.runTest();
    }

    public void showEvents() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Show Events");
        builder.setItems(new CharSequence[]{"List Format", "Map Format"},
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                        switch (which) {
                            case 0:
                                Intent intent = new Intent(getApplicationContext(), ActivityEventsList.class);
                                intent.putExtra("user", mCredential.getSelectedAccountName());
                                startActivity(intent);
                                break;
                            case 1:
                                Intent intent1 = new Intent(getApplicationContext(), ActivityGoogleMaps.class);
                                intent1.putExtra("user", mCredential.getSelectedAccountName());
                                startActivity(intent1);
                                break;
                        }
                    }
                });
        builder.create().show();
    }

    public void manageEvents() {
        Intent intent = new Intent(this, ActivityManageEvent.class);
        intent.putExtra("user", mCredential.getSelectedAccountName());
        startActivity(intent);
    }

    /**
     * Method to request various permissions
     */
    public void requestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 123);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 123);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.GET_ACCOUNTS}, 123);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, 123);

    }

    /**
     * Method to show message to notify if permission granted or not
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //ProfilePermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
        if (requestCode == 123 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Called whenever this activity is pushed to the foreground, such as after
     * a call to onCreate().
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (isGooglePlayServicesAvailable()) {
            refreshResults();
        } else {
            mOutputText.setText("Google Play Services required: " +
                    "after installing, close and relaunch this app.");
        }
    }

    /**
     * Called when an activity launched here (specifically, AccountPicker
     * and authorization) exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     * @param requestCode code indicating which activity result is incoming.
     * @param resultCode code indicating the result of the incoming
     *     activity result.
     * @param data Intent (containing result data) returned by incoming
     *     activity result.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    isGooglePlayServicesAvailable();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null && data.getExtras() != null) {
                    String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        mCredential.setSelectedAccountName(accountName);
                        SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                    }
                } else if (resultCode == RESULT_CANCELED) {
                    mOutputText.setText("Account unspecified.");
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode != RESULT_OK) {
                    chooseAccount();
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Attempt to get a set of data from the Google Calendar API to display. If the
     * email address isn't known yet, then call chooseAccount() method so the
     * user can pick an account.
     */
    private void refreshResults() {
        // No account selected
        if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        }
        // Account selected
        else {
            // Internet connection
            if (isDeviceOnline()) {
                mOutputText.setText(mCredential.getSelectedAccountName());
            }
            // No connection
            else {
                mOutputText.setText("No network connection available.");
            }
        }
    }

    /**
     * Starts an activity in Google Play Services so the user can pick an
     * account.
     */
    private void chooseAccount() {
        startActivityForResult(mCredential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
    }

    /**
     * Checks whether the device currently has a network connection.
     * @return true if the device has a network connection, false otherwise.
     */
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * Check that Google Play services APK is installed and up to date. Will
     * launch an error dialog for the user to update Google Play Services if
     * possible.
     * @return true if Google Play Services is available and up to
     *     date on this device; false otherwise.
     */
    private boolean isGooglePlayServicesAvailable() {
        final int connectionStatusCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        // Services unavailable
        if (GooglePlayServicesUtil.isUserRecoverableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
            return false;
        } else if (connectionStatusCode != ConnectionResult.SUCCESS ) {
            return false;
        }
        // Services available
        return true;
    }

    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     * @param connectionStatusCode code describing the presence (or lack of)
     *     Google Play Services on this device.
     */
    void showGooglePlayServicesAvailabilityErrorDialog(final int connectionStatusCode) {
        Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
                connectionStatusCode,
                ActivityMenu.this,
                REQUEST_GOOGLE_PLAY_SERVICES);
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
 */

/*
Edit mes events -> Fenetre pour editer un evenement existant
                   Field pour modifier Date, nom lieu, (x,y), titre, id(?) ou nb max participants
 */