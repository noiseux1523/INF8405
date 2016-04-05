package com.cedricnoiseux.projetfinal;

/**
 * Created by User on 2016-03-31.
 */
public class User {
    public String email;
    public float lastX;
    public float lastY;

    public User(String username) {
        email = username;
        lastX = 0;
        lastY = 0;
    }

}
