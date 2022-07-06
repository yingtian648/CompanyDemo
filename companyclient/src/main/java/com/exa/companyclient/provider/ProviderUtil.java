package com.exa.companyclient.provider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.exa.companyclient.Constants;
import com.exa.companyclient.utils.L;

import java.util.ArrayList;
import java.util.List;

public class ProviderUtil {

    //测试provider功能
    public static void testProvider(Context context) {
        List<Files> files = ProviderUtil.getProviderData(context);
        if (!files.isEmpty()) {
            ProviderUtil.deleteById(context, String.valueOf(files.get(1).id));
            ProviderUtil.updateData(context, files.get(0));
            ProviderUtil.insert(context, files.get(0));
        }
    }

    public static List<Files> getSystemMediaProviderData(Context context) {
        ContentResolver resolver = context.getContentResolver();
        String[] projection = new String[]{
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.TITLE,
                MediaStore.Video.Media.ALBUM,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.MIME_TYPE,
                MediaStore.Video.Media.DISPLAY_NAME,
//                MediaStore.Video.Media.AUTHOR,//API 30
//                MediaStore.Video.Media.ALBUM_ARTIST,//API 30
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.DATE_ADDED,
                MediaStore.Video.Media.DATE_MODIFIED,
                MediaStore.Video.Media.HEIGHT,
                MediaStore.Video.Media.WIDTH,
        };
        Cursor cursor = resolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,//外部存储
                projection, null, null, null);
        List<Files> dataList = new ArrayList<>();
        while (cursor.moveToNext()) {
            Files info = new Files();
            info.name = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE));
            info.size = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media.SIZE));
            info.path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
            info.id = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media._ID));
            info.add_time = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media.DATE_ADDED));
            info.modify_time = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media.DATE_MODIFIED));
            info.duration = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));
            info.mime_type = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.MIME_TYPE));
            dataList.add(info);
            L.d("query result:" + info);
        }
        cursor.close();
        L.d("getSystemMediaProviderData : " + dataList.size());
        return dataList;
    }

    public static List<Files> getProviderData(Context context) {
        ContentResolver resolver = context.getContentResolver();
        String[] projection = new String[]{"id", "name", "path", "add_time", "size", "duration"};
        Cursor cursor = resolver.query(Constants.CUSTOMER_URI,
                projection, null, null, null);
        List<Files> dataList = new ArrayList<>();
        while (cursor.moveToNext()) {
            Files info = new Files();
            info.name = cursor.getString(cursor.getColumnIndex("name"));
            info.size = cursor.getInt(cursor.getColumnIndex("size"));
            info.path = cursor.getString(cursor.getColumnIndex("path"));
            info.id = cursor.getInt(cursor.getColumnIndex("id"));
            info.add_time = cursor.getInt(cursor.getColumnIndex("add_time"));
            info.duration = cursor.getInt(cursor.getColumnIndex("duration"));
            dataList.add(info);
        }
        cursor.close();
        L.d("query result:" + dataList.size());
        return dataList;
    }

    public static void deleteById(Context context, String id) {
        ContentResolver resolver = context.getContentResolver();
        int result = resolver.delete(Constants.CUSTOMER_URI, "id=" + id, null);
        L.d("deleteById: " + id + "  result:" + result);
    }

    public static void updateData(Context context, Files files) {
        ContentResolver resolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put("name", "妈妈叫你回家吃饭");
        int result = resolver.update(Constants.CUSTOMER_URI, values, "id=" + files.id, null);
        L.d("updateData result:" + result + " " + files);
    }

    public static void insert(Context context, Files files) {
        ContentResolver resolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put("name", "客户端插入name");
        Uri uri = resolver.insert(Constants.CUSTOMER_URI, values);
        L.d("insert uri:" + uri + "  files:" + files);
    }
}
