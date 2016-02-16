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
                playLevel(1, grid);
            }
        });

        Button buttonLevel2 = (Button) findViewById(R.id.buttonLevel2);
        buttonLevel2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                playLevel(2, grid);
            }
        });

        Button buttonLevel3 = (Button) findViewById(R.id.buttonLevel3);
        buttonLevel3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                playLevel(3, grid);
            }
        });

        Button buttonExit = (Button) findViewById(R.id.buttonExit);
        buttonExit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showSimplePopUp();
            }
        });

    }

    private void playLevel(int level, boolean grid) {
        int l = grid ? level : level + 3;
        if (Level.maxLevelAllowed < l) return;
        Intent intent = new Intent(SecondaryActivity.this, Level.class);
        intent.putExtra("level", l);
        startActivity(intent);
    }

}
