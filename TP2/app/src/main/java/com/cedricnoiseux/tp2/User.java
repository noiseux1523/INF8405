package com.cedricnoiseux.tp2;

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

    public User getUser(String email, String groupName, boolean isAdmin,
                        List<String> nomPreferences, int lastLocationX, int lastLocationY) {
        if (allUsers_ == null) {
            //todo : aller les chercher dans la base de donnees
        }

        for (User u : allUsers_) {
            if (u.email_ == email) {
                return u;
            }
        }
        //rendu la, on sait que le groupe n'existe pas. On le cree
        Group g = Group.getGroup(groupName);
        List<ActivityType> a = new LinkedList<ActivityType>();
        for (String s : nomPreferences) {
            a.add(ActivityType.getActivityType(s));
        }
        User newUser = new User(email, g, isAdmin, a, lastLocationX, lastLocationY );
        allUsers_.add(newUser);
        //todo : ecrire en memoire
        return newUser;
    }



}
