package com.example.ronald.fetchme.models;

/**
 * Created by ronald on 10/18/2017.
 */

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {

    public String username;
    public String email;

    public User()
    {

    }

    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }

}