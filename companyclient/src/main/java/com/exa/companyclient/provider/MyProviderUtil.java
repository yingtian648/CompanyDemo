package com.exa.companyclient.provider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;

import com.exa.baselib.utils.L;
import com.exa.baselib.BaseConstants;
import com.exa.companyclient.App;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

public class MyProviderUtil {
    private static ContentObserver observer;

    public static ContentObserver getObserver() {
        if (observer == null) {
            HandlerThread handlerThread = new HandlerThread("another");
            handlerThread.start();
            observer = new ContentObserver(new Handler(handlerThread.getLooper())) {
                @Override
                public void onChange(boolean selfChange, @Nullable Uri uri) {//搜到uri变化回调
                    L.d("ContentObserver.onChange:" + uri);
                }
            };
        }
        return observer;
    }

    //测试provider功能
    public static void testMyProvider(Context context) {
        List<Files> files = MyProviderUtil.getProviderData(context);
//        if (!files.isEmpty()) {
//            MyProviderUtil.deleteById(context, String.valueOf(files.get(1).id));
//            MyProviderUtil.updateData(context, files.get(0));
//            MyProviderUtil.insert(context, files.get(0));
//        }
    }

    /**
     * 注册监听者 监听uri的变化
     *
     * @param context
     * @param observer
     */
    public static void registerObserver(Context context, ContentObserver observer) {
        L.dd();
        ContentResolver resolver = context.getContentResolver();
        resolver.registerContentObserver(BaseConstants.CUSTOMER_URI, true, observer);
    }

    /**
     * 注销监听者
     *
     * @param context
     * @param observer
     */
    public static void unregisterObserver(Context context, ContentObserver observer) {
        L.dd();
        ContentResolver resolver = context.getContentResolver();
        resolver.unregisterContentObserver(observer);
    }

    public static List<Files> getProviderData(Context context) {
        ContentResolver resolver = context.getContentResolver();
        String[] projection = new String[]{"id", "name", "path", "add_time", "size", "duration"};
        Cursor cursor = resolver.query(BaseConstants.CUSTOMER_URI,
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
        int result = resolver.delete(BaseConstants.CUSTOMER_URI, "id=" + id, null);
        L.d("deleteById: " + id + "  result:" + result);
    }

    public static void updateData(Context context, Files files) {
        ContentResolver resolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put("name", "妈妈叫你回家吃饭");
        int result = resolver.update(BaseConstants.CUSTOMER_URI, values, "id=" + files.id, null);
        L.d("updateData result:" + result + " " + files);
    }

    public static void insert(Context context, Files files) {
        ContentResolver resolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put("name", "客户端插入name");
        Uri uri = resolver.insert(BaseConstants.CUSTOMER_URI, values);
        L.d("insert uri:" + uri + "  files:" + files);
    }
}
