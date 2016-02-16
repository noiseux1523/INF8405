package com.example.cedric.flowfree;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import static java.lang.Math.floor;

public class Level extends MainActivity {

    TextView header = null;
    ImageView mainImageView = null;

    public static int maxLevelAllowed7x7 = 1;
    public static int maxLevelAllowed8x8 = 1;

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
                handleTouch(event);
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

    private void handleTouch(MotionEvent m){

        int action = m.getActionMasked();
        int columns = g.getLevel() > 3 ? 8 : 7;
        int boxWidth = mainImageView.getWidth()/columns;///myLayout.getRowCount();
        int idX = (int)floor(m.getX(0) / boxWidth);
        int idY = (int)floor(m.getY(0) / boxWidth);
        if (idX < 0 || idY < 0 || idX >= columns || idY >= columns) return;

        switch (action) {
                case MotionEvent.ACTION_DOWN:
                    g.down(idX, idY) ;
                    break;
                case MotionEvent.ACTION_UP:
                    g.up();
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    break;
                case MotionEvent.ACTION_MOVE: // 25 EST UN CHIFFRE ARBITRAIRE, carré sont 150x150
                    g.move(idX, idY) ;
                    break;
                default:
        }

        g.draw(mainImageView, getApplicationContext());

        if (g.finished()) {
            AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
            dlgAlert.setTitle("FlowFree");
            dlgAlert.setCancelable(true);

            if (g.isWon()) {
                if (g.getLevel() == 6) {
                    dlgAlert.setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    //dismiss the dialog
                                }
                            });
                    dlgAlert.setMessage("You won! You finished all levels");
                }
                else {
                    dlgAlert.setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    g = new Game(g.getLevel() + 1);
                                    g.draw(mainImageView, getApplicationContext());
                                    if (g.getLevel() < 4){
                                        maxLevelAllowed7x7 = Math.max(maxLevelAllowed7x7, g.getLevel());
                                    }
                                    else {
                                        maxLevelAllowed8x8 = Math.max(maxLevelAllowed8x8, g.getLevel() - 3);
                                    }
                                    updateHeader();
                                }
                            });
                    dlgAlert.setNeutralButton("Retry",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //dismiss the dialog
                                }
                            });
                    dlgAlert.setMessage("You won! Try this next level :)");
                }
            }
            else {
                dlgAlert.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //dismiss the dialog
                            }
                        });
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

        if (g.getLevel() < 4){
            header.setText(size + "x" + size + "              Level " + level);
        }
        else {
            header.setText(size + "x" + size + "              Level " + (level - 3));
        }
    }

    protected void retry(){
        g.restart();
        g.draw(mainImageView, getApplicationContext());
    }

    private void back(int level){
        if (level == 1) {
            super.onBackPressed();
        }
        else if (level == 4 && maxLevelAllowed7x7 < 3) {
            AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
            dlgAlert.setTitle("FlowFree");
            dlgAlert.setCancelable(true);
            dlgAlert.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //dismiss the dialog
                        }
                    });
            dlgAlert.setMessage("You have to complete the 7x7 grid levels first!");
            dlgAlert.create().show();
        }
        else  {
            g = new Game(level - 1);
            g.draw(mainImageView, getApplicationContext());
        }
        updateHeader();
    }

    private void next() {
        if ((g.getLevel() < 3 && g.getLevel() + 1 <= maxLevelAllowed7x7)
                || (g.getLevel() > 2 && g.getLevel() < 6 && ((g.getLevel() + 1) - 3) <= maxLevelAllowed8x8)) {
            g = new Game(g.getLevel() + 1);
            g.draw(mainImageView, getApplicationContext());
        }
        else if (g.getLevel() == 6) {
            AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
            dlgAlert.setTitle("FlowFree");
            dlgAlert.setCancelable(true);
            dlgAlert.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //dismiss the dialog
                        }
                    });
            dlgAlert.setMessage("You completed the game, no more levels are available!");
            dlgAlert.create().show();
        }
        else {
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
