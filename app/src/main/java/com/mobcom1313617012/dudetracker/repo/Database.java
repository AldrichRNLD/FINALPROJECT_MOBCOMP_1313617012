package com.mobcom1313617012.dudetracker.repo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.lifecycle.MutableLiveData;

import com.mobcom1313617012.dudetracker.model.User;

import java.util.ArrayList;

public class Database {
    private static final MutableLiveData<ArrayList<User>> USER_MLD = new MutableLiveData<>(new ArrayList<>());
    private final SQLiteDatabase DB;
    private final String TABLE_USER = "user1313617012";
    private final String ATTR_ID = "id1313617012";
    private final String ATTR_NAME = "name1313617012";
    private final String ATTR_TOKEN = "token1313617012";
    private final String ATTR_LAT = "lat1313617012";
    private final String ATTR_LNG = "lng1313617012";
    private final String ATTR_STAT = "status1313617012";
    public Database(Context context) {
        this.DB = context.openOrCreateDatabase("dudetracker.db", Context.MODE_PRIVATE, null);
        String query = "CREATE TABLE IF NOT EXISTS " + TABLE_USER +"("+
                ATTR_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ATTR_NAME+ " VARCHAR(40) NOT NULL, " +
                ATTR_TOKEN+ " VARCHAR(40) NOT NULL, " +
                ATTR_LAT+ " REAL NOT NULL, "+
                ATTR_LNG+ " REAL NOT NULL, "+
                ATTR_STAT+ " INT NOT NULL "+
                ");";
        DB.execSQL(query);
    }

    public boolean isUserExists(User user) {
        String query = "SELECT COUNT("+ATTR_ID+") FROM "
                +TABLE_USER+" WHERE "
                +ATTR_NAME+"='"+user.name+"' AND "
                +ATTR_TOKEN+"='"+user.token+"'";
        @SuppressLint("Recycle") Cursor cursor = DB.rawQuery(query, null);
        if(cursor.moveToFirst()){
            if(cursor.getInt(0) > 0) {
                cursor.close();
                return true;
            }
        }
        cursor.close();
        return false;
    }

    public void create(User user) {
        if(!isUserExists(user)) {
            String query = "INSERT INTO " + TABLE_USER
                + "("+ATTR_NAME+","+ATTR_TOKEN+","+ATTR_LAT+","+ATTR_LNG+","+ATTR_STAT+")"
                + "VALUES (" +
                    "'"+user.name+"',"+
                    "'"+user.token+"',"+
                    user.lat+","+
                    user.lng+","+
                    user.status+
                ");";
            DB.execSQL(query);
            read();
        } else
            update(user);
    }

    public void update(User user) {
        String query = "UPDATE " + TABLE_USER + " SET " +
                ATTR_LAT + "=" + user.lat + "," +
                ATTR_LNG + "=" + user.lng + "," +
                ATTR_STAT + "=" + user.status +
                " WHERE " + ATTR_NAME + " = '"+user.name+
                "' AND " + ATTR_TOKEN + " = '"+user.token+"'";
        DB.execSQL(query);
        read();
    }

    public void delete(User user) {
        String query = "DELETE FROM "+TABLE_USER+" WHERE "+ATTR_ID+"="+user.id;
        DB.execSQL(query);
    }

    public MutableLiveData<ArrayList<User>> read() {
        new Thread(() -> {
            String query = "SELECT * FROM "+TABLE_USER;
            @SuppressLint("Recycle") Cursor cursor = DB.rawQuery(query, null);
            ArrayList<User> tmp = new ArrayList<>();
            if(cursor.moveToFirst()){
                if(cursor.getInt(0) > 0) {
                    while(!cursor.isAfterLast()){
                        User user = new User();
                        user.id = cursor.getInt(0);
                        user.name = cursor.getString(1);
                        user.token = cursor.getString(2);
                        user.lat = cursor.getDouble(3);
                        user.lng = cursor.getDouble(4);
                        user.status = cursor.getInt(5);
                        tmp.add(user);
                        cursor.moveToNext();
                    }
                }
            }
            cursor.close();
            USER_MLD.postValue(tmp);
        }).start();
        return USER_MLD;
    }
}
