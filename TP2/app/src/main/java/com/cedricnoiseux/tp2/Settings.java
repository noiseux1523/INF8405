package com.cedricnoiseux.tp2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class Settings extends Activity {
    public final static String PROFILE = "profile.txt";
    private EditText Email;
    private TextView Save;
    private TextView Return;
    private TextView ClickPicture;
    private ImageView Photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        checkProfile();

        readFileInEditor();

        Email = (EditText) findViewById(R.id.Email);

        Save = (TextView) findViewById(R.id.Save);
        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveClicked(v);
            }
        });

        ClickPicture = (TextView) findViewById(R.id.ClickPicture);

        Photo = (ImageView) findViewById(R.id.Photo);

    }

    // VOIR http://stackoverflow.com/questions/29196227/android-studio-display-images-from-my-android-gallery
    // POUR LA PHOTO DE PROFIL

    public void saveClicked(View v){
        try {
            OutputStreamWriter out = new OutputStreamWriter(openFileOutput(PROFILE, 0));
            out.write(Email.getText().toString());
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
                StringBuilder buf = new StringBuilder();
                while ((str = reader.readLine()) != null) {
                    buf.append(str);
                }
                in.close();
                Email.setText(buf.toString());
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
