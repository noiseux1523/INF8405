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
    private static Point l1[] = {
            new Point(0, 0), new Point(0, 6),//red
            new Point(1, 0), new Point(1, 6),//blue
            new Point(2, 0), new Point(2, 6),//yellow
            new Point(3, 0), new Point(3, 6),//green
            new Point(4, 0), new Point(4, 6),//magenta
            new Point(5, 0), new Point(5, 6) //cyan
    };
    private static Point l2[] = {
            new Point(0, 6), new Point(1, 1),
            new Point(4, 2), new Point(6, 3),
            new Point(1, 4), new Point(4, 3),
            new Point(0, 3), new Point(4, 4),
            new Point(4, 6), new Point(6, 4),
    };
    private static Point l3[] = {
            new Point(2, 0), new Point(0, 6),
            new Point(1, 0), new Point(1, 6),
            new Point(2, 0), new Point(2, 6),
            new Point(3, 0), new Point(3, 6),
            new Point(4, 0), new Point(4, 6),
    };

    private static Point levels[][] = {l1,l2,l3};


    public Game(int level) {

        basePoints = new Vector<Point>(Arrays.asList(levels[level-1]));
        currentPath = new Vector<Point>();
        drawnPaths = new Vector<Vector<Point>>();
        size = level > 3 ? 8 : 7;
    }

    public void down(int x, int y) {
        currentPath.clear();
        Point p = new Point(x, y);

        Vector<Point> pathToDelete = null;

        //if a drawn path is clicked, the path is deleted
        for (Vector<Point> path:
             drawnPaths) {
            if (path.contains(p)){
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

    public void move(int x, int y) {
        Point p = new Point(x, y);
        //No moving if not initialized
        if (currentPath.isEmpty()) return;
        // the point did not change
        if (currentPath.lastElement().equals(p)) return;
        if (currentPath.contains(p)) {
            //go back one square
            if (currentPath.indexOf(p) == currentPath.size()-2 )
                currentPath.removeElementAt(currentPath.size()-1);
            //collision with line in current path
            else currentPath.clear();
            return;
        }
        //collision with already drawn paths
        for (Vector<Point> path:
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
        if (distance != 1){
            currentPath.clear();
            return;
        }

        currentPath.add(p);
    }

    public boolean finished() {
        return basePoints.size()/2 == drawnPaths.size();
    }

    public boolean isWon() {
        int count = 0;
        for (Vector<Point> path:
             drawnPaths) {
            count += path.size();
        }
        return count == size * size;
    }

    public void up() {
        currentPath.clear();
    }

    public void restart() {
        currentPath.clear();
        drawnPaths.clear();
    }



    public void draw(ImageView image, Context context) {

        Resources res = context.getResources();
        int d = size == 7 ? R.drawable.grid77 : R.drawable.grid88;
        Bitmap bitmap = BitmapFactory.decodeResource(res, d);

        Bitmap tempBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(tempBitmap);

        canvas.drawBitmap(bitmap, 0, 0, null);

        int colors[] = {Color.RED, Color.BLUE, Color.YELLOW, Color.GREEN, Color.MAGENTA, Color.CYAN};
        int squareWidth = canvas.getHeight()/size;
        int bigDotRadius = (int)((double)squareWidth/2);
        int rectangleWidth = (int)((double)squareWidth/4);

        Paint paint = new Paint();
        for (int i = 0 ; i < basePoints.size() ; i+=2) {
            paint.setColor(colors[i / 2]);
            //base points
            for (int j = 0 ; j < 2 ; j++) {
                Point p1 = basePoints.elementAt(i+j);
                canvas.drawCircle(p1.x*squareWidth+squareWidth/2, p1.y*squareWidth+squareWidth/2,
                        bigDotRadius, paint);
            }
            //drawn paths
            boolean broken = false;
            for (Vector<Point> path:
                 drawnPaths) {
                if (path.contains(basePoints.elementAt(i))) {
                    for (int j = 0 ; j < path.size()-1 ; j++) {
                        Point p1 = path.elementAt(j);
                        Point p2 = path.elementAt(j+1);
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
                if (first.equals(basePoints.elementAt(i)) || first.equals(basePoints.elementAt(i+1))) {
                    for (int j = 0 ; j < currentPath.size()-1 ; j++) {
                        Point p1 = currentPath.elementAt(j);
                        Point p2 = currentPath.elementAt(j+1);
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
//

        image.setImageDrawable(new BitmapDrawable(context.getResources(), tempBitmap));

    }

}
