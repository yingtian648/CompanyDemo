package com.exa.baselib.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.exa.baselib.BaseConstants;
import com.exa.baselib.bean.EventBean;

import org.greenrobot.eventbus.EventBus;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

public class Utils {
    public static Bitmap loadVideoThumbnail(Context context, String path) {
        long start = System.currentTimeMillis();
        MediaStore.Video.Thumbnails.getThumbnail(context.getContentResolver(), 1, MediaStore.Video.Thumbnails.MICRO_KIND, null);
        Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Video.Thumbnails.MICRO_KIND);
        L.d("解析缩略图：" + (System.currentTimeMillis() - start) + " " + ((bitmap == null ? 0 : bitmap.getByteCount())));
        return (bitmap == null || bitmap.getByteCount() == 0) ? null : bitmap;
    }

    /**
     * 执行命令
     * doCommand("am broadcast -a android.intent.action.MEDIA_MOUNTED");//发送挂载广播
     * 最终执行的是：adb shell am broadcast -a android.intent.action.MEDIA_MOUNTED
     *
     * @param command
     */
    public static void doCommand(String command) {
        L.d("doCommand:" + command);
        BaseConstants.getSinglePool().execute(() -> {
            int result = -1; //0正常 1失败 -1异常
            DataOutputStream os = null;
            try {
                Process process = Runtime.getRuntime().exec("adb shell");
                os = new DataOutputStream(process.getOutputStream());
                os.writeBytes(command + "\n");
                os.writeBytes("exit\n");
                os.flush();
                process.waitFor();
                result = process.waitFor();
                if (result == 0) {//返回正常
                    InputStreamReader nor = new InputStreamReader(process.getInputStream());
                    LineNumberReader returnDataNor = new LineNumberReader(nor);
                    String line;
                    StringBuilder builder = new StringBuilder();
                    while ((line = returnDataNor.readLine()) != null) {
                        builder.append(line).append("\n");
                    }
                    EventBus.getDefault().post(new EventBean("doCommand success:" + builder));
                    nor.close();
                    L.e("doCommand success: " + 0);
                } else {
                    L.e("doCommand fail: " + command);
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                L.d("doCommand.IOException");
            } finally {
                if (os != null)
                    try {
                        os.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
            L.d("doCommand finish");
        });
    }

    /**
     * 获取字符串编码格式
     *
     * @param str
     * @return
     */
    public static String getEncoding(String str) {
        String encode;
        encode = "UTF-16";
        try {
            if (str.equals(new String(str.getBytes(), encode))) {
                return encode;
            }
        } catch (Exception ignored) {
        }
        encode = "ASCII";
        try {
            if (str.equals(new String(str.getBytes(), encode))) {
                return "字符串<< " + str + " >>中仅由数字和英文字母组成，无法识别其编码格式";
            }
        } catch (Exception ignored) {
        }
        encode = "ISO-8859-1";
        try {
            if (str.equals(new String(str.getBytes(), encode))) {
                return encode;
            }
        } catch (Exception ignored) {
        }
        encode = "GB2312";
        try {
            if (str.equals(new String(str.getBytes(), encode))) {
                return encode;
            }
        } catch (Exception ignored) {
        }
        encode = "UTF-8";
        try {
            if (str.equals(new String(str.getBytes(), encode))) {
                return encode;
            }
        } catch (Exception ignored) {
        }

        /*
         *......待完善
         */

        return "未识别编码格式";
    }

    /**
     * 判断是否是图片文件
     *
     * @param path
     * @return
     */
    public static boolean isImagePath(String path) {
        if (!TextUtils.isEmpty(path)) {
            return path.toLowerCase().matches("(.*).(png|jpg|gif|jpeg|bmp)$");
        }
        return false;
    }

    /**
     * 发送消息
     * @param msg
     */
    public static void postEventMessage(String msg){
        EventBus.getDefault().post(new EventBean(msg));
    }
}
