package com.example.ronald.fetchme.models;

/**
 * Created by ronald on 10/18/2017.
 */

        import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {

    public String username;
    public String email;
    public String photo_url;
    public String uid;

    public User()
    {

    }

    public User(String username, String email, String photo_url, String uid) {
        this.username = username;
        this.email = email;
        this.photo_url = photo_url;
        this.uid = uid;
    }

}