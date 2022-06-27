package com.exa.companydemo.musicload;

import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.view.View;

import com.exa.companydemo.Constants;
import com.exa.companydemo.R;
import com.exa.companydemo.base.BaseBindActivity;
import com.exa.companydemo.base.adapter.BaseRecyclerAdapter;
import com.exa.companydemo.databinding.ActivityMusicLoadBinding;
import com.exa.companydemo.utils.L;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

public class MediaLoadActivity extends BaseBindActivity<ActivityMusicLoadBinding> {
    private ArrayList<MediaInfo> dataList;
    private ExecutorService pool;
    private final BitmapFactory.Options mBitmapOptions = new BitmapFactory.Options();

    @Override
    protected int setContentViewLayoutId() {
        pool = Executors.newSingleThreadExecutor();
        return R.layout.activity_music_load;
    }

    @Override
    protected void initView() {
        dataList = new ArrayList<>();
        bind.recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        bind.recyclerView.setAdapter(new BaseRecyclerAdapter<MediaInfo>(this, R.layout.item_music_load, dataList) {
            @Override
            protected void onViewHolder(@NonNull View view, @NonNull MediaInfo data, int position) {

            }
        });
    }

    @Override
    protected void initData() {
        String mroot = Constants.FILE_DIR_MUSIC;
        File file = new File(mroot);
        File[] files = file.listFiles();
        L.d(mroot + ":" + (files == null ? "null" : files.length));
        if (files != null)
            pool.execute(() -> {
                long startTime = System.currentTimeMillis();
                loadFileAttrs(files);
                L.d("loadFileAttrs complete " + (System.currentTimeMillis() - startTime));
            });

        String iroot = Constants.FILE_DIR_IMAGE;
        File filei = new File(iroot);
        File[] fileis = filei.listFiles();
        L.d(iroot + ":" + (fileis == null ? "null" : fileis.length));
        if (fileis != null)
            pool.execute(() -> {
                long startTime = System.currentTimeMillis();
                processImageFileInDir(fileis);
                L.d("processImageFileInDir complete " + fileis.length + (System.currentTimeMillis() - startTime));
            });
    }

    private void loadFileAttrs(File[] files) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        for (File file : files) {
            MediaInfo entry = new MediaInfo();
            entry.size = file.length();
            entry.path = file.getAbsolutePath();
            try {
                mmr.setDataSource(file.getAbsolutePath());
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
//                String writer = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_WRITER);
//                entry.handleStringTag("writer", writer);
//                String compilation = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_COMPILATION);
//                entry.handleStringTag("compilation", compilation);
                String width = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
                entry.width = width == null ? 0 : Integer.parseInt(width);
                String height = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
                entry.height = height == null ? 0 : Integer.parseInt(height);
                dataList.add(entry);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                L.e("mmr.setDataSource err");
            }
        }
    }

    private void processImageFileInDir(File[] files) {
        for (File file : files) {
            MediaInfo entry = new MediaInfo();
            entry.size = file.length();
            entry.title = file.getName();
            entry.path = file.getAbsolutePath();
            try {
                mBitmapOptions.outWidth = 0;
                mBitmapOptions.outHeight = 0;
                BitmapFactory.decodeFile(file.getAbsolutePath(), mBitmapOptions);
                entry.width = mBitmapOptions.outWidth;
                entry.height = mBitmapOptions.outHeight;
                dataList.add(entry);
            } catch (Throwable th) {
                th.printStackTrace();
                L.e("processImageFileInDir err");
            }
        }
    }
}