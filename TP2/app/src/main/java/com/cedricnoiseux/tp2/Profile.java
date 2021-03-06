package com.cedricnoiseux.tp2;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class Profile extends AppCompatActivity {
    public static Boolean PROFILE_COMPLETED = false;
    public final static int SELECT_PHOTO = 20;

    private EditText Group;
    private EditText Email;
    private TextView Save;
    private TextView Return;
    private TextView ClickPicture;
    private ImageView Photo;
    private CheckBox Organizer;
    private String Path = "0";
    private static String Position = "0 0";
    private static String Calendar = "0";
    private String[] arraySpinner;
    private MultiSelectionSpinner Preferences;

    /**
     * Create the main activity.
     * @param savedInstanceState previously saved instance data.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Group = (EditText) findViewById(R.id.Group);
        Email = (EditText) findViewById(R.id.Email);
        Save = (TextView) findViewById(R.id.Save);
        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveClicked();
            }
        });
        Return = (TextView) findViewById(R.id.Menu);
        Return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMenu();
            }
        });
        ClickPicture = (TextView) findViewById(R.id.ClickPicture);
        ClickPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                browsePicture();
            }
        });
        Preferences = (MultiSelectionSpinner) findViewById(R.id.Preferences);
        this.arraySpinner = new String[] {"Restaurant", "Park", "Pizzeria", "Cafeteria", "Library",
                                          "Appartment", "Office", "Class", "Computer Class", "Bar"};
        Preferences.setItems(arraySpinner);
        Photo = (ImageView) findViewById(R.id.Photo);
        Organizer = (CheckBox) findViewById(R.id.Organizer);

        // Check permissions
        requestPermissions();

        // Check profile completeness
        checkProfile();
    }

    /**
     * Method to request various permissions
     */
    public void requestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 123);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 123);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 123);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CALENDAR}, 123);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_CALENDAR}, 123);

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
        ProfilePermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
        if (requestCode == 123 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Method to give access to photo gallery
     */
    @NeedsPermission({Manifest.permission.READ_EXTERNAL_STORAGE})
    public void browsePicture() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, SELECT_PHOTO);
    }

    /**
     * Method to select and show picture to user
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_PHOTO && resultCode == RESULT_OK && data != null) {

            Uri pickedImage = data.getData();

            String[] filePath = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(pickedImage, filePath, null, null, null);
            cursor.moveToFirst();
            String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));
            Path = imagePath;

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);
            Photo.setImageBitmap(bitmap);

            cursor.close();
        }
    }

    /**
     * Method to save the profile information
     */
    public void saveClicked() {
        try {
            // Open existing file to read
            InputStream in = openFileInput(String.valueOf(R.string.PROFILE));
            if (in != null) {
                InputStreamReader tmp = new InputStreamReader(in);
                BufferedReader reader = new BufferedReader(tmp);
                String str;
                int counter = 0;
                // Assign not null values to Path, Position and Calendar
                while ((str = reader.readLine()) != null) {
                    switch (counter) {
                        case 0:
                            if (Path.equals("0"))
                                Path = str;
                            break;
                        case 5:
                            if (!Position.equals("0 0"))
                                Position = str;
                            break;
                        case 6:
                            if(!Calendar.equals("0"))
                                Calendar = str;
                            break;
                        default:
                            break;
                    }
                    counter++;
                }
            }
            in.close();

            // Open existing file to write
            OutputStreamWriter out = new OutputStreamWriter(openFileOutput(String.valueOf(R.string.PROFILE),0));

            out.write(Path + "\r\n");

            out.write(Group.getText().toString() + "\r\n");

            out.write(Email.getText().toString() + "\r\n");

            boolean admin;
            if (Organizer.isChecked()) {
                admin = true;
                out.write("true" + "\r\n");
            } else {
                admin = false;
                out.write("false" + "\r\n");
            }

            List<String> activity = Preferences.getSelectedStrings();
            out.write(Preferences.getSelectedItemsAsString() + "\r\n");

            String[] splitPos = Position.split("\\s+");
            float posX = Float.parseFloat(splitPos[0]);
            float posY = Float.parseFloat(splitPos[1]);
            out.write(Position + "\r\n");

            out.write(Calendar + "\r\n");

            out.close();

            // Store the user in external server
            if (Email.getText().toString() != null && Group.getText().toString() != null && activity.size() >= 3){
                User.getUser(Email.getText().toString(), Group.getText().toString(), admin, activity, posX, posY);
            }
            Toast.makeText(this, "The content is saved.", Toast.LENGTH_LONG).show();
        }
        catch (java.io.FileNotFoundException e) {
            // File not created
            try {
                OutputStreamWriter out = new OutputStreamWriter(openFileOutput(String.valueOf(R.string.PROFILE),0));
                out.close();
                saveClicked();
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        catch (Throwable t) {
            Toast.makeText(this, "Exception: " + t.toString(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Method to go to the Menu window
     */
    public void goToMenu() {
        // File completed, position and calendar updated (when coming from menu)
        if (getLines() >= 7
                && !Path.equals("0")
                && Preferences.getSelectedStrings().size() >= 3
                && !Position.equals("0 0")
                && !Calendar.equals("0")
                && PROFILE_COMPLETED) {
            Intent intent = new Intent(this, Menu.class);
            startActivity(intent);
        }
        // File completed, position and calendar need to be updated (when starting application)
        else if (getLines() >= 7 && !Path.equals("0") && Preferences.getSelectedStrings().size() >= 3
                && (Position.equals("0 0") || Calendar.equals("0") || !PROFILE_COMPLETED)) {
            PROFILE_COMPLETED = true;
            AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
            dlgAlert.setTitle("Organisapp");
            dlgAlert.setCancelable(true);
                    dlgAlert.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                            startActivity(intent);
                        }
                    });
            dlgAlert.setMessage("We need your location to continue");
            dlgAlert.create().show();
        }
        // File incomplete
        else {
            Toast.makeText(this, "You must complete your profile before going to the menu.", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Method to give the number of lines in a text file
     * @return number of lines
     */
    public int getLines() {
        int lines = 0;
        try {
            // Open file to count not null lines
            InputStream in = openFileInput(String.valueOf(R.string.PROFILE));
            InputStreamReader tmp = new InputStreamReader(in);
            BufferedReader reader = new BufferedReader(tmp);
            String str;
            while ((str = reader.readLine()) != null) {
                if (!str.equals("")) {
                    lines++;
                }
            }
            reader.close();
        }
        catch (java.io.FileNotFoundException e) {
            // File note created
        }
        catch (Throwable t) {
            Toast.makeText(this, "Exception: " + t.toString(), Toast.LENGTH_LONG).show();
        }
        return lines;
    }

    /**
     * Method to verify if the profile is complete or not
     */
    public void checkProfile() {
        try {
            InputStream in = openFileInput(String.valueOf(R.string.PROFILE));
            // File doesn't exist or incomplete
            int lignes = getLines();
            if (in == null || getLines() < 7) {
                Toast.makeText(this, "You must complete your profile first.", Toast.LENGTH_LONG).show();
            }
            // File exists
            else {
                // Fill each input and go to menu if app is starting
                readFileInEditor();
                if (!PROFILE_COMPLETED) {
                    goToMenu();
                }
            }
            in.close();
        }

        catch (java.io.FileNotFoundException e) {
            // File not created
            Toast.makeText(this, "You must create your profile first.", Toast.LENGTH_LONG).show();
        }

        catch (Throwable t) {
            Toast.makeText(this, "Exception: " + t.toString(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Method to fill the user profile if already created
     */
    public void readFileInEditor() {
        try {
            // Open existing file
            InputStream in = openFileInput(String.valueOf(R.string.PROFILE));
            if (in != null) {
                InputStreamReader tmp = new InputStreamReader(in);
                BufferedReader reader = new BufferedReader(tmp);
                String str;
                int counter = 0;
                // Fill each profile input and assign values to Position and Calendar
                while ((str = reader.readLine()) != null) {
                    switch (counter) {
                        case 0:
                            Path = str;
                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                            Bitmap bitmap = BitmapFactory.decodeFile(str, options);
                            Photo.setImageBitmap(bitmap);
                            break;
                        case 1:
                            Group.setText(str);
                            break;
                        case 2:
                            Email.setText(str);
                            break;
                        case 3:
                            if (str.equals("true")) {
                                Organizer.setChecked(true);
                            } else {
                                Organizer.setChecked(false);
                            }
                            break;
                        case 4:
                            String[] pref = str.split(", ");
                            Preferences.setSelection(pref);
                            Preferences.simple_adapter.clear();
                            Preferences.simple_adapter.add(Preferences.buildSelectedItemString());
                            break;
                        case 5:
                            Position = str;
                            break;
                        case 6:
                            Calendar = str;
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
}
