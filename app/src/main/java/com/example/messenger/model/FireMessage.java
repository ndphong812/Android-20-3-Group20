package com.example.messenger.model;


public class FireMessage  {
    public int type;
    public String fromMail;
    public String toMail;
    public String message;
    public String sentDate;
    public boolean isMine;

    public FireMessage() {
    }

    public FireMessage(int type, String fromMail, String toMail, String message, String sentDate, Boolean IsMine) {
        this.fromMail = fromMail;
        this.toMail = toMail;
        this.message = message;
        this.sentDate = sentDate;
        this.isMine = IsMine;
        this.type = type;
    }

    public int getmType() {
        return type;
    }

    public void setmType(int mType) {
        this.type = mType;
    }

    public String getFromMail() {
        return fromMail;
    }

    public void setFromMail(String fromMail) {
        this.fromMail = fromMail;
    }

    public String getToMail() {
        return this.toMail;
    }

    public void setToMail(String toMail) {
        this.toMail = toMail;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSentDate() {
        return this.sentDate;
    }

    public void setSentDate(String sentDate) {
        this.sentDate = sentDate;
    }

    public boolean isMine() {
        return isMine;
    }

    public void setMine(boolean mine) {
        isMine = mine;
    }
}
