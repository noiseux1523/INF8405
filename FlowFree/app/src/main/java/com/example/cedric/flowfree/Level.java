package com.example.cedric.flowfree;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import static java.lang.Math.abs;
import static java.lang.Math.floor;

public class Level extends MainActivity {

    GridLayout myLayout = null;
    ImageView mainImageView = null;
    Canvas canvas = null;

    double x_ini;
    double y_ini;
    double x_int;
    double y_int;
    double x_fin;
    double y_fin;

    // x_ini, y_ini, x_int, y_int, x_fin, y_fin
    double[] position = {x_ini, y_ini, x_int, y_int, x_fin, y_fin};

    boolean[] success = {false, false, false, false, false, false};

    Game g = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        final int level = extras.getInt("level");

        mainImageView = (ImageView) findViewById((R.id.grid77));
        mainImageView.setOnTouchListener(new View.OnTouchListener() {
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
                back(g.getLevel());
            }
        });

        Button buttonNext = (Button) findViewById(R.id.buttonNext);
        buttonNext.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                next(g.getLevel());
            }
        });

        g = new Game(level);
        g.draw(mainImageView, getApplicationContext());
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



        double x = (int) m.getX(0);
        double y = (int) m.getY(0);
        int action = m.getActionMasked();
        int id = m.getPointerId(0);
        int actionIndex = m.getActionIndex();
        String actionString;
        int boxWidth = mainImageView.getWidth()/7;///myLayout.getRowCount();
        int idX = (int)floor(m.getX(0)/boxWidth);
        int idY = (int)floor(m.getY(0)/boxWidth);
        if (idX < 0 || idY < 0 || idX > 6 || idY > 7) return;

        switch (action) {
                case MotionEvent.ACTION_DOWN:
                    actionString = "DOWN";
                    g.down(idX, idY) ;
                    break;
                case MotionEvent.ACTION_UP:
                    actionString = "UP";
                    g.up();
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    actionString = "PNTR DOWN";
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    actionString = "PNTR UP";
                    break;
                case MotionEvent.ACTION_MOVE: // 25 EST UN CHIFFRE ARBITRAIRE, carré sont 150x150
                    actionString = "MOVE";
                    g.move(idX, idY) ;
                    break;
                default:
                    actionString = "";
            }

            String touchStatus = "Action: " + actionString /*+ " Index: " + actionIndex + " ID: " +
            id +*/+ " X: " + idX + " Y: " + idY;

            if (id == 0){
                textView1.setText(touchStatus);
            }

        g.draw(mainImageView, getApplicationContext());

        if (g.finished()) {
            AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
            dlgAlert.setTitle("FlowFree");
            dlgAlert.setCancelable(true);
            dlgAlert.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //dismiss the dialog
                        }
                    });
            if (g.isWon()) {
                success[g.getLevel() - 1] = true;
                if (g.getLevel() == 6) {
                    dlgAlert.setMessage("You won! You finished all levels");
                }
                else {
                    dlgAlert.setMessage("You won! Try this next level :)");
                    g = new Game(g.getLevel() + 1);
                    g.draw(mainImageView, getApplicationContext());
                }
            }
            else {
                dlgAlert.setMessage("You lost... :(");
            }

            dlgAlert.create().show();
            g.restart();
        }
        position[0] = x_ini;
        position[1] = y_ini;
        position[2] = x_int;
        position[3] = y_int;
        position[4] = x_fin;
        position[5] = y_fin;

    }

    protected void retry(){
//        Intent intent = getIntent();
//        finish();
//        startActivity(intent);
        g.restart();
    }

    private void back(int level){
        if (level == 1) {
            super.onBackPressed();
        }
        else {
            g = new Game(level - 1);
            g.draw(mainImageView, getApplicationContext());
        }
    }

    private void next(int level) {
        if (success[level - 1]){
            g = new Game(level + 1);
            g.draw(mainImageView, getApplicationContext());
        }
        else{
            AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
            dlgAlert.setTitle("FlowFree");
            dlgAlert.setCancelable(true);
            dlgAlert.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //dismiss the dialog
                        }
                    });
            dlgAlert.setMessage("You have to finish this level first!");
            dlgAlert.create().show();
        }
    }


}
