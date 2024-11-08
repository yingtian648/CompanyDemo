package com.exa.companyclient.provider;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.MediaStore;
import android.util.Size;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.exa.baselib.BaseConstants;
import com.exa.baselib.bean.EventBean;
import com.exa.baselib.bean.Files;
import com.exa.baselib.utils.L;
import com.exa.companyclient.App;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SystemMediaProviderUtil {

    private static ContentObserver observer;

    public static ContentObserver getObserver() {
        if (observer == null) {
            HandlerThread handlerThread = new HandlerThread("another");
            handlerThread.start();
            observer = new ContentObserver(new Handler(handlerThread.getLooper())) {
                long last = 0;

                @Override
                public void onChange(boolean selfChange, @Nullable Uri uri) {//搜到uri变化回调
                    L.d("ContentObserver.onChange:" + uri);
                    EventBus.getDefault().post(new EventBean("ContentObserver.onChange:" + uri));
//                    if (System.currentTimeMillis() - last > 1000) {
//                        last = System.currentTimeMillis();
//                        ExeHelper.getInstance().exeGetSystemMediaProviderData();
//                    }
                }
            };
        }
        return observer;
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
//        resolver.registerContentObserver(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, true, observer);
        resolver.registerContentObserver(Uri.parse("content://media/external/file"), true, observer);
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

    public static void deleteAll(Context context) {
        ContentResolver resolver = context.getContentResolver();
        Uri urip = Uri.parse("content://media").buildUpon().appendPath("internal").appendPath("audio").appendPath("media").build();
        urip = Uri.parse("content://media").buildUpon().appendPath("external").appendPath("audio").appendPath("media").build();
        resolver.delete(urip, null);
        urip = Uri.parse("content://media").buildUpon().appendPath("external").appendPath("images").appendPath("media").build();
        resolver.delete(urip, null);
        urip = Uri.parse("content://media").buildUpon().appendPath("external").appendPath("video").appendPath("media").build();
        resolver.delete(urip, null);
        urip = Uri.parse("content://media").buildUpon().appendPath("internal").appendPath("audio").appendPath("media").build();
        resolver.delete(urip, null);
        urip = Uri.parse("content://media").buildUpon().appendPath("internal").appendPath("video").appendPath("media").build();
        resolver.delete(urip, null);
        urip = Uri.parse("content://media").buildUpon().appendPath("internal").appendPath("images").appendPath("media").build();
        resolver.delete(urip, null);
    }

    /**
     * 获取系统MediaProvider数据?
     *
     * @param context
     * @param type    1音频 2视频 3图片
     * @return data
     */
    public static ArrayList<Files> getSystemMediaProviderData(Context context, int type) {
        Uri urip = Uri.parse("content://media").buildUpon()
                .appendPath("external_primary")
                .appendPath("audio")
                .appendPath("media").build();
        ContentResolver resolver = context.getContentResolver();
        String[] projection;
        Uri uri;
        switch (type) {
            case BaseConstants.SystemMediaType.Image:
                uri = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);//MediaStore.VOLUME_EXTERNAL_PRIMARY
                projection = new String[]{
                        MediaStore.Images.Media._ID,
                        MediaStore.Images.Media.TITLE,
                        MediaStore.Images.Media.SIZE,
                        MediaStore.Images.Media.MIME_TYPE,
                        MediaStore.Images.Media.DISPLAY_NAME,
//                MediaStore.Video.Images.AUTHOR,//API 30
//                MediaStore.Video.Images.ALBUM_ARTIST,//API 30
                        MediaStore.Images.Media.DATA,
                        MediaStore.Images.Media.DATE_ADDED,
                        MediaStore.Images.Media.DATE_MODIFIED,
                        MediaStore.Images.Media.HEIGHT,
                        MediaStore.Images.Media.WIDTH,
                };
                break;
            case BaseConstants.SystemMediaType.Video:
                uri = MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);//MediaStore.VOLUME_EXTERNAL_PRIMARY
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
                uri = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);//MediaStore.VOLUME_EXTERNAL_PRIMARY
                projection = new String[]{
                        MediaStore.Audio.Media._ID,
                        MediaStore.Audio.Media.TITLE,
                        MediaStore.Audio.Media.ALBUM,
                        MediaStore.Audio.Media.SIZE,
                        MediaStore.Audio.Media.DURATION,
                        MediaStore.Audio.Media.MIME_TYPE,
                        MediaStore.Audio.Media.DISPLAY_NAME,
//                MediaStore.Video.Audio.AUTHOR,//API 30
//                MediaStore.Video.Audio.ALBUM_ARTIST,//API 30
                        MediaStore.Audio.Media.DATA,
                        MediaStore.Audio.Media.DATE_ADDED,
                        MediaStore.Audio.Media.DATE_MODIFIED,
                        MediaStore.Audio.AudioColumns.ALBUM_ID,
                };
                break;
        }
        L.e("getSystemMediaProviderData start:" + uri + "  volume:" + uri.getPathSegments().get(0) + "  AUTHORITY:" + uri.getAuthority());
        Cursor cursor = resolver.query(uri,//外部存储
                projection, null, null, null);
        ArrayList<Files> dataList = new ArrayList<>();
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
            if (type == BaseConstants.SystemMediaType.Audio) {
                info.album_id = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM_ID));
            }
            dataList.add(info);
            L.d("query result:" + info);
        }
        cursor.close();
        L.d("getSystemMediaProviderData result: " + dataList.size());
        return dataList;
    }

    /**
     * android Q以下的获取专辑封面方式
     *
     * @param context
     * @param ids
     * @return
     */
    public static List<Files> getAudioAlbumThumbnail(Context context, String... ids) {
        if (ids == null) return null;
        StringBuilder builder = new StringBuilder();
        builder.append("(");
        for (int i = 0; i < ids.length; i++) {
            builder.append(ids[i]).append(",");
        }
        builder.deleteCharAt(builder.lastIndexOf(","));
        builder.append(")");
        L.d("getAlbumThumbnail " + builder);

        ContentResolver resolver = context.getContentResolver();
        Uri albumUri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        String[] projection = new String[]{
                MediaStore.Audio.Albums._ID,
                MediaStore.Audio.Albums.ALBUM_ART
        };
        String thumbnail = MediaStore.Audio.Albums.ALBUM_ART;
        String idColumn = MediaStore.Audio.Albums._ID;
        String selection = idColumn + " in " + builder;
        Cursor cursor = resolver.query(albumUri, projection, selection, null, null);
        List<Files> files = new ArrayList<>();
        while (cursor.moveToNext()) {
            String id = cursor.getString(cursor.getColumnIndex(idColumn));
            String path = cursor.getString(cursor.getColumnIndex(thumbnail));
            if (path != null && new File(path).exists()) {
                L.d("歌曲专辑图片:album_id=" + id + "  " + path);
                Files file = new Files();
                file.album_id = id;
                file.album_path = path;
                files.add(file);
            }
        }
        cursor.close();
        return files;
    }

    /**
     * android Q及以上的获取专辑封面方式
     *
     * @param albumId
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static Bitmap loadAudioAlbumThumbnail(@NonNull String albumId, String path) {
        Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, Long.parseLong(albumId));
        L.d("loadAudioAlbumThumbnail: " + uri + ", path:" + path);
        Size size = new Size(200, 200);
        ContentResolver resolver = App.getContext().getContentResolver();
        try {
            return resolver.loadThumbnail(uri, size, null);
        } catch (IOException e) {
            e.printStackTrace();
            L.e("loadAudioAlbumThumbnail IOException:" + e.getMessage());
        }
        return null;
    }
}
