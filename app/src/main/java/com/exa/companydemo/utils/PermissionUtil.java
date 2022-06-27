package com.exa.companydemo.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * CreateTime 2017/9/26 22:35
 * Author LiuShiHua
 * Description：权限校验——未获取到权限就去申请
 */

public class PermissionUtil {
    private Activity activity;
    public static final int PERMISSION_REQUEST = 0x99;//权限申请码
    public static final int SETTING_REQUEST = 0x100;//APP设置码
    private static PermissionUtil util;

    public interface PermissionListener {
        void permissionGranted();//被同意
    }

    /**
     * 申请授权，当用户拒绝时，会显示默认一个默认的Dialog提示用户
     *
     * @param context
     * @param listener
     * @param permission 要申请的权限
     */
    public static void requestPermission(@NonNull Activity context, @NonNull PermissionListener listener,@NonNull String permission) {
        requestPermission(context, listener, new String[]{permission});
    }

    /**
     * 申请授权，当用户拒绝时，可以设置是否显示Dialog提示用户，也可以设置提示用户的文本内容
     *
     * @param context
     * @param listener
     * @param permission 需要申请授权的权限
     */
    public static void requestPermission(@NonNull Activity context, @NonNull PermissionListener listener, @NonNull String[] permission) {
        if (hasPermission(context, permission)) {
            listener.permissionGranted();
        } else {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                listener.permissionGranted();
            } else {
                ActivityCompat.requestPermissions(context, permission, 0x120);
            }
        }
    }

    /**
     * 判断权限是否授权
     *
     * @param context
     * @param permissions
     * @return
     */
    public static boolean hasPermission(@NonNull Context context, @NonNull String... permissions) {
        if (permissions.length == 0) {
            return false;
        }
        for (String per : permissions) {
            int result = PermissionChecker.checkSelfPermission(context, per);
            if (result != PermissionChecker.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断一组授权结果是否为授权通过
     *
     * @param grantResult
     * @return
     */
    public static boolean isGranted(@NonNull int[] grantResult) {
        if (grantResult.length == 0) {
            return false;
        }
        for (int result : grantResult) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }


    //设置应用的权限
    public static void showAppSettingsDialog(final Activity context) {
        if (context != null)
            new AlertDialog.Builder(context).setTitle("温馨提示")
                    .setMessage("是否进入APP设置界面进行权限设置？")
                    .setNegativeButton("否", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            goToSet(context);
                        }
                    })
                    .setCancelable(true)
                    .show();
    }

    //进入APP设置界面
    public static void goToSet(Activity activity) {
        String SETTINGS_ACTION = "android.settings.APPLICATION_DETAILS_SETTINGS";
        Intent intent = new Intent()
                .setAction(SETTINGS_ACTION)
                .setData(Uri.fromParts("package", activity.getPackageName(), null));
        activity.startActivityForResult(intent, SETTING_REQUEST);
    }

    //获取权限对应的中文提示
    private String getDENIEDPermissionNoticeStr(String permission) {
        switch (permission) {
            case "android.permission.CAMERA":
                return "相机";
            case "android.permission.ACCESS_FINE_LOCATION":
                return "GPS";
            case "android.permission.READ_EXTERNAL_STORAGE":
                return "读写手机存储";
            case "android.permission.WRITE_EXTERNAL_STORAGE":
                return "读写手机存储";
            case "android.permission.READ_PHONE_STATE":
                return "获取手机信息";
            case "android.permission.ACCESS_COARSE_LOCATION":
                return "获取位置";
            case "android.permission.RECORD_AUDIO":
                return "录音";
            case "android.permission.READ_CONTACTS":
                return "读取联系人列表";
            case "android.permission.WRITE_CONTACTS":
                return "写入联系人";
            case "android.permission.ACCESS_WIFI_STATE":
                return "读取WIFI信息";
            case "android.permission.CHANGE_WIFI_STATE":
                return "该表WIFI状态";
            case "android.permission.SEND_SMS":
                return "发送短信";
            case "android.permission.READ_SMS":
                return "读取短信";
            case "android.permission.RECEIVE_SMS":
                return "接收短信";
        }
        return "";
    }

    /**
     * 常见权限
     * 按需要求添加到getDENIEDPermissionNoticeStr()
     *
     android.permission-group.CALENDAR
     android.permission.READ_CALENDAR       //读取用户的日程信息
     android.permission.WRITE_CALENDAR      //写入用户的日程信息

     android.permission-group.CAMERA
     android.permission.CAMERA          //相机

     android.permission-group.CONTACTS
     android.permission.READ_CONTACTS       //读取联系人列表
     android.permission.WRITE_CONTACTS      //写入联系人
     android.permission.GET_ACCOUNTS        //访问GMail账户列表

     android.permission-group.LOCATION
     android.permission.ACCESS_FINE_LOCATION    //GPS定位
     android.permission.ACCESS_COARSE_LOCATION  //通过WiFi或移动基站的方式获取用户错略的经纬度信息

     android.permission.ACCESS_WIFI_STATE       //WiFi接入的状态以及WLAN热点的信息
     android.permission.CHANGE_WIFI_STATE       //改变WiFi状态
     android.permission.BLUETOOTH_ADMIN         //发现和配对新的蓝牙设备

     android.permission-group.MICROPHONE
     android.permission.RECORD_AUDIO            //录音

     android.permission-group.PHONE
     android.permission.READ_PHONE_STATE            //获取手机信息
     android.permission.CALL_PHONE                  //拨号
     android.permission.READ_CALL_LOG               //获取拨号记录
     android.permission.WRITE_CALL_LOG              //写入拨号记录
     com.android.voicemail.permission.ADD_VOICEMAIL
     android.permission.USE_SIP                     //允许程序使用SIP视频服务
     android.permission.PROCESS_OUTGOING_CALLS      //允许程序监视，修改或放弃播出电话

     android.permission-group.SENSORS
     android.permission.BODY_SENSORS

     android.permission-group.SMS               //短信
     android.permission.SEND_SMS                //发送短信
     android.permission.RECEIVE_SMS             //接收短信
     android.permission.READ_SMS                //读取短信
     android.permission.RECEIVE_WAP_PUSH        //接收WAP PUSH信息
     android.permission.RECEIVE_MMS             //接收彩信
     android.permission.READ_CELL_BROADCASTS

     android.permission-group.STORAGE   //读写手机存储卡
     android.permission.READ_EXTERNAL_STORAGE
     android.permission.WRITE_EXTERNAL_STORAGE

     android.permission.CLEAR_APP_CACHE         //清除应用缓存
     android.permission.CLEAR_APP_USER_DATA     //清除应用的用户数据

     android.permission.DELETE_PACKAGES         //允许程序删除应用
     android.permission.READ_FRAME_BUFFER       //读取帧缓存用于屏幕截图
     *
     */
}
