package com.exa.companydemo.utils;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.util.SparseArray;

import com.exa.baselib.utils.L;
import com.exa.companydemo.mediaprovider.FilesDao;
import com.exa.companydemo.mediaprovider.entity.Files;
import com.exa.companydemo.musicload.MediaInfo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Tools {

    public static Runnable insertRunnable(Context context) {
        return () -> {
            ArrayList<Files> files = new ArrayList<>();
            for (int i = 1; i < 1000; i++) {
                files.add(makeFiles(i + 1));
            }
            FilesDao dao = new FilesDao(context);
            dao.insertBySQLiteStatement(files);
        };
    }

    public static Files makeFiles(int index) {
        Files files = new Files();
        long time = System.currentTimeMillis();
        files.name = "不要说话";
        files.add_time = index;
        files.modify_time = index;
        files.size = 2155421;
        files.duration = 315000;
        files.width = 0;
        files.height = 0;
        files.file_type = 1;
        files.path = "/sd/dsds/" + index;
        files.root_dir = "/sd/dsds/" + index;
        files.mime_type = "video/mp4";
        files.artist = "陈奕迅";
        files.album = "SHDJSUNHDJSHJDS";
        files.display_name = String.valueOf(index);
        files.tags = null;
        return files;
    }

    public static SparseArray<MediaInfo> loadFileAttrs(File[] files) {
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
                mmr.setDataSource(file.getCanonicalPath());
                entry.author = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_AUTHOR);
                entry.album = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
                entry.artList = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                entry.albumArtist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST);
                entry.composer = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_COMPOSER);
//                String genre = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE);
//                entry.handleStringTag("genre", genre);
                entry.title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                getGbkStr(entry.title, entry.artList);
//                String year = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_YEAR);
//                entry.handleStringTag("year", year);
                String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                entry.duration = duration == null ? 0 : Integer.parseInt(duration);
                entry.mimeType = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);
//                String writer = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_WRITER);
//                entry.handleStringTag("writer", writer);
//                String compilation = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_COMPILATION);
//                entry.handleStringTag("compilation", compilation);
                String width = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
                entry.width = width == null ? 0 : Integer.parseInt(width);
                String height = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
                entry.height = height == null ? 0 : Integer.parseInt(height);
//                L.d("paytime:" + (System.currentTimeMillis() - start) + "\u3000\u3000" + file.getAbsolutePath());
            } catch (IllegalArgumentException | IOException e) {
                e.printStackTrace();
                L.e("mmr.setDataSource err:" + e.getMessage());
            }
            datas.append(index, entry);
            index++;
            L.d("scanFile:" + entry);
        }
        return datas;
    }

    public static String getGbkStr(String title, String artist) {
        try {
            if (title.equals(new String(title.getBytes("iso8859-1"), "iso8859-1"))) {
                title = new String(title.getBytes("iso8859-1"), "gb2312");
            }
            if (artist.equals(new String(artist.getBytes("iso8859-1"), "iso8859-1"))) {
                artist = new String(artist.getBytes("iso8859-1"), "gb2312");
            }
        } catch (Exception e) {

        }
        L.d("getGbkStr:" + title + ",artist:" + artist);
        return title;
    }
}
