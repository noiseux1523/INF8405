package com.cedricnoiseux.projetfinal;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Created by User on 2016-03-31.
 */
public class SqlUtility {
    public static String userName = "i1104927_wp1";
    public static String password = "y117746q";
    public static java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static void addParticipation(User u, Event e) {
        String q = "INSERT INTO Participations (USERNAME, EVENTID) " +
                "VALUES ('" + u.email + "', " + Integer.valueOf(e.id).toString() + ")";
        SqlSet process = new SqlSet();
        process.execute(q);
    }

    public static void removeParticipation(User u, Event e) {
        String q = "DELETE FROM Participations WHERE " +
                "USERNAME = '" + u.email + "' AND " +
                "EVENTID = " + Integer.valueOf(e.id).toString() + ")";
        SqlSet process= new SqlSet();
        process.execute(q);
    }

    public static void addEvent(Event e) throws Exception { //S'ASSURER QUE L'EVENEMENT AIT UN ID DE -1
        if (e.id != -1) {
            throw new Exception("This event is not new");
        }

        String q = "INSERT INTO Events (NAME, HOST, LOCATIONNAME, DATE, POSITIONX, POSITIONY) " +
                "VALUES " + e.toSqlString();
        SqlSet process= new SqlSet();
        process.execute(q);
    }
    public static void updateEvent(Event e) {
        String q = "UPDATE Events SET " +
                "Name = '" + e.name +
                "', LOCATIONNAME = '" + e.locationName +
                "', DATE = '" + sdf.format(e.date) +
                "', POSITIONX = " + Double.valueOf(e.locX).toString() +
                ", POSITIONY = " + Double.valueOf(e.locY).toString() +
                "WHERE ID = " + Integer.valueOf(e.id).toString();
        SqlSet process= new SqlSet();
        process.execute(q);
    }

    public static void removeEvent(Event e) {
        String q = "DELETE FROM Events WHERE ID = " + Integer.valueOf(e.id).toString();
        SqlSet process= new SqlSet();
        process.execute(q);
    }

    public static LinkedList<Event> getAllEvents() throws Exception {
        String q = "SELECT ID, NAME, HOST, LOCATIONNAME, DATE, POSITIONX, POSITIONY FROM Events";
        SqlGet process = new SqlGet();
        process.execute(q);
        return process.get();
    }

    public static LinkedList<Event> getParticipatingEvent(User u) throws Exception{
        String q = "SELECT ID, NAME, HOST, LOCATIONNAME, DATE, POSITIONX, POSITIONY " +
                "FROM Events, Participations WHERE USERNAME = '" + u.email + "' AND EVENTID = ID";
        SqlGet process = new SqlGet();
        process.execute(q);
        return process.get();
    }

    public static LinkedList<Event> getCreatedEvents(User u) throws Exception{
        String q = "SELECT ID, NAME, HOST, LOCATIONNAME, DATE, POSITIONX, POSITIONY " +
                "FROM Events WHERE HOST = '" +u.email + "'";
        SqlGet process = new SqlGet();
        process.execute(q);
        return process.get();
    }

    public static LinkedList<Event> phpStringToEvents(String s) throws Exception {
        LinkedList<Event> ret = new LinkedList<Event>();
        String[] eventStrings = s.split("~");
        for (String eventString : eventStrings) {
            String[] tmp = eventString.split(";");
            if (tmp.length != 7) return ret;
            Event newEvent = new Event(Integer.parseInt(tmp[0]), tmp[1], tmp[2], tmp[3], sdf.parse(tmp[4]), Double.parseDouble(tmp[5]), Double.parseDouble(tmp[6]));
            ret.add(newEvent);
        }
        return ret;
    }

    private static void printEvents(LinkedList<Event> l) {
        for (int i = 0; i < l.size(); i++) {
            Event o =  l.get(i);
            System.out.println(o.name + " " + o.host);
        }
    }

    public static void runTest() {
        User user1 = new User("user1", 0, 0);
        User user2 = new User("user2", 0, 0);
        User user3 = new User("user3", 0, 0);
        User user4 = new User("user4", 0, 0);



        try {

            LinkedList<Event> l1 = getAllEvents();
            Event e1 = l1.get(0);
            Event e2 = l1.get(1);
            Event e3 = l1.get(2);
            Event e4 = l1.get(3);
            Event e5 = l1.getLast();
            removeEvent(e1);
            removeEvent(e5);



            LinkedList<Event> l2 = getCreatedEvents(user1);
            LinkedList<Event> l3 = getParticipatingEvent(user2);
            System.out.println("all events");
            printEvents(l1);
            System.out.println("all events created by user1");
            printEvents(l2);
            System.out.println("all events user2 participates to");
            printEvents(l3);
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}
