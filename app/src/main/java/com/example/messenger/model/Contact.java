package com.example.messenger.model;

import java.io.Serializable;

public class Contact implements Serializable {
    private String username;
    private String avatarPath;
    private String latestUserChat;
    private String latestMessage;

    public Contact() {

    }
    public Contact(String username, String avatarPath, String latestUserChat, String latestMessage) {
        this.username = username;
        this.avatarPath = avatarPath;
        this.latestUserChat = latestUserChat;
        this.latestMessage = latestMessage;
    }

    public String getUsername() {
        return username;
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    public String getLatestMessage() {
        return latestMessage;
    }

    public String getLatestUserChat() {
        return latestUserChat;
    }
}
