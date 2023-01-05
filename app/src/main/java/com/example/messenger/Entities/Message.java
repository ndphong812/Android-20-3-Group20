package com.example.messenger.Entities;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.ArrayList;

public class Message implements Serializable {
    public String FromMail;
    public String ToMail;
    public String Message;
    public String SentDate;

    private static final String TAG = "Message";
    public static final int TEXT_MESSAGE = 1;

    private int mType;
    private String mText;
    private String chatName;
    private byte[] byteArray;
    private InetAddress senderAddress;
    private String fileName;
    private long fileSize;
    private String filePath;
    private boolean isMine;

    public Message(int type, String fromMail, String toMail, String message, String sentDate, Boolean IsMine, InetAddress sender) {
        FromMail = fromMail;
        ToMail = toMail;
        Message = message;
        SentDate = sentDate;
        isMine = IsMine;
        mType = type;
        senderAddress = sender;
    }

    public Message(int type, String fromMail, String toMail, String message, String sentDate, Boolean IsMine) {
        FromMail = fromMail;
        ToMail = toMail;
        Message = message;
        SentDate = sentDate;
        isMine = IsMine;
        mType = type;
    }

    public int getmType() { return mType; }
    public void setmType(int mType) { this.mType = mType; }
    public String getmText() { return mText; }
    public void setmText(String mText) { this.mText = mText; }
    public String getChatName() { return chatName; }
    public void setChatName(String chatName) { this.chatName = chatName; }
    public byte[] getByteArray() { return byteArray; }
    public void setByteArray(byte[] byteArray) { this.byteArray = byteArray; }
    public InetAddress getSenderAddress() { return senderAddress; }
    public void setSenderAddress(InetAddress senderAddress) { this.senderAddress = senderAddress; }
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public long getFileSize() { return fileSize; }
    public void setFileSize(long fileSize) { this.fileSize = fileSize; }
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    public boolean isMine() { return isMine; }
    public void setMine(boolean isMine) { this.isMine = isMine; }

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
}
