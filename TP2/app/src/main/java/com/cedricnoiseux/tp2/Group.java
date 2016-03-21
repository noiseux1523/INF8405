package com.cedricnoiseux.tp2;

import java.util.List;

/**
 * Created by User on 2016-03-21.
 */
public class Group {
    private String name_;
    private static List<Group> allGroups_ = null;

    private Group(String name) {
        name_ = name;
    }

    public static Group getGroup(String name) {
        if (allGroups_ == null) {
            //todo : aller les chercher dans la base de donnees
        }
        for (Group g : allGroups_) {
            if (g.name_ == name) {
                return g;
            }
        }
        //rendu la, on sait que le groupe n'existe pas. On le cree
        Group newGroup = new Group(name);
        allGroups_.add(newGroup);
        //todo : ecrire en memoire
        return newGroup;
    }
}
