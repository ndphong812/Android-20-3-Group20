package com.example.messenger.Database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Patterns;

import com.example.messenger.Entities.Message;
import com.example.messenger.Services.PreferenceManager;
import com.example.messenger.model.User;

import java.util.ArrayList;
import java.util.List;

public class DataContext extends SQLiteOpenHelper {
    public static final String DBNAME = "Messengers.db";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_IMAGE = "image";
    PreferenceManager shp;
    String DBPATH;
    Context ctx;
    private static final int DB_VERSION = 3;

    public DataContext(Context context) {

        super(context, DBNAME, null, 1);
        // DB_VERSION is an int,update it every new build

        this.ctx = context;
        this.DBPATH = this.ctx.getDatabasePath(DBNAME).getAbsolutePath();
//        this.DBPATH = context.getDatabasePath(DataContext.DBNAME).toString();
//        Log.e("Path 1", DBPATH);

    }

    @Override
    public void onCreate(SQLiteDatabase MyDB) {
        String users = "create Table users(email TEXT primary key, name TEXT, password TEXT, image TEXT)";
        String messages = "create Table messages(FromMail text, ToMail text, Message text, SentDate text, image TEXT)";
        shp = new PreferenceManager(ctx.getApplicationContext());
        MyDB.execSQL(users);
        MyDB.execSQL(messages);
    }

    @Override
    public void onUpgrade(SQLiteDatabase MyDB, int i, int i1) {
        String dropUsers = "drop table if exists users; ";
        String dropMessages = "drop table if exists messages;";
        MyDB.execSQL(dropUsers);
        MyDB.execSQL(dropMessages);
        onCreate(MyDB);
    }

//    @Override
//    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        db.setVersion(oldVersion);
//    }

    public Boolean insertDataLogin(String email, String name, String password, String image) {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("email", email);
        contentValues.put("name", name);
        contentValues.put("password", password);
        contentValues.put("image", image);
        long result = MyDB.insert("users", null, contentValues);
        return result != -1;
    }

    @SuppressLint("Range")
    public ArrayList<User> getListUsers() {
        ArrayList<User> userList = new ArrayList<>();
        SQLiteDatabase MyDB = this.getWritableDatabase();
        String query = "Select * from users";
        Cursor c = MyDB.rawQuery(query, null);

        c.moveToFirst();
        while (!c.isAfterLast()) {
            try {
                User user = new User();
                user.email = c.getString(c.getColumnIndex("email"));
                user.name = c.getString(c.getColumnIndex("name"));
                user.password = c.getString(c.getColumnIndex("password"));
                user.image = c.getString(c.getColumnIndex("image"));
//                user.ID = Random_Code();
                userList.add(user);
                c.moveToNext();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        c.close();
        return userList;
    }

    public void insertDataMessage(String from, String to, String message, String sentDate) {
        SQLiteDatabase MyDB = getWritableDatabase();
        String query = "insert into messages (FromMail, ToMail, Message, SentDate) values('" + from + "', '" + to + "', '" + message.replace("'", "\"") + "','" + sentDate + "');";
        MyDB.execSQL(query);
    }
    
    @SuppressLint("Range")
    public List<Message> getChat(String emailSend, String emailReceiver) {
        List<Message> messageList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        try {
//            int limit = (5 * pageNo) + 35;
//            String whereCondition = "((FromMail = '" + userMail + "' and ToMail='" + friendMail + "') or (ToMail = '" + userMail + "' and FromMail='" + friendMail + "'))";
//            String query = "select * from ( select rowid, * from Messages where " + whereCondition + " order by rowid desc limit " + limit + ")  order by rowid ";
            String query = "select * from messages where FromMail = ? and ToMail = ?";
            Cursor c1 = db.rawQuery(query, new String[]{emailSend, emailReceiver});
            c1.moveToFirst();
            while (!c1.isAfterLast()) {
//                Message mess = new Message();
//                mess.FromMail = c1.getString(c1.getColumnIndex("FromMail"));
//                mess.ToMail = c1.getString(c1.getColumnIndex("ToMail"));
//                mess.Message = c1.getString(c1.getColumnIndex("Message"));
//                mess.SentDate = c1.getString(c1.getColumnIndex("SentDate"));
//                messageList.add(mess);
//                c1.moveToNext();
            }
            c1.close();

            Cursor c2 = db.rawQuery(query, new String[]{emailReceiver, emailSend});
            c2.moveToFirst();
            while (!c2.isAfterLast()) {
//                Message mess = new Message();
//                mess.FromMail = c2.getString(c2.getColumnIndex("FromMail"));
//                mess.ToMail = c2.getString(c2.getColumnIndex("ToMail"));
//                mess.Message = c2.getString(c2.getColumnIndex("Message"));
//                mess.SentDate = c2.getString(c2.getColumnIndex("SentDate"));
//                messageList.add(mess);
//                c2.moveToNext();
            }
//            Log.e("jjj", messageList.get(0).getMessage());
            c2.close();
            return messageList;
        } catch (Exception e) {
            e.printStackTrace();
            return messageList;
        }
    }

    public void updatePassword(String email, String newPassword) {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PASSWORD, newPassword);
//        Log.d(newPassword, "abc");
        MyDB.update("users", values,"email = ?", new String[] { email });
        MyDB.close();
    }

    public void updateImageUser(String email, String image) {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_IMAGE, image);
//        Log.d(newPassword, "abc");
        MyDB.update("users", values,"email = ?", new String[] { email });
        MyDB.update("messages", values,"FromMail = ?", new String[] { email });
        MyDB.close();
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

    public Boolean checkEmailFormatForgot(String email) {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor cursor = MyDB.rawQuery("Select * from users where email=?", new String[]{email});
        return isValidEmail(email) && cursor.getCount() > 0 && !email.isEmpty();
    }

    public Boolean checkusernamepassword(String email, String password) {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor cursor = MyDB.rawQuery("Select * from users where email = ? and password = ?", new String[]{email, password});
        return cursor.getCount() > 0;
    }

    public String getName(String email) {
        ArrayList<User> Users = getListUsers();
        String tmp = " ";
        for(int i=0;i<Users.size();i++) {
            if(Users.get(i).getEmail().equals(email)) {
                tmp = Users.get(i).getName();
            }
        }
        return tmp;
    }

    public String getImage(String email) {
        ArrayList<User> Users = getListUsers();
        String tmp = " ";
        for(int i=0;i<Users.size();i++) {
            if(Users.get(i).getEmail().equals(email)) {
                tmp = Users.get(i).getImage();
            }
        }
        return tmp;
    }


    public int Random_Code()
    {
        int min = 100000;
        int max = 999999;
        int random_int = (int)Math.floor(Math.random()*(max-min+1)+min);
        return random_int;
    }
}
