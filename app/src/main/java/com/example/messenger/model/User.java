package com.example.messenger.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class User implements Serializable {

    public String ID;
    public int port;

    public String name;
    public String email;
    public String password;
    public String image;

    public User() {

    }

    public User(String ID, int port, String name, String email, String password, String image) {
        this.ID = ID;
        this.port = port;
        this.name = name;
        this.email = email;
        this.password = password;
        this.image = image;
    }

    public User(int port) {
        this.port = port;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
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
