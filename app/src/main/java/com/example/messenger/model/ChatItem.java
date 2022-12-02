package com.example.messenger.model;

public class ChatItem {
    private String sender;
    private String receiver;
    private String msg;
    private boolean fromSelf;

    public ChatItem(String sender, String receiver, String msg, boolean fromSelf) {
        this.sender = sender;
        this.receiver = receiver;
        this.msg = msg;
        this.fromSelf = fromSelf;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getMsg() {
        return msg;
    }

    public boolean isFromSelf() {
        return fromSelf;
    }


}
