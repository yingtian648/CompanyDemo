package com.exa.companydemo;

import android.Manifest;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.widget.Toast;

import com.exa.baselib.BaseConstants;
import com.exa.baselib.base.BaseActivity;
import com.exa.baselib.utils.L;
import com.exa.companydemo.musicload.MediaInfo;
import com.exa.companydemo.utils.PermissionUtil;

import java.io.File;

import androidx.annotation.NonNull;

public class MainActivity extends BaseActivity {

    @Override
    protected void initData() {
        test();
    }

    @Override
    protected void initView() {
        findViewById(R.id.btn).setOnClickListener(view -> {
            L.d("点击Toast测试1");
            Toast.makeText(this, "原生Toast测试", Toast.LENGTH_SHORT).show();
        });
        findViewById(R.id.btn2).setOnClickListener(view -> {
            L.d("点击跳转到第二个页面");
            startActivity(new Intent(this, SecondActivity.class));
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }


    private void test() {
        File dir = new File(BaseConstants.FILE_DIR);
        final File[] files = dir.listFiles();
        if (files != null && files.length > 0) {
            BaseConstants.getSinglePool().execute(() -> {
                MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                for (File file : files) {
                    if (file.isDirectory()) continue;//过滤掉文件夹
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
                        L.d("::::" + entry);
                    } catch (Exception e) {
                        e.printStackTrace();
                        L.e("mmr.setDataSource err");
                    }
                }
                mmr.release();
            });
        }
    }

    private void checkPermission() {
        PermissionUtil.requestPermission(this, () -> {
            L.d("已授权 读写权限");
        }, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE});
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        checkPermission();
    }
}