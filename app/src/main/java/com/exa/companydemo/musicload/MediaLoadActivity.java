package com.exa.companydemo.musicload;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.exa.companydemo.Constants;
import com.exa.companydemo.R;
import com.exa.companydemo.base.BaseBindActivity;
import com.exa.companydemo.base.adapter.BaseRecyclerAdapter;
import com.exa.companydemo.databinding.ActivityMusicLoadBinding;
import com.exa.companydemo.utils.L;
import com.exa.companydemo.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Handler;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

public class MediaLoadActivity extends BaseBindActivity<ActivityMusicLoadBinding> {
    private ArrayList<MediaInfo> musicList;
    private ArrayList<MediaInfo> imageList;
    private final BitmapFactory.Options mBitmapOptions = new BitmapFactory.Options();

    @Override
    protected int setContentViewLayoutId() {
        return R.layout.activity_music_load;
    }

    @Override
    protected void initView() {
        musicList = new ArrayList<>();
        imageList = new ArrayList<>();
        bind.recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        bind.recyclerView.setAdapter(new BaseRecyclerAdapter<MediaInfo>(this, R.layout.item_music_load, musicList) {
            @Override
            protected void onViewHolder(@NonNull View view, @NonNull MediaInfo data, int position) {
                TextView titleT = view.findViewById(R.id.title);
                ImageView image = view.findViewById(R.id.image);
                TextView artListT = view.findViewById(R.id.artList);
                TextView pathT = view.findViewById(R.id.path);
                image.setImageBitmap(Utils.getCover(data.path));
                titleT.setText(data.title == null ? data.name : data.title);
                int r = data.duration / 1000;
                artListT.setText(r / 60 + "\'" + r % 60 + "\" " + (data.album == null ? "" : data.album) + " " + (data.size / 1024 + "KB"));
                pathT.setText(data.path);
            }
        });
    }

    @Override
    protected void initData() {
        String mroot = Constants.FILE_DIR_MUSIC;
        File file = new File(mroot);
        File[] files = file.listFiles();
        if (files != null)
            Constants.getFixPool().execute(() -> {
                long startTime = System.currentTimeMillis();
                loadFileAttrs(files);
                L.d("loadFileAttrs complete " + files.length + "  " + (System.currentTimeMillis() - startTime));
                Constants.getHandler().post(() -> {
                    Collections.reverse(musicList);
                    bind.recyclerView.getAdapter().notifyDataSetChanged();
                });
            });

        String iroot = Constants.FILE_DIR_IMAGE;
        File filei = new File(iroot);
        File[] fileis = filei.listFiles();
        if (fileis != null)
            Constants.getFixPool().execute(() -> {
                long startTime = System.currentTimeMillis();
                processImageFileInDir(fileis);
                L.d("processImageFileInDir complete " + fileis.length + "  " + (System.currentTimeMillis() - startTime));
                Collections.reverse(imageList);
            });
    }

    private void loadFileAttrs(File[] files) {
        musicList.clear();
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        for (File file : files) {
            MediaInfo entry = new MediaInfo();
            entry.size = file.length();
            entry.name = file.getName();
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
                musicList.add(entry);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                L.e("mmr.setDataSource err");
            }
        }
    }


    private void processImageFileInDir(File[] files) {
        imageList.clear();
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
                imageList.add(entry);
            } catch (Throwable th) {
                th.printStackTrace();
                L.e("processImageFileInDir err");
            }
        }
    }
}