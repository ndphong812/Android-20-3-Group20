package com.example.messenger.model;

import java.io.Serializable;

public class Contact implements Serializable {
    private String username;
    private int avatarPath;
    private String latestUserChat;
    private String latestMessage;

    public Contact() {

    }
    public Contact(String username, int avatarPath, String latestUserChat, String latestMessage) {
        this.username = username;
        this.avatarPath = avatarPath;
        this.latestUserChat = latestUserChat;
        this.latestMessage = latestMessage;
    }

    public String getUsername() {
        return username;
    }

    public int getAvatarPath() {
        return avatarPath;
    }

    public String getLatestMessage() {
        return latestMessage;
    }

    public String getLatestUserChat() {
        return latestUserChat;
    }
    public String getLatestMessageWithUser() {
        return  latestUserChat+ ":" + latestMessage;
    }
}
