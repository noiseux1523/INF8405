package com.example.cedric.flowfree;

import android.app.Notification;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Pair;
import android.widget.ImageView;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Vector;

/**
 * Created by User on 2016-02-14.
 */
public class Game {
    private Vector<Point> basePoints;
    private Vector<Point> currentPath;
    private Vector<Vector<Point>> drawnPaths;
    private int size;
    private int levelNumber;


    //Création des niveaux;
    private static Point l1[] = {
            new Point(2, 2), new Point(4, 3),//red
            new Point(0, 1), new Point(0, 6),//blue
            new Point(2, 4), new Point(4, 5),//yellow
            new Point(0, 5), new Point(5, 5),//green
            new Point(1, 5), new Point(4, 4), //gray (orange)

    };
    private static Point l2[] = {
            new Point(6, 4), new Point(6, 6), //red
            new Point(0, 5), new Point(6, 3), //blue
            new Point(1, 5), new Point(6, 2), //yellow
            new Point(5, 4), new Point(5, 6), //green
            new Point(3, 5), new Point(5, 2), //gray (orange)
            new Point(1, 1), new Point(2, 5), //cyan
            new Point(2, 2), new Point(5, 1), //magenta (gold)
    };
    private static Point l3[] = {
            new Point(3, 5), new Point(6, 6),
            new Point(0, 5), new Point(3, 4),
            new Point(2, 2), new Point(4, 2),
            new Point(1, 3), new Point(4, 4),
            new Point(1, 5), new Point(4, 5),
            new Point(1, 2), new Point(5, 4),
    };
    private static Point l4[] = {
            new Point(4, 0), new Point(4, 5), //red
            new Point(5, 1), new Point(7, 1), //blue
            new Point(0, 3), new Point(6, 3), //yellow
            new Point(0, 1), new Point(2, 2), //green
            new Point(2, 5), new Point(3, 4), //gray (orange)
            new Point(7, 2), new Point(7, 7), //cyan
            new Point(5, 2), new Point(6, 1), //magenta (gold)
            new Point(0, 0), new Point(0, 2), //bourgogne
            new Point(2, 4), new Point(5, 3), //turquoise
    };
    private static Point l5[] = {
            new Point(1, 6), new Point(3, 4),
            new Point(2, 6), new Point(5, 5),
            new Point(5, 0), new Point(5, 3),
            new Point(6, 1), new Point(6, 3),
            new Point(4, 1), new Point(3, 6),
            new Point(4, 0), new Point(6, 0),
            new Point(2, 2), new Point(2, 4), //turquoise
    };
    private static Point l6[] = {
            new Point(2, 5), new Point(4, 4),
            new Point(1, 1), new Point(2, 6),
            new Point(3, 5), new Point(5, 1),
            new Point(0, 3), new Point(3, 0),
            new Point(3, 1), new Point(4, 3),
            new Point(0, 4), new Point(4, 1),
            new Point(2, 1), new Point(3, 3), //bourgogne
            new Point(4, 5), new Point(5, 2), //turquoise
    };
    
    private static Point levels[][] = {l1, l2, l3, l4, l5, l6};

    //Constructeur
    public Game(int level) {
        basePoints = new Vector<Point>(Arrays.asList(levels[level - 1]));
        currentPath = new Vector<Point>();
        drawnPaths = new Vector<Vector<Point>>();
        size = level > 3 ? 8 : 7;
        levelNumber = level;
    }

    //Retourne le niveau courant
    public int getLevel() {
        return levelNumber;
    }

    //Retourne la taille de la grille
    public int getSize() {
        return size;
    }

    // Quand le doigt est déposé sur la grille
    public void down(int x, int y) {
        currentPath.clear();
        Point p = new Point(x, y);

        Vector<Point> pathToDelete = null;

        //if a drawn path is clicked, the path is deleted
        for (Vector<Point> path :
                drawnPaths) {
            if (path.contains(p)) {
                pathToDelete = path;
                break;
            }
        }

        if (pathToDelete != null) {
            drawnPaths.remove(pathToDelete);
            return;
        }

        if (basePoints.contains(p)) currentPath.add(p);
    }

    //Le doigt en contact avec la grille se déplace
    public void move(int x, int y) {
        Point p = new Point(x, y);

        //No moving if not initialized
        if (currentPath.isEmpty()) return;

        // the point did not change
        if (currentPath.lastElement().equals(p)) return;

        if (currentPath.contains(p)) {
            //go back one square
            if (currentPath.indexOf(p) == currentPath.size() - 2)
                currentPath.removeElementAt(currentPath.size() - 1);
                //collision with line in current path
            else currentPath.clear();
            return;
        }

        //collision with already drawn paths
        for (Vector<Point> path :
                drawnPaths) {
            if (path.contains(p)) {
                currentPath.clear();
                return;
            }
        }

        //collision with a base point
        if (basePoints.contains(p)) {
            int first = basePoints.indexOf(currentPath.firstElement());
            int second = basePoints.indexOf(p);
            int max = Math.max(first, second);
            int min = Math.min(first, second);
            if (min % 2 == 0 && max - min == 1) {
                currentPath.add(p);
                drawnPaths.add(new Vector<Point>(currentPath));
            }
            currentPath.clear();
            return;
        }

        //if not adjacent to previous
        int distance = Math.abs(p.x - currentPath.lastElement().x) + Math.abs(p.y - currentPath.lastElement().y);
        if (distance != 1) {
            currentPath.clear();
            return;
        }

        currentPath.add(p);
    }

    //Vérification de si la partie est terminée ou pas
    public boolean finished() {
        return basePoints.size() / 2 == drawnPaths.size();
    }

    //Vérification de si la parite est gagnée
    public boolean isWon() {
        int count = 0;

        for (Vector<Point> path :
                drawnPaths) {
            count += path.size();
        }

        return count == size * size;
    }

    //Le doigt de l'utilisateur se lève
    public void up() {
        currentPath.clear();
    }

    //Recommencer la partie
    public void restart() {
        currentPath.clear();
        drawnPaths.clear();
    }

    //Affichage de la grille de jeu
    public void draw(ImageView image, Context context) {

        Resources res = context.getResources();
        int d = size == 7 ? R.drawable.grid77 : R.drawable.grid88;
        Bitmap bitmap = BitmapFactory.decodeResource(res, d);

        Bitmap tempBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(tempBitmap);

        canvas.drawBitmap(bitmap, 0, 0, null);

        int colors[] = {Color.RED, Color.BLUE, Color.YELLOW, Color.GREEN, Color.DKGRAY, Color.CYAN, Color.MAGENTA, Color.WHITE, Color.LTGRAY};
        int squareWidth = canvas.getHeight() / size;
        int bigDotRadius = (int) ((double) squareWidth / 2.25);
        int rectangleWidth = (int) ((double) squareWidth / 4);

        Paint paint = new Paint();
        for (int i = 0; i < basePoints.size(); i += 2) {
            paint.setColor(colors[i / 2]);

            //base points
            for (int j = 0; j < 2; j++) {
                Point p1 = basePoints.elementAt(i + j);
                canvas.drawCircle(p1.x * squareWidth + squareWidth / 2, p1.y * squareWidth + squareWidth / 2,
                        bigDotRadius, paint);
            }

            //drawn paths
            boolean broken = false;
            for (Vector<Point> path :
                    drawnPaths) {
                if (path.contains(basePoints.elementAt(i))) {
                    for (int j = 0; j < path.size() - 1; j++) {
                        Point p1 = path.elementAt(j);
                        Point p2 = path.elementAt(j + 1);
                        canvas.drawRect(
                                Math.min(p1.x, p2.x) * squareWidth + squareWidth / 2 - rectangleWidth / 2,
                                Math.max(p1.y, p2.y) * squareWidth + squareWidth / 2 + rectangleWidth / 2,
                                Math.max(p1.x, p2.x) * squareWidth + squareWidth / 2 + rectangleWidth / 2,
                                Math.min(p1.y, p2.y) * squareWidth + squareWidth / 2 - rectangleWidth / 2,
                                paint);
                    }
                    broken = true;
                    break;
                }
            }

            if (!broken && !currentPath.isEmpty()) {
                Point first = currentPath.firstElement();
                if (first.equals(basePoints.elementAt(i)) || first.equals(basePoints.elementAt(i + 1))) {
                    for (int j = 0; j < currentPath.size() - 1; j++) {
                        Point p1 = currentPath.elementAt(j);
                        Point p2 = currentPath.elementAt(j + 1);
                        canvas.drawRect(
                                Math.min(p1.x, p2.x) * squareWidth + squareWidth / 2 - rectangleWidth / 2,
                                Math.max(p1.y, p2.y) * squareWidth + squareWidth / 2 + rectangleWidth / 2,
                                Math.max(p1.x, p2.x) * squareWidth + squareWidth / 2 + rectangleWidth / 2,
                                Math.min(p1.y, p2.y) * squareWidth + squareWidth / 2 - rectangleWidth / 2,
                                paint);
                    }
                }
            }
        }

        image.setImageDrawable(new BitmapDrawable(context.getResources(), tempBitmap));
    }
}
