package com.cedricnoiseux.tp2;

import java.util.List;

/**
 * Created by User on 2016-03-21.
 */
public class ActivityType {
    private String name_;
    private static List<ActivityType> allActivityTypes_ = null;

    private ActivityType(String name) {
        name_ = name;
    }

    public static ActivityType getActivityType(String name) {
        if (allActivityTypes_ == null) {
            //todo : aller les chercher dans la base de donnees
        }
        for (ActivityType a : allActivityTypes_) {
            if (a.name_ == name) {
                return a;
            }
        }
        //rendu la, on sait que le groupe n'existe pas. On le cree
        ActivityType newActivityType = new ActivityType(name);
        allActivityTypes_.add(newActivityType);
        //todo : ecrire en memoire
        return newActivityType;
    }
}
