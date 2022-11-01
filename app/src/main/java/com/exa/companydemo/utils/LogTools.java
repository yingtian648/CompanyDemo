package com.exa.companydemo.utils;

import android.graphics.Typeface;
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
                Typeface def = Typeface.create(Typeface.DEFAULT,Typeface.NORMAL);
                L.d("default fonts: style:" + def.getStyle() + ",weight:" + def.getWeight() + "," + def.equals(Typeface.create("GacFont-Regular",Typeface.NORMAL)));
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
    public static void logAudioFileAttr(String path) {
        if (path == null) {
            path = App.getContext().getFilesDir().getPath();
        }
        File file = new File(path);
        L.d("path:" + file.getAbsolutePath() + "," + (file.exists() ? ("exists," + file.list()) : "not exists"));
        if (file.exists() && file.list() != null) {
            L.d("path:" + Arrays.toString(file.list()));
            File[] files = file.listFiles();
            if (files != null) {
                com.exa.companydemo.utils.Tools.loadFileAttrs(files);
            }
        } else {
            L.e("logAudioFileAttr path is null or has no child");
        }
    }
}
