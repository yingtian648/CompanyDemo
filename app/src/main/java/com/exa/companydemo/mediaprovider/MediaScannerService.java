package com.exa.companydemo.mediaprovider;

import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.media.MediaMetadataRetriever;
import android.os.IBinder;
import android.util.SparseArray;

import com.exa.baselib.BaseConstants;
import com.exa.baselib.utils.L;
import com.exa.companydemo.Constants;
import com.exa.companydemo.mediaprovider.entity.Files;
import com.exa.companydemo.musicload.MediaInfo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import androidx.annotation.Nullable;

public class MediaScannerService extends Service {
    private FilesDao dao;
    private final BitmapFactory.Options mBitmapOptions = new BitmapFactory.Options();

    @Override
    public void onCreate() {
        super.onCreate();
        dao = new FilesDao(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        doScan(intent);
        return Service.START_REDELIVER_INTENT;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void doScan(Intent intent) {
        String mroot = Constants.FILE_DIR_MUSIC;
        if (intent != null && intent.getExtras() != null) {
            mroot = intent.getExtras().getString("path");
        }
        L.d("start doScan:" + mroot);
        long startTime = System.currentTimeMillis();
        File file = new File(mroot);
        final File[] files = file.listFiles();
        AtomicInteger total = new AtomicInteger();
        if (files != null) {
            Constants.getFixPool().execute(() -> {
                dao.deleteAll();//清除数据库中所有记录
                SparseArray<MediaInfo> musicList = loadFileAttrs(files);
                L.d("loadFileAttrs complete " + files.length + "\u3000\u3000" + (System.currentTimeMillis() - startTime));
                if (musicList.size() > 0) {
                    ArrayList<Files> filesList = new ArrayList<>();
                    for (int i = 0; i < musicList.size(); i++) {
                        Files mf = new Files();
                        mf.name = musicList.get(i).title;
                        mf.path = musicList.get(i).path;
                        mf.size = musicList.get(i).size;
                        mf.display_name = musicList.get(i).displayName;
                        mf.width = musicList.get(i).width;
                        mf.height = musicList.get(i).height;
                        mf.mime_type = musicList.get(i).mimeType;
                        mf.album = musicList.get(i).album;
                        mf.duration = musicList.get(i).duration;
                        filesList.add(mf);
                    }
                    dao.insertByContentValues(filesList);//插入Provider数据库
                    total.set(filesList.size());
                    L.d("end doScan:" + total.get() + "  payTime:" + (System.currentTimeMillis() - startTime));
                }
                sendFinishBroadCast();
                getContentResolver().notifyChange(BaseConstants.CUSTOMER_URI, null);
            });
        } else {
            sendFinishBroadCast();
            L.d("end doScan:0" + "  payTime:" + (System.currentTimeMillis() - startTime));
        }
    }

    /**
     * 扫描完成广播
     */
    private void sendFinishBroadCast() {
        L.dd(BaseConstants.ACTION_MY_PROVIDER_SCAN_FINISH);
        Intent intent = new Intent(BaseConstants.ACTION_MY_PROVIDER_SCAN_FINISH);
        sendBroadcast(intent);
    }

    private SparseArray<MediaInfo> loadFileAttrs(File[] files) {
        SparseArray<MediaInfo> datas = new SparseArray<>();
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        int index = 0;
        for (File file : files) {
            long start = System.currentTimeMillis();
            MediaInfo entry = new MediaInfo();
            entry.size = file.length();
            entry.displayName = file.getName();
            entry.path = file.getAbsolutePath();
            try {
                mmr.setDataSource(file.getAbsolutePath());
                String author = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_AUTHOR);
                entry.author = author;
                String album = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
                entry.album = album;
                String artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                entry.artList = artist;
                String albumartist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST);
                entry.albumArtist = albumartist;
                String composer = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_COMPOSER);
                entry.composer = composer;
//                String genre = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE);
//                entry.handleStringTag("genre", genre);
                String title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                entry.title = title;
//                String year = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_YEAR);
//                entry.handleStringTag("year", year);
                String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                entry.duration = duration == null ? 0 : Integer.parseInt(duration);
                String mimeType = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);
                entry.mimeType = mimeType;
//                String writer = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_WRITER);
//                entry.handleStringTag("writer", writer);
//                String compilation = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_COMPILATION);
//                entry.handleStringTag("compilation", compilation);
                String width = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
                entry.width = width == null ? 0 : Integer.parseInt(width);
                String height = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
                entry.height = height == null ? 0 : Integer.parseInt(height);
//                L.d("paytime:" + (System.currentTimeMillis() - start) + "\u3000\u3000" + file.getAbsolutePath());
                datas.append(index, entry);
                index++;
//                L.v("scanfile:" + entry);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                L.e("mmr.setDataSource err");
            }
        }
        return datas;
    }


    private SparseArray<MediaInfo> processImageFileInDir(File[] files) {
        SparseArray<MediaInfo> datas = new SparseArray<>();
        int index = 0;
        for (File file : files) {
            MediaInfo entry = new MediaInfo();
            entry.size = file.length();
            entry.title = file.getName();
            entry.path = file.getAbsolutePath();
            if (entry.path.endsWith("jpeg") || entry.path.endsWith("JPEG") || entry.path.endsWith("raw") || entry.path.endsWith("RAW"))
                try {
                    ExifInterface exif = new ExifInterface(entry.path);//获取经纬度
                    float[] location = new float[2];
                    if (exif.getLatLong(location)) {
                        entry.lat = location[0];
                        entry.lon = location[1];
                    }
                } catch (IOException ex) {
                    L.e("EprocessImageFileInDir xifInterface err");
                }
            try {
                mBitmapOptions.outWidth = 0;
                mBitmapOptions.outHeight = 0;
                BitmapFactory.decodeFile(file.getAbsolutePath(), mBitmapOptions);
                entry.width = mBitmapOptions.outWidth;
                entry.height = mBitmapOptions.outHeight;
                datas.append(index, entry);
                index++;
            } catch (Throwable th) {
                th.printStackTrace();
                L.e("processImageFileInDir err");
            }
        }
        return datas;
    }
}
