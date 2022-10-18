package com.example.messenger;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;

import java.security.SecureRandom;
import java.security.spec.KeySpec;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class DBLogin extends SQLiteOpenHelper {
    public static final String DBNAME = "Login.db";

    public DBLogin(Context context) {
        super(context, DBNAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase MyDB) {
        MyDB.execSQL("create Table users(email TEXT primary key, password TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase MyDB, int i, int i1) {
        MyDB.execSQL("drop Table if exists users");
    }

    public Boolean insertData(String email, String password) {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("email", email);
        contentValues.put("password", password);
        long result = MyDB.insert("users", null, contentValues);
        return result != -1;
    }


    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public Boolean isValidPassword(String pass) {
        return pass.length() > 6;
    }


    public Boolean checkEmailFormat(String email) {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor cursor = MyDB.rawQuery("Select * from users where email=?", new String[]{email});
        return isValidEmail(email) && cursor.getCount() > 0;
    }

    public Boolean checkusernamepassword(String email, String password) {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor cursor = MyDB.rawQuery("Select * from users where email = ? and password = ?", new String[]{email, password});
        return cursor.getCount() > 0;
    }
}




//    public Boolean forgotPassword(String email) {
//        SQLiteDatabase MyDB = this.getWritableDatabase();
//        Cursor cursor = MyDB.query("users",null, email + "=?",new String[]{email},null,null,null);
//        int rowCount = cursor.getCount();
//        cursor.close();
//        if (rowCount != 1) return false;
//        SaltandHash sah = new SaltandHash("123");
//        ContentValues cv = new ContentValues();
//        cv.put("salt",sah.getSalt());
//        cv.put("hash",sah.getHash());
//        int result = MyDB.update("users",cv, "email" +"=?",new String[]{email});
//        if (result != 1) return false;
//        Log.d("NEWPASSWORD","A new password has been sent to " + email + " the password is " + "123");
//        // SEND THE EMAIL FROM HERE
//        return true;
//    }
//
//    private class SaltandHash {
//        private byte[] salt;
//        private byte[] hash;
//        private boolean worked = true;
//
//        SaltandHash(String password) {
//            this.salt = new SecureRandom().generateSeed(32);
//            KeySpec ks = new PBEKeySpec(
//                    password.toCharArray(),
//                    salt,
//                    65536,
//                    128
//            );
//            try {
//                SecretKeyFactory skf =
//                        SecretKeyFactory.getInstance(
//                                "PBKDF2WithHmacSHA1"
//                        );
//                hash = skf.generateSecret(ks).getEncoded();
//            } catch (Exception e) {
//                worked = false;
//            }
//        }
//
//        private String getHash(){
//            return android.util.Base64.encodeToString(this.hash, android.util.Base64.DEFAULT);
//        }
//
//        private String getSalt() {
//            return android.util.Base64.encodeToString(this.salt, android.util.Base64.DEFAULT);
//        }
//
//        private boolean ifSaltandHashOK() {
//            return worked;
//        }
//    }
//}

