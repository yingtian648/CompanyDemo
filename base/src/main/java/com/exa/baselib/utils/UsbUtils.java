package com.exa.baselib.utils;

import android.content.Context;
import android.os.Build;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @Author lsh
 * @Date 2023/5/30 9:34
 * @Description
 */
public class UsbUtils {

    /**
     * U盘格式化
     * 适用于 Android 11 (R)
     *
     * @return true mean success
     */
    public static boolean format(Context context) {
        L.dd();
        StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        try {
            Method StorageManager_getVolumeList = StorageManager.class.getMethod("getVolumeList");
            Method StorageManager_format = StorageManager.class.getMethod("format", String.class);
            Method StorageVolume_getPath = StorageVolume.class.getMethod("getPath");
            Method StorageVolume_getId = StorageVolume.class.getMethod("getId");
            Method StorageVolume_isRemovable = StorageVolume.class.getMethod("isRemovable");
            Object volumes = StorageManager_getVolumeList.invoke(storageManager);
            if (volumes != null) {
                final int length = Array.getLength(volumes);
                for (int i = 0; i < length; i++) {
                    Object volume = Array.get(volumes, i);
                    String path = (String) StorageVolume_getPath.invoke(volume);
                    boolean removeAble = (Boolean) StorageVolume_isRemovable.invoke(volume);
                    String id = (String) StorageVolume_getId.invoke(volume);
                    L.dd("path=" + path + ", id=" + id);
                    if (removeAble && path != null && path.equals("com.exa.123")) {
                        StorageManager_format.invoke(storageManager, id);
                        L.d("usb format finish!");
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            L.de(e);
        }
        return false;
    }

    /**
     * U盘格式化
     *
     * @return
     */
    public static boolean format1(Context context) {
        L.dd();
        StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        try {
            List<StorageVolume> volumeList = storageManager.getStorageVolumes();
            for (StorageVolume volume : volumeList) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    L.dd("isRemovable=" + volume.isRemovable() + ", dir=" + volume.getDirectory() + ", uuid=" + volume.getUuid());
                }
            }
        } catch (Exception e) {
            L.de(e);
        }
        return false;
    }
}
