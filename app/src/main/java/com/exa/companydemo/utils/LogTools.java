package com.exa.companydemo.utils;

import android.graphics.fonts.Font;
import android.os.Build;

import com.exa.baselib.BaseConstants;
import com.exa.baselib.utils.L;
import com.exa.companydemo.App;

import java.io.File;
import java.util.Arrays;
import java.util.Set;

import static android.graphics.fonts.SystemFonts.getAvailableFonts;

public class LogTools {
    private LogTools() {
    }

    /**
     * 获取系统字体
     */
    public static void logSystemFonts() {
        L.d("------------logSystemFonts-------------");
        BaseConstants.getFixPool().execute(() -> {//获取字体
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                Set<Font> fonts = getAvailableFonts();
                if (!fonts.isEmpty()) {
                    StringBuilder builder = new StringBuilder();
                    int index = 1;
                    for (Font font : fonts) {
                        builder.append(font.getFile().getName()).append("\t").append((index % 5 == 0 ? "\n" : ""));
                        index++;
                    }
                    L.d("\n" + builder.toString());
                }
            }
        });
    }

    /**
     * 歌曲文件放在下面位置
     * /data/user/0/com.exa.companydemo/files
     */
    public static void logAudioFileAttr() {
        String path = App.getContext().getFilesDir().getPath();
        File file = new File(path);
        L.d("path:" + file.getAbsolutePath());
        if (file.exists()) {
            L.d("path:" + Arrays.toString(file.list()));
            File[] files = file.listFiles();
            if (files != null) {
                com.exa.companydemo.utils.Tools.loadFileAttrs(files);
            }
        }
    }
}
