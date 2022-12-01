package com.example.messenger.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class User implements Serializable {

    public long ID;

    public String name;
    public String email;
    public String password;
    public String image;

    public User() {
    }

    public User(long ID, String name, String email, String password, String image) {
        this.ID = ID;
        this.name = name;
        this.email = email;
        this.password = password;
        this.image = image;
    }

    public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
