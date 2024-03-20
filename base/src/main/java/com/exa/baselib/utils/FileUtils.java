package com.exa.baselib.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

/**
 * @Author lsh
 * @Date 2023/3/23 10:43
 * @Description
 */
public class FileUtils {

    /**
     * 创建新文件
     */
    public static File createNewFile(String path) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                Objects.requireNonNull(file.getParentFile()).mkdirs();
            } else {
                file.delete();
            }
            if (file.createNewFile()) {
                return file;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @param context
     * @param assetsFileName
     * @param targetPath 输出路径
     * @param replace true--replace exists,false--it will skip if targetPath exists
     */
    public static void copyAssetsFile(Context context, String assetsFileName, String targetPath, boolean replace) {
        L.df("copyAssetsFile assetsFileName= %s  targetPath= %s", assetsFileName, targetPath);
        try {
            File outFile = new File(targetPath);
            if (!outFile.exists()) {
                outFile.getParentFile().mkdirs();
            } else if (!replace) {
                return;
            }
            InputStream ins = context.getAssets().open(assetsFileName);
            OutputStream out = new FileOutputStream(targetPath);
            byte[] buffer = new byte[4096];
            int len;
            while ((len = ins.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
            out.flush();
            ins.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            L.df("copyAssetsFile: %s IO Err" + e.getMessage(), assetsFileName);
        }
    }

    /**
     *
     * @param context
     * @param assetsDir
     * @param targetPath 输出路径
     * @param replace true--replace exists,false--it will skip if targetPath exists
     */
    public static void copyAssetsDir(Context context, String assetsDir, String targetPath, boolean replace) {
        L.df("copyAssetsDir assetsFileName= %s  targetPath= %s", assetsDir, targetPath);
        try {
            String[] subs = context.getAssets().list(assetsDir);
            if (subs == null || subs.length <= 0) {
                copyAssetsFile(context, assetsDir, targetPath, replace);
            } else {
                for (String child : subs) {
                    String path = assetsDir + File.separator + child;
                    if (context.getAssets().list(path) == null ||
                            context.getAssets().list(path).length <= 0) {
                        copyAssetsFile(context, path, targetPath + File.separator + child, replace);
                    } else {
                        copyAssetsDir(context, path, targetPath, replace);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            L.e("copyAssetsFile IO Err", e);
        }
    }

    /**
     * 保存bitmap为问文件
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static boolean saveBitmapToImage(final Bitmap bitmap, final String savePath) {
        File file = new File(savePath);
        if (bitmap != null && !TextUtils.isEmpty(savePath)) {
            try {
                if (!file.exists()) {
                    Objects.requireNonNull(file.getParentFile()).mkdirs();
                    file.createNewFile();
                }
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(bos.toByteArray());
                fos.flush();
                fos.close();
                bos.close();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
