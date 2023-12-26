package com.exa.baselib.utils;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.hardware.display.DisplayManager;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.exa.baselib.BaseConstants;
import com.exa.baselib.bean.EventBean;

import org.greenrobot.eventbus.EventBus;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;

import androidx.annotation.NonNull;

import static com.exa.baselib.utils.L.TAG;

public class Utils {

    private void logIpArrivedStatus(String GNSS_SERVER_IP, String TEST_IP) {
        BaseConstants.getHandler().post(new Runnable() {
            @Override
            public void run() {
                try {
                    InetAddress inet = InetAddress.getByName(GNSS_SERVER_IP);
                    L.w("GpsIpArrived=" + inet.isReachable(1000));
                    inet = InetAddress.getByName(TEST_IP);
                    L.w("TestIpArrived=" + inet.isReachable(1000));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static boolean openApp(Context mContext, String packageName) {
        if (packageName != null) {
            try {
                PackageManager packageManager = mContext.getPackageManager();
                Intent intent = packageManager.getLaunchIntentForPackage(packageName);
                if (intent != null) {
                    mContext.startActivity(intent);
                    return true;
                } else {
                    L.e(String.format("openApp err: has not found %s launch activity", packageName));
                }
            } catch (Exception e) {
                e.printStackTrace();
                L.e("openApp err", e);
            }
        }
        return false;
    }

    public static Bitmap loadVideoThumbnail(Context context, String path) {
        long start = System.currentTimeMillis();
        MediaStore.Video.Thumbnails.getThumbnail(context.getContentResolver(), 1, MediaStore.Video.Thumbnails.MICRO_KIND, null);
        Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Video.Thumbnails.MICRO_KIND);
        L.d("解析缩略图：" + (System.currentTimeMillis() - start) + " " + ((bitmap == null ? 0 : bitmap.getByteCount())));
        return (bitmap == null || bitmap.getByteCount() == 0) ? null : bitmap;
    }

    public static <T> T reflexClass(T t, String clsName, Object... objects) {
        try {
            // 获取类
            Class clazz = Class.forName(clsName);
            // 获取只有一个Context参数的构造函数
            Constructor constructor = clazz.getConstructor(Context.class);
            T result = (T) constructor.newInstance(objects);
            return result;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            L.e("reflexClass NoSuchMethodException,", e);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            L.e("reflexClass ClassNotFoundException,", e);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            L.e("reflexClass IllegalAccessException,", e);
        } catch (InstantiationException e) {
            e.printStackTrace();
            L.e("reflexClass InstantiationException,", e);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            L.e("reflexClass InvocationTargetException,", e);
        }
        return null;
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

    /**
     * 发送消息
     *
     * @param msg
     */
    public static void postEventMessage(String msg) {
        EventBus.getDefault().post(new EventBean(msg));
    }

    public static boolean isAppInstalled(Context context, String pkgName) {
        if (pkgName != null) {
            try {
                context.getPackageManager().getPackageInfo(pkgName, 0);
            } catch (Exception e) {
                Log.d(TAG, "isAppInstalled false: " + e.getMessage());
                return false;
            }
        }
        return true;
    }

    /**
     * 设置当前Activity屏幕亮度
     *
     * @param activity
     * @param brightness 0.0-1.0
     */
    public static void setScreenBrightness(Activity activity, float brightness) {
        Window window = activity.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.screenBrightness = brightness;
        window.setAttributes(lp);
    }

    /**
     * 设置当前Activity屏幕亮度
     *
     * @param context
     * @param brightness 0-255
     */
    public static void setSystemScreenBrightness(Context context, int brightness) {
        int currBrightness = 255;
        ContentResolver resolver = context.getContentResolver();
        try {
            int mode = Settings.System.getInt(resolver, Settings.System.SCREEN_BRIGHTNESS_MODE);
            currBrightness = Settings.System.getInt(resolver, Settings.System.SCREEN_BRIGHTNESS);
            Log.d("Tools", "SystemScreenBrightness mode: " + mode + ",curr:" + currBrightness);
            if (mode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {//自动调节屏幕亮度模式值为1
                Settings.System.putInt(resolver, Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
            }
            Settings.System.putInt(resolver, Settings.System.SCREEN_BRIGHTNESS, brightness);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            Log.e("Tools", "setSystemScreenBrightness: " + e.getMessage());
        }
    }

    /**
     * textview edittext
     * 获取密码转换器
     *
     * @return
     */
    public static PasswordTransformationMethod getPasswordTransformationMethod() {
        return new PasswordTransformationMethod() {
            @Override
            public CharSequence getTransformation(CharSequence source, View view) {
                return new PasswordCharSequence(source);
            }

            class PasswordCharSequence implements CharSequence {

                private CharSequence mSource;

                public PasswordCharSequence(CharSequence source) {
                    mSource = source;
                }

                public char charAt(int index) {//设置需要显示得字符
                    return '*';
                }

                public int length() {
                    return mSource.length();
                }

                public CharSequence subSequence(int start, int end) {
                    return mSource.subSequence(start, end); // Return default
                }
            }
        };
    }

    /**
     * 在指定屏幕上启动Activity
     *
     * @param context
     * @param clazz
     * @param displayId
     */
    public static void startActivityByDisplayId(Activity context, Class clazz, int displayId) {
        Display mDisplay = context.getWindowManager().getDefaultDisplay();//默认显示器id 0
        DisplayManager displayManager = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
        Display[] displays = displayManager.getDisplays(null);
        if (displays != null) {
            for (int i = 0; i < displays.length; i++) {
                L.d("更多显示器：" + displays[i].getDisplayId() + ", " + displays[i].getName() + ", " + displays[i].isValid());
            }
        }
        //FLAG_ACTIVITY_LAUNCH_ADJACENT 多屏使用
        Intent intent = new Intent(context, clazz);
        intent.setFlags(Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT | Intent.FLAG_ACTIVITY_NEW_TASK);
        ActivityOptions options = ActivityOptions.makeBasic().setLaunchDisplayId(displayId);
        context.startActivity(intent, options.toBundle());
    }
}
