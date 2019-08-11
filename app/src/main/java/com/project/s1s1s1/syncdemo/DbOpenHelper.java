package com.project.s1s1s1.syncdemo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbOpenHelper extends SQLiteOpenHelper {

    private static final String DB_NAME ="userDB";
    private static final int DB_VERSION =1;
    public static final String TABLE_NAME="userTBL";
    public static final String COL_ID="id_";
    public static final String COL_NAME="name";
    public static final String COL_SYNC_STATE="status";
    private Context context;

    public static final String CREATE_TBL="create table "+TABLE_NAME+ "( "+
            COL_ID+" integer primary key, "+
            COL_NAME+" text, "+
            COL_SYNC_STATE+" integer);";

    public static final String READ_USER_TBL="select * from "+TABLE_NAME;

    public static final String READ_FAILED_USER="select * from "+TABLE_NAME+ " where "+COL_SYNC_STATE + " = "+Constant.SYNC_STSTUS_FAILED;

    public static final String READ_SUCCESS_USER="select * from "+TABLE_NAME+ " where "+COL_SYNC_STATE + " = "+Constant.SYNC_STATUS_OK;

    public static final String DROP_TABLE = "drop table if exists "+TABLE_NAME;

    public DbOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TBL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(DROP_TABLE);
        onCreate(sqLiteDatabase);
    }
}
