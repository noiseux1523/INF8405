package com.cedricnoiseux.projetfinal;

import java.util.Date;

/**
 * Created by User on 2016-03-31.
 */
public class Event {
    public int id;
    public String name;
    public String host;
    public String locationName;
    public Date date;
    public double locX;
    public double locY;

    // id = -1 when creating new event
    public Event(int i, String n, String h, String lN, java.util.Date d, double x, double y) {
        id = i;
        name = n;
        host = h;
        locationName = lN;
        date = d;
        locX = x;
        locY = y;
    }

    public String toSqlString() {
        return "('" + name + "', '" +
                host + "', '" +
                locationName + "', '" +
                SqlUtility.sdf.format(date) + "', " +
                Double.valueOf(locX).toString() + ", " +
                Double.valueOf(locY).toString() + ")";
    }

}
