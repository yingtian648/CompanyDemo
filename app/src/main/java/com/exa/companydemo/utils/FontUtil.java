package com.exa.companydemo.utils;

import android.content.Context;
import android.graphics.fonts.FontFamilyUpdateRequest;
import android.graphics.fonts.FontFileUpdateRequest;
import android.graphics.fonts.FontManager;
import android.graphics.fonts.FontStyle;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import com.exa.baselib.utils.L;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import static android.graphics.fonts.FontStyle.FONT_SLANT_UPRIGHT;
import static android.graphics.fonts.FontStyle.FONT_WEIGHT_NORMAL;
import static android.os.Environment.DIRECTORY_PICTURES;

/**
 * @author lsh
 * @date 2024/3/25 16:42
 * @description fsverity签名工具
 * https://github.com/ebiggers/fsverity-utils
 */
public class FontUtil {
    private static final String TEMP_DIR = "/data/local/tmp/";
    private static final String FONT_FILE = "NotoColorEmoji.ttf";
    private static final String FONT_FILE_SIG = "UpdatableSystemFontTestNotoColorEmoji.ttf.fsv_sig";
    private static final String FONT_FILE_AION = "AIONTypeRegular.otf";
    private static final String FONT_FILE_AION_SIG = "UpdatableSystemFontTestAIONTypeRegular.ttf.fsv_sig";
    private static final File PICTURE_FILE = Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES);

    public static void updateFontFamily(Context context) {
        FontManager fm = context.getSystemService(FontManager.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                File fileR = new File(TEMP_DIR + FONT_FILE);
                File sigR = new File(TEMP_DIR + FONT_FILE_SIG);
//                File fileL = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES), FONT_FILE_AION);
//                File fileB = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES), "AIONType-bold.otf");
                ParcelFileDescriptor fdR = ParcelFileDescriptor.open(fileR, ParcelFileDescriptor.MODE_READ_ONLY);
//                ParcelFileDescriptor fdL = ParcelFileDescriptor.open(fileL, ParcelFileDescriptor.MODE_READ_ONLY);
//                ParcelFileDescriptor fdB = ParcelFileDescriptor.open(fileB, ParcelFileDescriptor.MODE_READ_ONLY);
                byte[] signature = Files.readAllBytes(Paths.get(sigR.getAbsolutePath()));
                int result = fm.updateFontFamily(new FontFamilyUpdateRequest.Builder()
                        .addFontFileUpdateRequest(new FontFileUpdateRequest(fdR, signature))
//                        .addFontFileUpdateRequest(new FontFileUpdateRequest(fdL, signature))
//                        .addFontFileUpdateRequest(new FontFileUpdateRequest(fdB, signature))
                        .addFontFamily(
                                new FontFamilyUpdateRequest.FontFamily.Builder("AIONType",
                                        Arrays.asList(
                                                new FontFamilyUpdateRequest.Font.Builder(
                                                        "AIONType-regular",
                                                        new FontStyle(FONT_WEIGHT_NORMAL, FONT_SLANT_UPRIGHT)
                                                ).build(),
                                                new FontFamilyUpdateRequest.Font.Builder(
                                                        "AIONType-light",
                                                        new FontStyle(FONT_WEIGHT_NORMAL, FONT_SLANT_UPRIGHT)
                                                ).build(),
                                                new FontFamilyUpdateRequest.Font.Builder(
                                                        "AIONType-bold",
                                                        new FontStyle(FONT_WEIGHT_NORMAL, FONT_SLANT_UPRIGHT)
                                                ).build()
                                        )).build()
                        )
                        .build(), fm.getFontConfig().getConfigVersion());
                Log.w(L.TAG, "updateFontFamily: " + getResultStr(result));
            } catch (Exception e) {
                Log.e(L.TAG, "updateFontFamily error", e);
            }
        }
    }

    private static String getResultStr(int result) {
        switch (result) {
            case FontManager.RESULT_SUCCESS:
                return "成功";
            case FontManager.RESULT_ERROR_FAILED_TO_WRITE_FONT_FILE:
                return "写入字体文件失败";
            case FontManager.RESULT_ERROR_VERIFICATION_FAILURE:
                return "验签失败";
            case FontManager.RESULT_ERROR_INVALID_FONT_FILE:
                return "无效字体文件";
            case FontManager.RESULT_ERROR_INVALID_FONT_NAME:
                return "无效字体名称";
            case FontManager.RESULT_ERROR_DOWNGRADING:
                return "RESULT_ERROR_DOWNGRADING";
            case FontManager.RESULT_ERROR_FAILED_UPDATE_CONFIG:
                return "更新配置失败";
            case FontManager.RESULT_ERROR_FONT_UPDATER_DISABLED:
                return "禁用字体更新";
            case FontManager.RESULT_ERROR_VERSION_MISMATCH:
                return "版本异常";
            case FontManager.RESULT_ERROR_FONT_NOT_FOUND:
                return "字体未找到";
        }
        return "失败" + result;
    }

    public static void updateSystemFontFamily(Context context) {
        // 写一个更新系统字体的方法
        // 1. 获取FontManager
        FontManager fm = context.getSystemService(FontManager.class);
        // 2. 创建字体文件
        File file = new File(TEMP_DIR + FONT_FILE);
        File sig = new File(TEMP_DIR + FONT_FILE_SIG);
        try {
            // 3. 打开字体文件
            ParcelFileDescriptor fd = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
            // 4. 读取签名
            byte[] signature = Files.readAllBytes(Paths.get(sig.getAbsolutePath()));
            // 5. 更新字体
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                int result = fm.updateFontFamily(new FontFamilyUpdateRequest.Builder()
                        .addFontFileUpdateRequest(new FontFileUpdateRequest(fd, signature))
                        .addFontFamily(
                                new FontFamilyUpdateRequest.FontFamily.Builder("NotoColorEmoji",
                                        Arrays.asList(
                                                new FontFamilyUpdateRequest.Font.Builder(
                                                        "NotoColorEmoji",
                                                        new FontStyle(FONT_WEIGHT_NORMAL, FONT_SLANT_UPRIGHT)
                                                ).build()
                                        )).build()
                        )
                        .build(), fm.getFontConfig().getConfigVersion());
                Log.w(L.TAG, "updateFontFamily: " + getResultStr(result));
            }
        } catch (IOException e) {
            Log.e(L.TAG, "updateFontFamily error", e);
        }
    }

}
