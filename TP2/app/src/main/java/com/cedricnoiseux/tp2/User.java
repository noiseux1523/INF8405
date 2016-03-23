package com.cedricnoiseux.tp2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by User on 2016-03-21.
 */
public class User {

    private String email_;
    private Group group_;
    private boolean isAdmin_;
    private List<ActivityType> preferences_;
    private float lastLocationX_;
    private float lastLocationY_;

    private static List<User> allUsers_ = null;

    private User(String email, Group group, boolean isAdmin, List<ActivityType> preferences,
        float lastLocationX, float lastLocationY) {
        email_ = email;
        group_ = group;
        isAdmin_ = isAdmin;
        preferences_ = preferences;
        lastLocationX_ = lastLocationX;
        lastLocationY_ = lastLocationY;
    }

    public String toString() {
        return email_;
    }

    public static User getUser(String email, String groupName, boolean isAdmin,
                        List<String> nomPreferences, float lastLocationX, float lastLocationY) {

        if (allUsers_ == null) {
            String csv = Utility.getAllLines("users.txt");
            csv = csv.trim();
            String [] separated = csv.split("\r?\n");
            allUsers_ = new ArrayList<User>();
            for (String s : separated) {
                String[] elements = s.split(";");
                String e = elements[0];
                Group g = Group.getGroup(elements[1]);
                boolean a = Boolean.parseBoolean(elements[2]);
                int nbPreferences = Integer.parseInt(elements[3]);
                List<ActivityType> act = new ArrayList<ActivityType>();
                for (int i = 0 ; i < nbPreferences ; i++) {
                    act.add(ActivityType.getActivityType(elements[4 + i]));
                }
                float pX = Float.parseFloat(elements[4+nbPreferences]);
                float pY = Float.parseFloat(elements[5+nbPreferences]);
                allUsers_.add(new User(e, g, a, act, pX, pY));
            }
        }

        Group g = Group.getGroup(groupName);
        List<ActivityType> a = new LinkedList<ActivityType>();
        for (String s : nomPreferences) {
            a.add(ActivityType.getActivityType(s));
        }
        boolean exists = false;
        User ret = null;
        for (User u : allUsers_) {
            if (u.email_.equals(email)) {
                exists = true;
                u = ret = new User(email, g, isAdmin, a, lastLocationX, lastLocationY );
            }
        }

        //rendu la, on sait que le groupe n'existe pas. On le cree
        if (!exists) {
            ret = new User(email, g, isAdmin, a, lastLocationX, lastLocationY );
            allUsers_.add(ret);
        }

        String o = "";
        for (User u : allUsers_) {
            o+= u.email_ + ";";
            o+= u.group_.toString() + ";";
            o+= String.valueOf(u.isAdmin_) + ";";
            o+= String.valueOf(u.preferences_.size()) + ";";
            for (ActivityType type : u.preferences_) {
                o += type.toString() + ";";
            }
            o += String.valueOf(u.lastLocationX_) + ";";
            o += String.valueOf(u.lastLocationY_) + "\n";
        }
        try {
            Utility.setAllLines("users.txt", o);
        }
        catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
            return ret;
        }
}
