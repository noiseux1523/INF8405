package com.cedricnoiseux.tp2;

import java.io.IOException;
import java.util.ArrayList;
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

    public String toString() { return name_;}

    public static ActivityType getActivityType(String name) {
            if (allActivityTypes_ == null) {
                //todo : aller les chercher dans la base de donnees
                String csv = Utility.getAllLines("activitytypes.txt");
                csv = csv.trim();
                String[] separated = csv.split(";");
                allActivityTypes_ = new ArrayList<ActivityType>();
                for (String s : separated) {
                    allActivityTypes_.add(new ActivityType(s));
                }
            }
            System.out.println("Reading activities");
            for (ActivityType a : allActivityTypes_) {
                System.out.println(a.name_);
                if (a.name_.equals(name)) {
                    return a;
                }
            }
            //rendu la, on sait que le groupe n'existe pas. On le cree
            ActivityType newActivityType = new ActivityType(name);
            allActivityTypes_.add(newActivityType);
            String fileOut = "";
            for (ActivityType a : allActivityTypes_) {
                fileOut += a.name_ + ";";
            }
            fileOut = fileOut.substring(0, fileOut.length() - 1);
            try {
                Utility.setAllLines("activitytypes.txt", fileOut);
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
            return newActivityType;
        }
    }
