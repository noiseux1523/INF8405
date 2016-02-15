package com.example.cedric.flowfree;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Level2_8x8 extends MainActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level2_8x8);

        Button buttonExit = (Button) findViewById(R.id.buttonExit);
        buttonExit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AppExit();
            }
        });

        Button buttonRetry = (Button) findViewById(R.id.buttonRetry);
        buttonRetry.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                retry();
            }
        });

        Button buttonBack = (Button) findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                back();
            }
        });

        Button buttonNext = (Button) findViewById(R.id.buttonNext);
        buttonNext.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }
        });

    }

    protected void retry(){
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    private void back(){
        super.onBackPressed();
    }

}
