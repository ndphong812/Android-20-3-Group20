package com.example.messenger.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {

    public String id;
    public int port;

    public String name;
    public String email;
    public String password;
    public String image;
    public Boolean isLogined;
    public List<String> friends;
    public List<String> blocks;
    public User() {

    }

    public User(String ID, int port, String name, String email, String password, String image, Boolean isLogined) {
        this.id = ID;
        this.port = port;
        this.name = name;
        this.email = email;
        this.password = password;
        this.image = image;
        this.isLogined = isLogined;
        this.friends = new ArrayList<>();
        this.blocks = new ArrayList<>();
    }

    public void setFriends(List<String> friends) {
        this.friends = friends;
    }

    public List<String> getFriends() {
        return friends;
    }

    public void pushFriends(String newFriendID) {
        this.friends.add(newFriendID);
    }

    public void setBlocks(List<String> blocks) {
        this.blocks = blocks;
    }

    public List<String> getBlocks() {
        return blocks;
    }

    public void pushBlocks(String newBlockId) {
        this.blocks.add(newBlockId);
    }

    public User(int port) {
        this.port = port;
    }

    public String getID() {
        return id;
    }

    public void setID(String ID) {
        this.id = ID;
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

    public Boolean getIsLogined() {
        return isLogined;
    }

    public void setIsLogined(Boolean isLogined) {
        this.isLogined = isLogined;
    }
}
