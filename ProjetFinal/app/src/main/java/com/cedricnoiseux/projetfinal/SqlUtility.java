package com.cedricnoiseux.projetfinal;

import java.util.LinkedList;

/**
 * Created by User on 2016-03-31.
 */
public class SqlUtility {
    public static String userName = "i1104927_wp1";
    public static String password = "y117746q";

    public static void addParticipation(User u, Event e) {

    }

    public static void removeParticipation(User u, Event e) {

    }

    public static void addEvent(Event e) throws Exception{
        if (e.id != -1) {
            throw new Exception("This event is not new");
        }
        AddEvent process = new AddEvent();
        process.execute(e);
    }
    public static void updateEvent(Event e) {

    }

    public static void removeEvent(Event e) {

    }

    public static LinkedList<Event> getAllEvents() {
        LinkedList<Event> allEvents = new LinkedList<Event>();
        return allEvents;
    }

    public static LinkedList<Event> getParticipatingEvent(User u) {
        LinkedList<Event> allEvents = new LinkedList<Event>();
        return allEvents;
    }

    public static LinkedList<Event> getCreatedEvents(User u) {
        LinkedList<Event> allEvents = new LinkedList<Event>();
        return allEvents;
    }
}
