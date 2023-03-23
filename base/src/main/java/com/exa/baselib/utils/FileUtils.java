package com.exa.baselib.utils;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @Author lsh
 * @Date 2023/3/23 10:43
 * @Description
 */
public class FileUtils {

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
}
