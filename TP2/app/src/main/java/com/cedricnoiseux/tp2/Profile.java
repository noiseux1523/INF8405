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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class Profile extends AppCompatActivity {
    public final static String PROFILE = "profile.txt";
    public static Boolean PROFILE_COMPLETED = false;
    public final static int SELECT_PHOTO = 20;

    private EditText Group;
    private EditText Email;
    private TextView Save;
    private TextView Return;
    private TextView ClickPicture;
    private ImageView Photo;
    private CheckBox Organizer;
    private String Path;
    private String[] arraySpinner;
    // https://trinitytuts.com/tips/multiselect-spinner-item-in-android/
    private MultiSelectionSpinner Preferences;

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
                saveClicked(v);
            }
        });

        Return = (TextView) findViewById(R.id.Return);
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
                browsePicture(v);
            }
        });

        Preferences = (MultiSelectionSpinner) findViewById(R.id.Preferences);
        this.arraySpinner = new String[] {"Restaurant", "Park", "Pizzeria", "Cafeteria", "Library",
                                          "Appartment", "Office", "Class", "Computer Class", "Bar"};
        Preferences.setItems(arraySpinner);

        Photo = (ImageView) findViewById(R.id.Photo);

        Organizer = (CheckBox) findViewById(R.id.Organizer);

        // Check gallery permissions
//        if (!weHavePermissionToReadGallery()) {
//            requestReadGalleryPermissionFirst();
//        }

        // Check gallery permissions
        requestForGalleryPermission();
        requestForLocationPermissionFine();
        requestForLocationPermissionCoarse();

        // Check profile completeness
        checkProfile();
    }

    public void requestForLocationPermissionFine() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 123);
    }

    public void requestForLocationPermissionCoarse() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 123);
    }

    public void requestForGalleryPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 123);
        }
    }

//    /**
//     * Check external storage to find if permission allowed
//     * @return permission true or false
//     */
//    private boolean weHavePermissionToReadGallery() {
//        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
//    }
//
//    /**
//     * Show request to allow permission to gallery
//     */
//    private void requestReadGalleryPermissionFirst() {
//        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
//            Toast.makeText(this, "We need permission to access pictures.", Toast.LENGTH_LONG).show();
//            requestForResultGalleryPermission();
//        } else {
//            requestForResultGalleryPermission();
//        }
//    }
//
//    /**
//     * Method to allow permission to gallery
//     */
//    private void requestForResultGalleryPermission() {
//        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 123);
//    }

    /**
     * Show message to notify if permission granted or not
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 123 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Gives access to photo gallery
     * @param v
     */
    public void browsePicture(View v) {
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
     * @param v
     */
    public void saveClicked(View v){
        try {
            if (Path != null) {
            } else {
                InputStream in = openFileInput(PROFILE);
                if (in != null) {
                    InputStreamReader tmp = new InputStreamReader(in);
                    BufferedReader reader = new BufferedReader(tmp);
                    String str = reader.readLine();
                    Path = str;
                }
                in.close();
            }
            OutputStreamWriter out = new OutputStreamWriter(openFileOutput(PROFILE,0));
            out.write(Path + "\r\n");
            out.write(Group.getText().toString() + "\r\n");
            out.write(Email.getText().toString() + "\r\n");
            if (Organizer.isChecked()) {
                out.write("true" + "\r\n");
            } else {
                out.write("false" + "\r\n");
            }
            out.write(Preferences.getSelectedItemsAsString() + "\r\n");
            out.close();
            Toast.makeText(this, "The content is saved.", Toast.LENGTH_LONG).show();
        }
        catch (Throwable t) {
            Toast.makeText(this, "Exception: " + t.toString(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Method to go to the Menu window
     */
    public void goToMenu() {
        if (getLines() == 5) {
            Intent intent = new Intent(this, Menu.class);
            startActivity(intent);
        }
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
            InputStream in = openFileInput(PROFILE);
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
            // fichier par créé encore
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
            InputStream in = openFileInput(PROFILE);
            if (in == null || getLines() != 5) {
                Toast.makeText(this, "You must complete your profile first.", Toast.LENGTH_LONG).show();
            }
            else {
                readFileInEditor();
                if (!PROFILE_COMPLETED){
                    PROFILE_COMPLETED = true;
                    goToMenu();
                }
            }
        }

        catch (java.io.FileNotFoundException e) {
            Toast.makeText(this, "You must create your profile first.", Toast.LENGTH_LONG).show();
            // fichier par créé encore
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
            InputStream in = openFileInput(PROFILE);
            if (in != null) {
                InputStreamReader tmp = new InputStreamReader(in);
                BufferedReader reader = new BufferedReader(tmp);
                String str;
                int counter = 0;
                while ((str = reader.readLine()) != null) {
                    switch (counter) {
                        case 0:
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
                        case 4:
                            String[] pref = str.split(", ");
                            Preferences.setSelection(pref);
                            Preferences.simple_adapter.clear();
                            Preferences.simple_adapter.add(Preferences.buildSelectedItemString());
                        default:
                            break;
                    }
                    counter++;
                }
                in.close();
            }
        }
        catch (java.io.FileNotFoundException e) {
            // fichier par créé encore
        }
        catch (Throwable t) {
            Toast.makeText(this, "Exception: " + t.toString(), Toast.LENGTH_LONG).show();
        }
    }
}
