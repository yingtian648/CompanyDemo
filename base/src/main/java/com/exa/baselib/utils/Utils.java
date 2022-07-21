package com.exa.baselib.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;

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
}
