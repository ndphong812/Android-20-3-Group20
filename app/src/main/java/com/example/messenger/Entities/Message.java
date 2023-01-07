package com.example.messenger.Entities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
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
    public static final int IMAGE_MESSAGE = 2;
    public static final int FILE_MESSAGE = 5;

    private int mType;
    private byte[] byteArray;
    private InetAddress senderAddress;
    private String fileName;
    private long fileSize;
    private String filePath;
    private boolean isMine;


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

    public Bitmap byteArrayToBitmap(byte[] b){
        Log.v(TAG, "Convert byte array to image (bitmap)");
        return BitmapFactory.decodeByteArray(b, 0, b.length);
    }

    public void saveByteArrayToFile(Context context){
        Log.v(TAG, "Save byte array to file");
        if (mType == com.example.messenger.Entities.Message.FILE_MESSAGE) {
            filePath = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/" + fileName;
        }

        File file = new File(filePath);

        if (file.exists()) {
            file.delete();
        }

        try {
            FileOutputStream fos=new FileOutputStream(file.getPath());

            fos.write(byteArray);
            fos.close();
            Log.v(TAG, "Write byte array to file DONE !");
        }
        catch (java.io.IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Write byte array to file FAILED !");
        }
    }
}
