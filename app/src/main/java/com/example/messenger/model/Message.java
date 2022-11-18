package com.example.messenger.model;

public class Message {
    public String FromMail;
    public String ToMail;
    public String Message;
    public String SentDate;
    public Boolean fromSelf;
    public int rowid;

    public Message() {
    }

    public Message( int rowid, String fromMail, String toMail, String message, String sentDate, Boolean FromSelf) {
        FromMail = fromMail;
        ToMail = toMail;
        Message = message;
        SentDate = sentDate;
        fromSelf = FromSelf;
        this.rowid = rowid;
    }

    public String getFromMail() {
        return FromMail;
    }

    public void setFromMail(String fromMail) {
        FromMail = fromMail;
    }

    public String getToMail() {
        return ToMail;
    }

    public void setToMail(String toMail) {
        ToMail = toMail;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getSentDate() {
        return SentDate;
    }

    public void setSentDate(String sentDate) {
        SentDate = sentDate;
    }

    public Boolean getFromSelf() {
        return fromSelf;
    }

    public void setFromSelf(Boolean fromSelf) {
        this.fromSelf = fromSelf;
    }

    public int getRowid() {
        return rowid;
    }

    public void setRowid(int rowid) {
        this.rowid = rowid;
    }
}
