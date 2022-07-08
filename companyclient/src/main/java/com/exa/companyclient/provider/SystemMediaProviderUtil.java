package com.exa.companyclient.provider;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.exa.baselib.utils.L;
import com.exa.baselib.BaseConstants;

import java.util.ArrayList;
import java.util.List;

public class SystemMediaProviderUtil {
    /**
     * 获取系统MediaProvider数据
     *
     * @param context
     * @param type    1音频 2视频 3图片
     * @return data
     */
    public static List<Files> getSystemMediaProviderData(Context context, int type) {
        L.d("getSystemMediaProviderData start:" + MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        Uri urip = Uri.parse("content://media").buildUpon().appendPath("external").appendPath("audio").appendPath("media").build();
        L.d("urip:"+urip.toString());
        ContentResolver resolver = context.getContentResolver();
        String[] projection;
        Uri uri;
        switch (type) {
            case BaseConstants.SystemMediaType.Image:
                uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                projection = new String[]{
                        MediaStore.Video.Media._ID,
                        MediaStore.Video.Media.TITLE,
                        MediaStore.Video.Media.SIZE,
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
                break;
            case BaseConstants.SystemMediaType.Video:
                uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                projection = new String[]{
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
                break;
            default:
                uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                projection = new String[]{
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
                };
                break;
        }
        Cursor cursor = resolver.query(uri,//外部存储
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
            if (type != BaseConstants.SystemMediaType.Image) {
                info.duration = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));
            }
            info.mime_type = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.MIME_TYPE));
            if (type != BaseConstants.SystemMediaType.Audio) {
                info.width = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media.WIDTH));
                info.height = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media.HEIGHT));
            }
            dataList.add(info);
            L.d("query result:" + info);
        }
        cursor.close();
        L.d("getSystemMediaProviderData : " + dataList.size());
        return dataList;
    }
}
