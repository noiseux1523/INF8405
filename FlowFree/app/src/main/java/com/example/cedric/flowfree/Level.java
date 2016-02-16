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

    TextView header = null;
    ImageView mainImageView = null;
    Canvas canvas = null;


    double[] position = {0,0,0,0,0,0};

    private static int maxLevelAllowed = 1;

    Game g = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        final int level = extras.getInt("level");

        header = (TextView) findViewById(R.id.textLevel);
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
                next();
            }
        });
        g = new Game(level);
        g.draw(mainImageView, getApplicationContext());
        updateHeader();
    }

    private void handleTouch(MotionEvent m, double [] position){

        TextView textView1 = (TextView)findViewById(R.id.textView1);

        int pointerCount = m.getPointerCount();


        double x = (int) m.getX(0);
        double y = (int) m.getY(0);
        int action = m.getActionMasked();
        int id = m.getPointerId(0);
        int actionIndex = m.getActionIndex();
        String actionString;


        int columns = g.getLevel() > 3 ? 8 : 7;
        int boxWidth = mainImageView.getWidth()/columns;///myLayout.getRowCount();
        int idX = (int)floor(m.getX(0) / boxWidth);
        int idY = (int)floor(m.getY(0) / boxWidth);
        if (idX < 0 || idY < 0 || idX >= columns || idY >= columns) return;

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
                if (g.getLevel() == 6) {
                    dlgAlert.setMessage("You won! You finished all levels");
                }
                else {
                    dlgAlert.setMessage("You won! Try this next level :)");
                    g = new Game(g.getLevel() + 1);
                    g.draw(mainImageView, getApplicationContext());
                    maxLevelAllowed = Math.max(maxLevelAllowed, g.getLevel());
                }
            }
            else {
                dlgAlert.setMessage("You lost... :(");
            }

            dlgAlert.create().show();
            g.restart();
        }
        updateHeader();
    }

    private void updateHeader() {
        int level = g.getLevel();
        int size = g.getSize();
        header.setText(size + "x" + size + "              Level " + level);
    }

    protected void retry(){

        g.restart();
        g.draw(mainImageView, getApplicationContext());
    }

    private void back(int level){
        if (level == 1) {
            super.onBackPressed();
        }
        else {
            g = new Game(level - 1);
            g.draw(mainImageView, getApplicationContext());
        }
        updateHeader();
    }

    private void next() {
        if (g.getLevel() + 1 <= maxLevelAllowed){
            g = new Game(g.getLevel() + 1);
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
        updateHeader();
    }


}
