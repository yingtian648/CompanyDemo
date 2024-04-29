package com.exa.lsh.library;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * @author  lsh
 * @date  2023/3/23 10:43
 * @description
 */
public class FileUtils {
    private static final String TAG = "FileUtils";
    /** 字节数组容量 */
    private static final int BYTE_POOL_SIZE = 4096;
    /** bitmap最高质量 */
    private static final int BITMAP_MAX_QUALITY = 100;

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
            Log.e(TAG, "createNewFile exception", e);
        }
        return null;
    }

    /**
     * @param context        上下文
     * @param assetsFileName 文件名
     * @param targetPath     输出路径
     * @param replace        true--replace exists,false--it will skip if targetPath exists
     */
    public static void copyAssetsFile(Context context, String assetsFileName, String targetPath, boolean replace) {
        L.df("copyAssetsFile assetsFileName= %s  targetPath= %s", assetsFileName, targetPath);
        try {
            File outFile = new File(targetPath);
            if (!outFile.exists()) {
                Objects.requireNonNull(outFile.getParentFile()).mkdirs();
            } else if (!replace) {
                return;
            }
            InputStream ins = context.getAssets().open(assetsFileName);
            OutputStream out = Files.newOutputStream(Paths.get(targetPath));
            byte[] buffer = new byte[BYTE_POOL_SIZE];
            int len;
            while ((len = ins.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
            out.flush();
            ins.close();
            out.close();
        } catch (IOException e) {
            Log.e(TAG, "copyAssetsFile exception", e);
        }
    }

    /**
     * @param context    上下文
     * @param assetsDir  目录
     * @param targetPath 输出路径
     * @param replace    true--replace exists,false--it will skip if targetPath exists
     */
    public static void copyAssetsDir(Context context, String assetsDir,
                                     String targetPath, boolean replace) {
        L.df("copyAssetsDir assetsFileName= %s  targetPath= %s", assetsDir, targetPath);
        try {
            String[] subs = context.getAssets().list(assetsDir);
            if (subs == null || subs.length == 0) {
                copyAssetsFile(context, assetsDir, targetPath, replace);
            } else {
                for (String child : subs) {
                    String path = assetsDir + File.separator + child;
                    String[] childes = context.getAssets().list(path);
                    if (childes == null || childes.length == 0) {
                        copyAssetsFile(context, path
                                , targetPath + File.separator + child, replace);
                    } else {
                        copyAssetsDir(context, path, targetPath, replace);
                    }
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "copyAssetsDir exception", e);
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
                bitmap.compress(Bitmap.CompressFormat.JPEG, BITMAP_MAX_QUALITY, bos);
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(bos.toByteArray());
                fos.flush();
                fos.close();
                bos.close();
                return true;
            } catch (IOException e) {
                Log.e(TAG, "saveBitmapToImage exception", e);
            }
        }
        return false;
    }
}
