package com.example.cedric.flowfree;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.content.Intent;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button7x7 = (Button) findViewById(R.id.buttonLevel1);
        button7x7.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goToLevel_7x7();
            }
        });

        Button button8x8 = (Button) findViewById(R.id.button8x8);
        button8x8.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goToLevel_8x8();
            }
        });

        Button buttonExit = (Button) findViewById(R.id.buttonExit);
        buttonExit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showSimplePopUp();
            }
        });

    }

    private void goToLevel_7x7() {
        Intent intent = new Intent(this, SecondaryActivity.class);
        intent.putExtra("grid", true);
        startActivity(intent);
    }

    private void goToLevel_8x8() {
        Intent intent = new Intent(this, SecondaryActivity.class);
        intent.putExtra("grid", false);
        startActivity(intent);
    }

    protected void AppExit() {
        this.finish();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    protected void showSimplePopUp() {
        AlertDialog.Builder helpBuilder = new AlertDialog.Builder(this);
        helpBuilder.setMessage("Are you sure you want to exit?");
        helpBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        AppExit();
                    }
                });
        helpBuilder.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Retour à l'activite principale
                    }
                });
        AlertDialog helpDialog = helpBuilder.create();
        helpDialog.show();
    }

}
