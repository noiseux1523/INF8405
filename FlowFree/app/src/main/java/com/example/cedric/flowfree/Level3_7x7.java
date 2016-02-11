package com.example.cedric.flowfree;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Level3_7x7 extends MainActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level3_7x7);

        Button buttonExit = (Button) findViewById(R.id.buttonExit);
        buttonExit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AppExit();
            }
        });

    }

}
