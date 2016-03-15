package com.cedricnoiseux.tp2;

import android.Manifest;
import android.app.Activity;
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
import android.view.View;
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

public class Settings extends AppCompatActivity {
    public final static String PROFILE = "profile.txt";
    private final static int SELECT_PHOTO = 20;

    private EditText Group;
    private EditText Email;

    private TextView Save;
    private TextView Return;
    private TextView ClickPicture;

    private ImageView Photo;

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
        ClickPicture = (TextView) findViewById(R.id.ClickPicture);
        ClickPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                browsePicture(v);
            }
        });

        Photo = (ImageView) findViewById(R.id.Photo);

        if (!weHavePermissionToReadContacts()) {
            requestReadGalleryPermissionFirst();
        }
        checkProfile();
        readFileInEditor();

    }

    private boolean weHavePermissionToReadContacts() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestReadGalleryPermissionFirst() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Toast.makeText(this, "We need permission to access pictures.", Toast.LENGTH_LONG).show();
            requestForResultGalleryPermission();
        } else {
            requestForResultGalleryPermission();
        }
    }

    private void requestForResultGalleryPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 123);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 123
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }

    public void browsePicture(View v) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, SELECT_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_PHOTO && resultCode == RESULT_OK && data != null) {

            Uri pickedImage = data.getData();

            String[] filePath = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(pickedImage, filePath, null, null, null);
            cursor.moveToFirst();
            String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);
            Photo.setImageBitmap(bitmap);

            try {
                OutputStreamWriter out = new OutputStreamWriter(openFileOutput(PROFILE,0));
                out.write(Group.getText().toString() + "\r\n");
                out.write(Email.getText().toString() + "\r\n");
                out.close();
            }
            catch (Throwable t) {
                Toast.makeText(this, "Exception: " + t.toString(), Toast.LENGTH_LONG).show();
            }


            cursor.close();
        }
    }

    public void saveClicked(View v){
        try {
            OutputStreamWriter out = new OutputStreamWriter(openFileOutput(PROFILE,0));
            out.write(Group.getText().toString() + "\r\n");
            out.write(Email.getText().toString() + "\r\n");
            out.close();
            Toast.makeText(this, "The content is saved.", Toast.LENGTH_LONG).show();
        }
        catch (Throwable t) {
            Toast.makeText(this, "Exception: " + t.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private void checkProfile() {
        try {
            InputStream in = openFileInput(PROFILE);
            if (in == null) {
                Toast.makeText(this, "You have to complete your profile first.", Toast.LENGTH_LONG).show();
            }
            else {
                readFileInEditor();
            }
        }

        catch (java.io.FileNotFoundException e) {
            Toast.makeText(this, "You have to create your profile first.", Toast.LENGTH_LONG).show();
            // fichier par créé encore
        }

        catch (Throwable t) {
            Toast.makeText(this, "Exception: " + t.toString(), Toast.LENGTH_LONG).show();
        }
    }

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
                            Group.setText(str);
                            break;
                        case 1:
                            Email.setText(str);
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
            // fichier par créé encore
        }
        catch (Throwable t) {
            Toast.makeText(this, "Exception: " + t.toString(), Toast.LENGTH_LONG).show();
        }
    }

}
