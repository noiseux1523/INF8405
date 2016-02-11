package com.example.cedric.flowfree;

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
                AppExit();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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

    protected void AppExit()
    {
        this.finish();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
