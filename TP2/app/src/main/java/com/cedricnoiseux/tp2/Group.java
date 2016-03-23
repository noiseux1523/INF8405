package com.cedricnoiseux.tp2;

import java.io.IOException;
import java.util.ArrayList;
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

    public String toString() {
        return name_;
    }

    public static Group getGroup(String name) {
        if (allGroups_ == null) {
            String csv = Utility.getAllLines("groups.txt");
            csv = csv.trim();
            String [] separated = csv.split(";");
            allGroups_ = new ArrayList<Group>();
            for (String s : separated) {
                allGroups_.add(new Group(s));
            }
        }
        System.out.println("getting groups");
        for (Group g : allGroups_) {
            System.out.println(g.name_);
            if (g.name_.equals(name)) {
                return g;
            }
        }
        //rendu la, on sait que le groupe n'existe pas. On le cree
        Group newGroup = new Group(name);
        allGroups_.add(newGroup);
        String fileOut = "";
        for (Group g : allGroups_) {
            fileOut += g.name_ + ";";
        }
        fileOut =fileOut.substring(0, fileOut.length()-1);
        try {
        Utility.setAllLines("groups.txt", fileOut);
        }
        catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return newGroup;
    }
}
