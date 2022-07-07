package com.exa.companydemo.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;

import com.exa.baselib.utils.L;

public class Utils {
    /**
     * 获取封面
     *
     * @param path
     * @return
     */
    public static Bitmap getCover(String path) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        try {
            mmr.setDataSource(path);
            byte[] cover = mmr.getEmbeddedPicture();
            if (cover != null) {
                L.d("封面大小:" + cover.length);
                return BitmapFactory.decodeByteArray(cover, 0, cover.length);
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            L.d("onViewHolder mmr.setDataSource err");
        } finally {
            mmr.release();
        }
        return null;
    }
}
