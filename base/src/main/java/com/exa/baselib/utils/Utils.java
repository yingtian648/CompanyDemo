package com.exa.baselib.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;

public class Utils {
    public static Bitmap loadVideoThumbnail(Context context, String path) {
        long start = System.currentTimeMillis();
        MediaStore.Video.Thumbnails.getThumbnail(context.getContentResolver(), 1, MediaStore.Video.Thumbnails.MICRO_KIND, null);
        Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Video.Thumbnails.MICRO_KIND);
        L.d("解析缩略图：" + (System.currentTimeMillis() - start) + " " + ((bitmap == null ? 0 : bitmap.getByteCount())));
        return (bitmap == null || bitmap.getByteCount() == 0) ? null : bitmap;
    }
}
