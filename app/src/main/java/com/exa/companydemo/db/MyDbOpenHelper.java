package com.exa.companydemo.db;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class MyDbOpenHelper extends SQLiteOpenHelper {
    private final String TAG = MyDbOpenHelper.class.getSimpleName();
    public static final String DB_NAME = "myappd.db";// 数据库名称
    public static final int DB_VERSION = 1;
    private static final String CREATE_FILES_TABLE = "CREATE TABLE files"
            + "(id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "name TEXT,path TEXT,root_dir TEXT,add_time INTEGER,modify_time INTEGER,mime_type TEXT,"
            + "size INTEGER,duration INTEGER,width DOUBLE,height INTEGER,artist TEXT,"
            + "album TEXT,display_name TEXT,file_type INTEGER,tags TEXT)";

    public MyDbOpenHelper(@Nullable Context context) {
        this(context, DB_VERSION, null, null);
    }

    public MyDbOpenHelper(@Nullable Context context, int version, @Nullable SQLiteDatabase.CursorFactory factory, @Nullable DatabaseErrorHandler errorHandler) {
        super(context, DB_NAME, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {//创建数据库
        Log.d(TAG, "onCreate");
        createTables(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {//更新数据库
        if (newVersion > oldVersion)
            updateDb(db, oldVersion, newVersion);
    }

    private void createTables(SQLiteDatabase db) {
        db.execSQL(CREATE_FILES_TABLE);
    }

    private void updateDb(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "updateDb " + oldVersion + " -> " + newVersion);
    }
}
