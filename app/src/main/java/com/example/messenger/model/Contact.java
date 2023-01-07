package com.example.messenger.model;

import java.io.Serializable;

public class Contact implements Serializable {
    private String id;
    private String username;
    private String avatarPath;
    public Contact() {

    }
    public Contact(String username, String avatarPath) {
        this.username = username;
        this.avatarPath = avatarPath;
    }
    public Contact(String id, String username, String avatarPath) {
        this.id = id;
        this.username = username;
        this.avatarPath = avatarPath;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getUsername() {
        return username;
    }

    public String getAvatarPath() {
        return avatarPath;
    }
}
