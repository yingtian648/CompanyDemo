package com.exa.companydemo.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.util.Base64;
import android.util.SparseArray;
import android.util.Xml;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.exa.baselib.utils.L;
import com.exa.companydemo.mediaprovider.FilesDao;
import com.exa.companydemo.mediaprovider.entity.Files;
import com.exa.companydemo.musicload.MediaInfo;
import com.exa.companydemo.test.PhoneManagerServiceTemp;

import org.xmlpull.v1.XmlPullParser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class Tools {

    public static void setPrivateField(Object object, String fieldName, Object fieldValue) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(object, fieldValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param res
     * @return 双精度的double字符串
     */
    public static double getDoubleFormatD(double res) {
        java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");
        return Double.parseDouble(df.format(res));
    }

    public static void goHome(Context context) {
        Intent homeIntent = new Intent("android.intent.action.MAIN");
        homeIntent.addCategory("android.intent.category.HOME");
        homeIntent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(homeIntent);
    }

    /**
     * @param packageName 包名
     */
    public static void uninstall(Context context, String packageName) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_DELETE);
        intent.setData(Uri.parse("package:" + packageName));
        context.startActivity(intent);
    }


    // Window截屏
    public static void screenshotView(Window window, ImageView imageView) {
        // ⬇⬇⬇ 可直接放入点击事件中 ⬇⬇⬇
        View view = window.getDecorView(); // view可以替换成你需要截图的控件，如父控件 RelativeLayout，LinearLayout
        // view.setDrawingCacheEnabled(true); // 设置缓存，可用于实时截图
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        // view.setDrawingCacheEnabled(false); // 清空缓存，可用于实时截图

        String bitmapString = getBitmapString(bitmap); // 位图转 Base64 String
        byte[] drawByte = getBitmapByte(bitmap); // 位图转为 Byted
        imageView.setImageBitmap(bitmap); // ImageView控件直接图片展示截好的图片
    }

    // 位图转 Base64 String
    private static String getBitmapString(Bitmap bitmap) {
        String result = null;
        ByteArrayOutputStream out = null;
        try {
            if (bitmap != null) {
                out = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);

                out.flush();
                out.close();

                byte[] bitmapBytes = out.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT); // Base64.DEFAULT会自动换行，传给前端会报错，所以要用Base64.NO_WRAP
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.flush();
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    // 位图转 Byte
    private static byte[] getBitmapByte(Bitmap bitmap) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        // 参数1转换类型，参数2压缩质量，参数3字节流资源
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        try {
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out.toByteArray();
    }


    public static Runnable insertRunnable(Context context) {
        return () -> {
            ArrayList<Files> files = new ArrayList<>();
            for (int i = 1; i < 1000; i++) {
                files.add(makeFiles(i + 1));
            }
            FilesDao dao = new FilesDao(context);
            dao.insertBySQLiteStatement(files);
        };
    }

    public static Files makeFiles(int index) {
        Files files = new Files();
        long time = System.currentTimeMillis();
        files.name = "不要说话";
        files.add_time = index;
        files.modify_time = index;
        files.size = 2155421;
        files.duration = 315000;
        files.width = 0;
        files.height = 0;
        files.file_type = 1;
        files.path = "/sd/dsds/" + index;
        files.root_dir = "/sd/dsds/" + index;
        files.mime_type = "video/mp4";
        files.artist = "陈奕迅";
        files.album = "SHDJSUNHDJSHJDS";
        files.display_name = String.valueOf(index);
        files.tags = null;
        return files;
    }

    public static SparseArray<MediaInfo> loadFileAttrs(File[] files) {
        SparseArray<MediaInfo> datas = new SparseArray<>();
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        int index = 0;
        for (File file : files) {
            long start = System.currentTimeMillis();
            MediaInfo entry = new MediaInfo();
            entry.size = file.length();
            entry.displayName = file.getName();
            entry.path = file.getAbsolutePath();
            try {
                mmr.setDataSource(file.getCanonicalPath());
                entry.author = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_AUTHOR);
                entry.album = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
                entry.artList = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                entry.albumArtist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST);
                entry.composer = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_COMPOSER);
//                String genre = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE);
//                entry.handleStringTag("genre", genre);
                entry.title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                getGbkStr(entry.title, entry.artList);
//                String year = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_YEAR);
//                entry.handleStringTag("year", year);
                String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                entry.duration = duration == null ? 0 : Integer.parseInt(duration);
                entry.mimeType = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);
//                String writer = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_WRITER);
//                entry.handleStringTag("writer", writer);
//                String compilation = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_COMPILATION);
//                entry.handleStringTag("compilation", compilation);
                String width = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
                entry.width = width == null ? 0 : Integer.parseInt(width);
                String height = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
                entry.height = height == null ? 0 : Integer.parseInt(height);
//                L.d("paytime:" + (System.currentTimeMillis() - start) + "\u3000\u3000" + file.getAbsolutePath());
            } catch (IllegalArgumentException | IOException e) {
                e.printStackTrace();
                L.e("mmr.setDataSource err:" + e.getMessage());
            }
            datas.append(index, entry);
            index++;
            L.d("scanFile:" + entry);
        }
        return datas;
    }

    public static String getGbkStr(String title, String artist) {
        try {
            if (title.equals(new String(title.getBytes("iso8859-1"), "iso8859-1"))) {
                title = new String(title.getBytes("iso8859-1"), "gb2312");
            }
            if (artist.equals(new String(artist.getBytes("iso8859-1"), "iso8859-1"))) {
                artist = new String(artist.getBytes("iso8859-1"), "gb2312");
            }
        } catch (Exception e) {

        }
        L.d("getGbkStr:" + title + ",artist:" + artist);
        return title;
    }

    /**
     * parseXmlFile
     *
     * @return
     */
    public static ArrayList<PhoneManagerServiceTemp.CustomerWindowType> parseXmlFile(String path) {
        File configFile = new File(path);
        if (!configFile.exists()) {
            L.e("configFile: " + configFile.getAbsolutePath() + " isn't exist!");
            return null;
        }
        ArrayList<PhoneManagerServiceTemp.CustomerWindowType> results = new ArrayList<>();
        FileInputStream in = null;
        try {
            in = new FileInputStream(configFile);
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(in, StandardCharsets.UTF_8.name());
            int eventType = parser.getEventType();
            ArrayList<String> sceneType = new ArrayList<>();
            String packageName = null;
            String windowTitle = null;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        // start parse xml
                        break;
                    case XmlPullParser.START_TAG:
                        String name = parser.getName();
                        if ("item".equals(parser.getName())) {
                            sceneType.clear();
                            packageName = null;
                            windowTitle = null;
                        }
                        if ("window_type".equals(name)) {
                            String st = parser.nextText();
                            if (st != null) {
                                sceneType.add(parser.nextText());
                            }
                        }
                        if ("package_name".equals(name)) {
                            packageName = parser.nextText();
                        }
                        if ("title".equals(name)) {
                            windowTitle = parser.nextText();
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if ("item".equals(parser.getName()) && packageName != null) {
                            //添加结果
                        }
                        break;
                    default:
                        break;
                }
                eventType = parser.next();
            }
        } catch (Exception exp) {
            L.e("parseSceneInfos failed!", exp);
            results = null;
        } finally {
//            IoUtils.closeQuietly(in);
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return results;
    }

    /**
     * Use prefixed constants (static final values) on given class to turn value
     * into human-readable string.
     */
    public static String valueToString(Class<?> clazz, String prefix, int value) {
        for (Field field : clazz.getDeclaredFields()) {
            final int modifiers = field.getModifiers();
            if (Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers)
                    && field.getType().equals(int.class) && field.getName().startsWith(prefix)) {
                try {
                    if (value == field.getInt(null)) {
                        return constNameWithoutPrefix(prefix, field);
                    }
                } catch (IllegalAccessException ignored) {
                }
            }
        }
        return Integer.toString(value);
    }

    /**
     * Use prefixed constants (static final values) on given class to turn flags
     * into human-readable string.
     */
    public static String flagsToString(Class<?> clazz, String prefix, int flags) {
        final StringBuilder res = new StringBuilder();
        boolean flagsWasZero = flags == 0;

        for (Field field : clazz.getDeclaredFields()) {
            final int modifiers = field.getModifiers();
            if (Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers)
                    && field.getType().equals(int.class) && field.getName().startsWith(prefix)) {
                try {
                    final int value = field.getInt(null);
                    if (value == 0 && flagsWasZero) {
                        return constNameWithoutPrefix(prefix, field);
                    }
                    if (value != 0 && (flags & value) == value) {
                        flags &= ~value;
                        res.append(constNameWithoutPrefix(prefix, field)).append('|');
                    }
                } catch (IllegalAccessException ignored) {
                }
            }
        }
        if (flags != 0 || res.length() == 0) {
            res.append(Integer.toHexString(flags));
        } else {
            res.deleteCharAt(res.length() - 1);
        }
        return res.toString();
    }

    private static String constNameWithoutPrefix(String prefix, Field field) {
        return field.getName().substring(prefix.length());
    }
}
