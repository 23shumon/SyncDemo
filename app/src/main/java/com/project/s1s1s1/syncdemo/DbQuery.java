package com.project.s1s1s1.syncdemo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class DbQuery {
    private DbOpenHelper helper;
    private SQLiteDatabase db;

    public DbQuery(Context context) {
        helper=new DbOpenHelper(context);
    }

    public void openDb(){
        db=helper.getReadableDatabase();
    }

    public void closeDb(){
        db.close();
    }

    public void saveDataIntoDb(User user){
        this.openDb();
        ContentValues contentValues=new ContentValues();
        contentValues.put(DbOpenHelper.COL_NAME,user.getName());
        contentValues.put(DbOpenHelper.COL_SYNC_STATE,user.getSync_status());
        try{
            db.insert(DbOpenHelper.TABLE_NAME,null,contentValues);
        }catch (Exception e){
            e.printStackTrace();
        }
        this.closeDb();
    }

    public List<User> getAlluser(){

        List<User>userList=new ArrayList<>();
        this.openDb();
        Cursor cursor=db.rawQuery(DbOpenHelper.READ_USER_TBL, null);
        if (cursor!=null && cursor.getCount()>0){
            cursor.moveToFirst();
            do {
                User user =new User();
                user.setId(cursor.getInt(cursor.getColumnIndex(DbOpenHelper.COL_ID)));
                user.setName(cursor.getString(cursor.getColumnIndex(DbOpenHelper.COL_NAME)));
                user.setSync_status(cursor.getInt(cursor.getColumnIndex(DbOpenHelper.COL_SYNC_STATE)));
                userList.add(user);
            }while (cursor.moveToNext());
            cursor.close();
        }
        Log.e(TAG, "getAlluser: "+userList.size() );
        this.closeDb();
        return userList;
    }

    public void updateData(User user){
        this.openDb();
        ContentValues contentValues=new ContentValues();
        contentValues.put(DbOpenHelper.COL_SYNC_STATE,user.getSync_status());
        String clause= DbOpenHelper.COL_NAME+ " LIKE ?";
        String [] arguments ={user.getName()};
        try{
            db.update(DbOpenHelper.TABLE_NAME,contentValues,clause,arguments);
        }catch (Exception e){
            e.printStackTrace();
        }
        this.closeDb();
    }


    public List<User> syncFailedUser(){

        List<User>waitingUser=new ArrayList<>();
        this.openDb();
        Cursor cursor=db.rawQuery(DbOpenHelper.READ_FAILED_USER, null);
        if (cursor!=null && cursor.getCount()>0){
            cursor.moveToFirst();
            do {
                User failedUser = new User();
                failedUser.setId((cursor.getInt(cursor.getColumnIndex(DbOpenHelper.COL_ID))));
                failedUser.setName(cursor.getString(cursor.getColumnIndex(DbOpenHelper.COL_NAME)));
                failedUser.setSync_status(cursor.getInt(cursor.getColumnIndex(DbOpenHelper.COL_SYNC_STATE)));
                waitingUser.add(failedUser);
            }while (cursor.moveToNext());
            cursor.close();
        }
        Log.e(TAG, "getAlluser: "+waitingUser.size() );
        this.closeDb();
        return waitingUser;
    }

    public List<User> syncSuccessUser(){

        List<User>successList=new ArrayList<>();
        this.openDb();
        Cursor cursor=db.rawQuery(DbOpenHelper.READ_SUCCESS_USER, null);
        if (cursor!=null && cursor.getCount()>0){
            cursor.moveToFirst();
            do {
                User successUser = new User();
                successUser.setId((cursor.getInt(cursor.getColumnIndex(DbOpenHelper.COL_ID))));
                successUser.setName(cursor.getString(cursor.getColumnIndex(DbOpenHelper.COL_NAME)));
                successUser.setSync_status(cursor.getInt(cursor.getColumnIndex(DbOpenHelper.COL_SYNC_STATE)));
                successList.add(successUser);
            }while (cursor.moveToNext());
            cursor.close();
        }
        Log.e(TAG, "getAlluser: "+successList.size() );
        this.closeDb();
        return successList;
    }


}
