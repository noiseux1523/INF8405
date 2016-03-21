package com.cedricnoiseux.tp2;

import java.util.List;

/**
 * Created by User on 2016-03-21.
 */
public class Event {
    private String name_;
    private Group group_;
    private static List<Event> allEvents_ = null;

    private Event(String name, Group group) {
        name_ = name;
        group_ = group;
    }

    public static Event getEvent(String name, String groupName) {
        if (allEvents_ == null) {
            //todo : aller les chercher dans la base de donnees
        }
        Group g = Group.getGroup(groupName);
        for (Event e : allEvents_) {
            if (e.name_ == name && e.group_ == g) {
                return e;
            }
        }
        //rendu la, on sait que le groupe n'existe pas. On le cree
        Event newEvent = new Event(name, g);
        allEvents_.add(newEvent);
        //todo : ecrire en memoire
        return newEvent;
    }
}
