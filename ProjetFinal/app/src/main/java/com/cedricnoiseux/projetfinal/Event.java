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
    public float locX;
    public float locY;


    public Event(int i, String n,String h, String lN, java.sql.Date d, float x, float y) {
        id = i;
        name = n;
        host = h;
        locationName = lN;
        date = new Date(d.getTime());
        locX = x;
        locY = y;
    }

}
