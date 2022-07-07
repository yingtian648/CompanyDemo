package com.exa.companyclient.provider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.exa.companyclient.Constants;
import com.exa.companyclient.utils.L;

import java.util.ArrayList;
import java.util.List;

public class MyProviderUtil {

    //测试provider功能
    public static void testProvider(Context context) {
        List<Files> files = MyProviderUtil.getProviderData(context);
        if (!files.isEmpty()) {
            MyProviderUtil.deleteById(context, String.valueOf(files.get(1).id));
            MyProviderUtil.updateData(context, files.get(0));
            MyProviderUtil.insert(context, files.get(0));
        }
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
