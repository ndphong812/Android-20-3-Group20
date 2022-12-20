package com.example.messenger.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class User implements Serializable {

    public long ID;
    public int port;

    public String name;
    public String email;
    public String password;
    public String image;
    public String ipAddress;

    public User() {
    }

    public User(long ID, int port, String name, String email, String password, String image, String ipAddress) {
        this.ID = ID;
        this.port = port;
        this.name = name;
        this.email = email;
        this.password = password;
        this.image = image;
        this.ipAddress = ipAddress;
    }

    public User(String ipAddress, int port) {
        this.ipAddress = ipAddress;
        this.port = port;
    }

    public long getID() {
        return ID;
    }

    public void setID(long ID) {
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
