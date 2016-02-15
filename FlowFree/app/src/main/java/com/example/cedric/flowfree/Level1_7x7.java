package com.example.cedric.flowfree;

import android.content.Intent;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import static java.lang.Math.abs;
import static java.lang.Math.floor;

public class Level1_7x7 extends MainActivity {

    double x_ini;
    double y_ini;
    double x_int;
    double y_int;
    double x_fin;
    double y_fin;

    // x_ini, y_ini, x_int, y_int, x_fin, y_fin
    double[] position = {x_ini, y_ini, x_int, y_int, x_fin, y_fin};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level1_7x7);

        GridLayout myLayout = (GridLayout) findViewById(R.id.gridLayout);
        myLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                handleTouch(event, position);
                return true;
            }
        });

        Button buttonExit = (Button) findViewById(R.id.buttonExit);
        buttonExit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showSimplePopUp();
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

    private void handleTouch(MotionEvent m, double [] position){

        TextView textView1 = (TextView)findViewById(R.id.textView1);

        int pointerCount = m.getPointerCount();

        x_ini = position[0];
        y_ini = position[1];
        x_int = position[2];
        y_int = position[3];
        x_fin = position[4];
        y_fin = position[5];

        for (int i = 0; i < pointerCount; i++)
        {
            double x = (int) m.getX(i);
            double y = (int) m.getY(i);
            int action = m.getActionMasked();
            int id = m.getPointerId(i);
            int actionIndex = m.getActionIndex();
            String actionString;

            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    actionString = "DOWN";
                    //showSimplePopUp();
                    double x_ini = floor(m.getX(i)/140);
                    double y_ini = floor(m.getY(i)/140);
                    if (x_ini > -1 && x_ini < 7 && y_ini > -1 && y_ini < 7){
                        x_ini = (x_ini * 140) + 70;
                        y_ini = (y_ini * 140) + 70;
                        x_int = x_ini;
                        y_int = y_ini;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    actionString = "UP";
                    //showSimplePopUp();
                    double x_fin = floor(m.getX(i)/140);
                    double y_fin = floor(m.getY(i)/140);
                    if (x_fin > -1 && x_fin < 7 && y_fin > -1 && y_fin < 7){
                        x_fin = (x_fin * 140) + 70;
                        y_fin = (y_fin * 140) + 70;
                        x_int = x_fin;
                        y_int = y_fin;
                    }
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    actionString = "PNTR DOWN";
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    actionString = "PNTR UP";
                    break;
                case MotionEvent.ACTION_MOVE: // 25 EST UN CHIFFRE ARBITRAIRE, carré sont 150x150
                    actionString = "MOVE";
                    if ((x/140) > 0 && (x/140) < 7 && (y/140) > 0 && (y/140) < 7) {
                        if (x > x_int + 70) {
                            x_int += 140;
                        }
                        else if (x < x_int - 70){
                            x_int -= 140;
                        }
                        else if (y > y_int + 70) {
                            y_int += 140;
                        }
                        else if (y < y_int - 70){
                            y_int -= 140;
                        }
                    }
                    break;
                default:
                    actionString = "";
            }

            String touchStatus = "Action: " + actionString + " Index: " + actionIndex + " ID: " + id + " X: " + x_int + " Y: " + y_int;

            if (id == 0){
                textView1.setText(touchStatus);
            }
        }

        position[0] = x_ini;
        position[1] = y_ini;
        position[2] = x_int;
        position[3] = y_int;
        position[4] = x_fin;
        position[5] = y_fin;

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
