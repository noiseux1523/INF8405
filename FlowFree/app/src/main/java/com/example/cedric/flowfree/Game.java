package com.example.cedric.flowfree;

import android.graphics.Point;
import android.util.Pair;

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

    public Game(int level) {
        Point bases[] = {new Point(0, 0), new Point(4, 4)};
        basePoints = new Vector<Point>(Arrays.asList(bases));
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
                drawnPaths.add(currentPath);
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

    public boolean gameWon() {
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

}
