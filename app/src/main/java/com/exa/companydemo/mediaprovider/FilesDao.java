package com.exa.companydemo.mediaprovider;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.exa.companydemo.mediaprovider.entity.Files;

import java.util.ArrayList;

import androidx.annotation.NonNull;

public class FilesDao {
    private MyDbOpenHelper helper;
    private final String TAG = FilesDao.class.getSimpleName();

    public FilesDao(Context context) {
        helper = new MyDbOpenHelper(context);
    }

    public MyDbOpenHelper getHelper() {
        return helper;
    }

    /**
     * 插入数据列表
     */
    public boolean insertBySQLiteStatement(@NonNull ArrayList<Files> filesList) {
        Log.d(TAG, "insertFiles files.size=" + filesList.size());
        long time = System.currentTimeMillis();
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            String sqlD = "INSERT INTO files (add_time,modify_time,size,duration,width,height," +
                    "file_type,name,path,root_dir,mime_type,artist," +
                    "album,display_name,tags) " +
                    "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            db.beginTransaction();
            SQLiteStatement stat = db.compileStatement(sqlD);
            long id = 0;
            for (Files files : filesList) {
                stat.bindLong(1, files.add_time);
                stat.bindLong(2, files.modify_time);
                stat.bindLong(3, files.size);
                if (files.duration != null)
                    stat.bindLong(4, files.duration);
                if (files.width != null)
                    stat.bindLong(5, files.width);
                if (files.height != null)
                    stat.bindLong(6, files.height);
                if (files.file_type != null)
                    stat.bindLong(7, files.file_type);
                if (files.name != null)
                    stat.bindString(8, files.name);
                stat.bindString(9, files.path);
                if (files.root_dir != null)
                    stat.bindString(10, files.root_dir);
                if (files.mime_type != null)
                    stat.bindString(11, files.mime_type);
                if (files.artist != null)
                    stat.bindString(12, files.artist);//没有值，可以不传
                if (files.album != null)
                    stat.bindString(13, files.album);
                if (files.display_name != null)
                    stat.bindString(14, files.display_name);
                stat.bindString(15, "");//不能传空值
                id = stat.executeInsert();
            }
            db.setTransactionSuccessful();
            db.endTransaction();
            time = (System.currentTimeMillis() - time);
            Log.d(TAG, "insertFiles complete:" + id + " useTime=" + time);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "insertFiles: SQLException:" + e.getMessage());
            return false;
        } finally {
            db.close();
        }
    }

    /**
     * 批量插入
     *
     * @param filesList
     */
    public void insertByContentValues(@NonNull ArrayList<Files> filesList) {
        Log.d(TAG, "insertFiles files.size=" + filesList.size());
        long time = System.currentTimeMillis();
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransaction();
        for (Files files : filesList) {
            ContentValues values = new ContentValues();
            values.put("add_time", files.add_time);
            values.put("modify_time", files.modify_time);
            values.put("size", files.size);
            values.put("duration", files.duration);
            values.put("width", files.width);
            values.put("height", files.height);
            values.put("file_type", files.file_type);
            values.put("name", files.name);
            values.put("path", files.path);
            values.put("root_dir", files.root_dir);
            values.put("mime_type", files.mime_type);
            values.put("artist", files.artist);
            values.put("album", files.album);
            values.put("display_name", files.display_name);
            values.put("tags", files.tags);

            SQLiteDatabase db1 = helper.getWritableDatabase();
            db.insert("files", null, values);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    /**
     * 批量插入数据列表
     */
    public boolean insertFiles(@NonNull ArrayList<Files> filesList) {
        Log.d(TAG, "insertFiles files.size=" + filesList.size());
        long time = System.currentTimeMillis();
        SQLiteDatabase db = helper.getWritableDatabase();
        String sqlD = "INSERT INTO files (add_time,modify_time,size,duration,width,height," +
                "file_type,name,path,root_dir,mime_type,artist," +
                "album,display_name,tags) " +
                "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try {
            db.beginTransaction();
            for (Files files : filesList) {
                db.execSQL(sqlD, new Object[]{
                        files.add_time, files.modify_time, files.size, files.duration, files.width, files.height,
                        files.file_type, files.name, files.path, files.root_dir, files.mime_type, files.artist,
                        files.album, files.display_name, files.tags

                });
            }
            db.setTransactionSuccessful();
            db.endTransaction();
            Log.d(TAG, "insertFiles complete useTime=" + time);
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
    public boolean insertFiles(@NonNull Files files) {
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


    public ArrayList<Files> searchAll() {
        SQLiteDatabase db = helper.getReadableDatabase();
        String sql = "SELECT * FROM files";
        @SuppressLint("Recycle")
        Cursor cursor = db.rawQuery(sql, null);
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
        String sql = "DELETE FROM files WHERE id=?";
        try {
            db.execSQL(sql, ids);
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(TAG, "deleteById SQLException:" + e.getMessage());
            return false;
        } finally {
            db.close();
        }
        return true;
    }

    /**
     * 通过id删除记录
     *
     * @return
     */
    public boolean deleteAll() {
        SQLiteDatabase db = helper.getWritableDatabase();
        String sql = "DELETE FROM files";
        try {
            db.delete("files", null, null);
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(TAG, "deleteAll SQLException:" + e.getMessage());
            return false;
        } finally {
            db.close();
        }
        Log.d(TAG, "deleteAll files table complete");
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
        ContentValues values = new ContentValues();
        values.put("name","2022208021011");
        db.update("files",values,"id="+id,null);
//        db.beginTransaction();
//        String sql = "UPDATE files SET add_time=?,modify_time=?,size=?,duration=?,width=?,height=?," +
//                "file_type=?,name=?,path=?,root_dir=?,mime_type=?,artist=?," +
//                "album=?,display_name=?,tags=? WHERE id=?";
//        try {
//            db.execSQL(sql, new Object[]{
//                    files.add_time, files.modify_time, files.size, files.duration, files.width, files.height,
//                    files.file_type, files.name, files.path, files.root_dir, files.mime_type, files.artist,
//                    files.album, files.display_name, files.tags, id
//
//            });
//        } catch (SQLException e) {
//            e.printStackTrace();
//            Log.e(TAG, "updateById SQLException:" + e.getMessage());
//            return false;
//        } finally {
//            db.endTransaction();
//        }
        db.close();
        return true;
    }
}
