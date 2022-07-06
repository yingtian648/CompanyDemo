package com.exa.companydemo.musicload;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.exa.companydemo.Constants;
import com.exa.companydemo.R;
import com.exa.companydemo.base.BaseBindActivity;
import com.exa.companydemo.base.adapter.BaseRecyclerAdapter;
import com.exa.companydemo.databinding.ActivityMusicLoadBinding;
import com.exa.companydemo.db.FilesDao;
import com.exa.companydemo.db.entity.Files;
import com.exa.companydemo.utils.L;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class MediaLoadActivity extends BaseBindActivity<ActivityMusicLoadBinding> {
    private ArrayList<MediaInfo> musicList;
    private ArrayList<MediaInfo> imageList;
    private ArrayList<Files> filesList;
    private final BitmapFactory.Options mBitmapOptions = new BitmapFactory.Options();
    private FilesDao dao;

    @Override
    protected int setContentViewLayoutId() {
        return R.layout.activity_music_load;
    }

    @Override
    protected void initView() {
        musicList = new ArrayList<>();
        imageList = new ArrayList<>();
        filesList = new ArrayList<>();
        bind.recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        bind.recyclerView.setAdapter(new BaseRecyclerAdapter<MediaInfo>(this, R.layout.item_music_load, musicList) {
            @Override
            protected void onViewHolder(@NonNull View view, @NonNull MediaInfo data, int position) {
                TextView titleT = view.findViewById(R.id.title);
                ImageView image = view.findViewById(R.id.image);
                TextView artListT = view.findViewById(R.id.artList);
                TextView pathT = view.findViewById(R.id.path);
//                image.setImageBitmap(Utils.getCover(data.path));
                titleT.setText(data.title == null ? data.name : data.title);
                int r = data.duration / 1000;
                artListT.setText(r / 60 + "\'" + r % 60 + "\" " + (data.album == null ? "" : data.album) + " " + (data.size / 1024 + "KB"));
                pathT.setText(data.path);
            }
        });
    }

    @Override
    protected void initData() {
        dao = new FilesDao(this);
        String mroot = Constants.FILE_DIR_MUSIC;
        File file = new File(mroot);
        File[] files = file.listFiles();
        if (files != null)
            Constants.getFixPool().execute(() -> {
                dao.deleteAll();//清除数据库中所有记录
                long startTime = System.currentTimeMillis();
                loadFileAttrs(files);
                L.d("loadFileAttrs complete " + files.length + "\u3000\u3000" + (System.currentTimeMillis() - startTime));
                if (!musicList.isEmpty()) {
                    for (int i = 0; i < musicList.size(); i++) {
                        Files mf = new Files();
                        mf.name = musicList.get(i).name;
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
                    dao.insertFiles2(filesList);//插入Provider数据库
                }
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
        String vroot = Constants.FILE_DIR_VIDEO;
        File fileVideo = new File(vroot);
        if (fileVideo.exists() && fileVideo.isDirectory()) {
            File[] vs = fileVideo.listFiles();
            if (vs != null) {
                Constants.getFixPool().execute(() -> {
                    for (File filev : vs) {
                        boolean success = loadVideoThumbnail(filev.getAbsolutePath());
                        if (success) break;
                    }
                });
            }
        }
    }

    private boolean loadVideoThumbnail(String path) {
        long start = System.currentTimeMillis();

        MediaStore.Video.Thumbnails.getThumbnail(getContentResolver(), 1, MediaStore.Video.Thumbnails.MICRO_KIND, null);
        final Bitmap[] bitmap = {ThumbnailUtils.createVideoThumbnail(path, MediaStore.Video.Thumbnails.MICRO_KIND)};
        if (bitmap[0] == null || bitmap[0].getByteCount() == 0) return false;
        L.d("解析缩略图：" + (System.currentTimeMillis() - start) + " " + bitmap[0].getByteCount());
        runOnUiThread(() -> {
            bind.image.setImageBitmap(bitmap[0]);
            bitmap[0] = null;
        });
        return true;
    }

    private void loadFileAttrs(File[] files) {
        musicList.clear();
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        for (File file : files) {
            long start = System.currentTimeMillis();
            MediaInfo entry = new MediaInfo();
            entry.size = file.length();
            entry.name = file.getName();
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
                imageList.add(entry);
            } catch (Throwable th) {
                th.printStackTrace();
                L.e("processImageFileInDir err");
            }
        }
    }
}