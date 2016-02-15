package com.example.cedric.flowfree;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SecondaryActivity extends MainActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secondary);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        final boolean grid = extras.getBoolean("grid");

        Button buttonLevel1 = (Button) findViewById(R.id.buttonLevel1);
        buttonLevel1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goToLevel1(grid);
            }
        });

        Button buttonLevel2 = (Button) findViewById(R.id.buttonLevel2);
        buttonLevel2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // IMPLEMENT
            }
        });

        Button buttonLevel3 = (Button) findViewById(R.id.buttonLevel3);
        buttonLevel3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // IMPLEMENT
            }
        });

        Button buttonExit = (Button) findViewById(R.id.buttonExit);
        buttonExit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showSimplePopUp();
            }
        });

    }

    private void goToLevel1(boolean grid) {
        if (grid == true){
            Intent intent = new Intent(this, Level1_7x7.class);
            startActivity(intent);
        }
        else {
            Intent intent = new Intent(this, Level1_8x8.class);
            startActivity(intent);
        }

    }

    private void goToLevel2(boolean grid) {

    }

    private void goToLevel3(boolean grid) {

    }

}
