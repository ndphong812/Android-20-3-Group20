package com.example.messenger.model;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class FireMessage  {
    public int type;
    public String fromMail;
    public String toMail;
    public String message;
    public String sentDate;
    public boolean isMine;


    private String filePath;
    private byte[] byteArray;
    private String fileName;
    private long fileSize;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public byte[] getByteArray() {
        return byteArray;
    }

    public void setByteArray(byte[] byteArray) {
        this.byteArray = byteArray;
    }

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

    public Bitmap byteArrayToBitmap(byte[] b){
//        Log.v(TAG, "Convert byte array to image (bitmap)");
        return BitmapFactory.decodeByteArray(b, 0, b.length);
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
