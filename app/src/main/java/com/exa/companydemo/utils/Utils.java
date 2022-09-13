package com.exa.companydemo.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;

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

    /**
     * 隐藏输入法
     *
     * @param editW
     */
    public static void hideKeyboard(@NonNull EditText editW) {
        editW.clearFocus();
        try {
            InputMethodManager imm = (InputMethodManager) editW.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editW.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
            L.e("hideKeyboard:" + e.getMessage());
        }
    }

    //将文本复制到剪贴板
    public static void copyText(Context context, String text) {
        ClipboardManager clip = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData data = ClipData.newPlainText(null, text);
        clip.setPrimaryClip(data);
    }
}
