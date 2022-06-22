package com.exa.companydemo.db;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.exa.companydemo.db.entity.Files;

import java.util.ArrayList;

import androidx.annotation.NonNull;

public class FilesDao {
    private MyDbOpenHelper helper;
    private final String TAG = FilesDao.class.getSimpleName();

    public FilesDao(Context context) {
        helper = new MyDbOpenHelper(context);
    }

    /**
     * 插入数据列表
     */
    public boolean insertFiles(@NonNull ArrayList<Files> files) {
        Log.d(TAG, "insertFiles files.size=" + files.size());
        SQLiteDatabase db = helper.getWritableDatabase();
        String sqlD = "INSERT INTO files (add_time,modify_time,size,duration,width,height," +
                "file_type,name,path,root_dir,mime_type,artist," +
                "album,display_name,tags) " +
                "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        SQLiteStatement stat = null;
        try {
            stat = db.compileStatement(sqlD);
            db.beginTransaction();
            for (Files item : files) {
                stat.bindLong(1, item.add_time);
                stat.bindLong(2, item.modify_time);
                stat.bindLong(3, item.size);
                stat.bindLong(4, item.duration);
                stat.bindLong(5, item.width);
                stat.bindLong(6, item.height);
                stat.bindLong(7, item.file_type);
                stat.bindString(8, item.name);
                stat.bindString(9, item.path);
                stat.bindString(10, item.root_dir);
                stat.bindString(11, item.mime_type);
                stat.bindString(12, item.artist);
                stat.bindString(13, item.album);
                stat.bindString(14, item.display_name);
                stat.bindString(15, item.tags);
                long id = stat.executeInsert();
                Log.d(TAG, "insertFiles success:" + id);
            }
            db.setTransactionSuccessful();
            db.endTransaction();
            Log.d(TAG, "insertFiles complete");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(TAG, "insertFiles: SQLException:" + e.getMessage());
            return false;
        } finally {
            db.close();
        }
    }

    /**
     * 插入数据
     */
    public boolean insertFile(@NonNull Files files) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransaction();
        String sql = "INSERT INTO files VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        String sqlD = "INSERT INTO files (add_time,modify_time,size,duration,width,height," +
                "file_type,name,path,root_dir,mime_type,artist," +
                "album,display_name,tags) " +
                "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try {
            db.execSQL(sqlD, new Object[]{
                    files.add_time, files.modify_time, files.size, files.duration, files.width, files.height,
                    files.file_type, files.name, files.path, files.root_dir, files.mime_type, files.artist,
                    files.album, files.display_name, files.tags

            });
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(TAG, "insertFile SQLException:" + e.getMessage());
            return false;
        } finally {
            db.endTransaction();
        }
        db.close();
        return true;
    }

    public ArrayList<Files> searchMediaFilesByDir(String rootDir) {
        SQLiteDatabase db = helper.getReadableDatabase();
        String sql = "SELECT * FROM files WHERE root_dir=?";
        @SuppressLint("Recycle")
        Cursor cursor = db.rawQuery(sql, new String[]{rootDir});
        ArrayList<Files> files = new ArrayList<>();
        while (cursor.moveToNext()) {
            Files filesEntity = new Files();
            filesEntity.id = cursor.getInt(cursor.getColumnIndex("id"));
            filesEntity.add_time = cursor.getLong(cursor.getColumnIndex("add_time"));
            filesEntity.modify_time = cursor.getLong(cursor.getColumnIndex("modify_time"));
            filesEntity.size = cursor.getLong(cursor.getColumnIndex("size"));
            filesEntity.duration = cursor.getInt(cursor.getColumnIndex("duration"));
            filesEntity.width = cursor.getInt(cursor.getColumnIndex("width"));
            filesEntity.height = cursor.getInt(cursor.getColumnIndex("height"));
            filesEntity.file_type = cursor.getInt(cursor.getColumnIndex("file_type"));
            filesEntity.name = cursor.getString(cursor.getColumnIndex("name"));
            filesEntity.path = cursor.getString(cursor.getColumnIndex("path"));
            filesEntity.root_dir = cursor.getString(cursor.getColumnIndex("root_dir"));
            filesEntity.mime_type = cursor.getString(cursor.getColumnIndex("mime_type"));
            filesEntity.artist = cursor.getString(cursor.getColumnIndex("artist"));
            filesEntity.album = cursor.getString(cursor.getColumnIndex("album"));
            filesEntity.display_name = cursor.getString(cursor.getColumnIndex("display_name"));
            filesEntity.tags = cursor.getString(cursor.getColumnIndex("tags"));
            files.add(filesEntity);
        }
        cursor.close();
        db.close();
        return files;
    }

    /**
     * 通过id删除记录
     *
     * @param ids
     * @return
     */
    public boolean deleteById(Long... ids) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransaction();
        String sql = "DELETE FROM files WHERE id=?";
        try {
            db.execSQL(sql, ids);
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(TAG, "deleteById SQLException:" + e.getMessage());
            return false;
        } finally {
            db.endTransaction();
        }
        db.close();
        return true;
    }

    /**
     * 跟新数据
     *
     * @param id
     * @param files
     */
    public boolean updateById(long id, Files files) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransaction();
        String sql = "UPDATE files SET add_time=?,modify_time=?,size=?,duration=?,width=?,height=?," +
                "file_type=?,name=?,path=?,root_dir=?,mime_type=?,artist=?," +
                "album=?,display_name=?,tags=? WHERE id=?";
        try {
            db.execSQL(sql, new Object[]{
                    files.add_time, files.modify_time, files.size, files.duration, files.width, files.height,
                    files.file_type, files.name, files.path, files.root_dir, files.mime_type, files.artist,
                    files.album, files.display_name, files.tags, id

            });
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(TAG, "updateById SQLException:" + e.getMessage());
            return false;
        } finally {
            db.endTransaction();
        }
        db.close();
        return true;
    }
}
