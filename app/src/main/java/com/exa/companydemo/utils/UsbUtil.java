package com.exa.companydemo.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author  lsh
 * Date 2024/3/5 14:14
 * 1.Usb损坏
 * 2.Usb挂载但并非存储设备
 */
public class UsbUtil {
    private final String TAG = "UsbUtil";
    private final int USB_2_PACKET_SIZE = 512;
    private final Context mContext;
    private Callback mCallback;
    private final String usb1Path = "/storage/usb0";
    private final String usb2Path = "/storage/usb1";

    public String getUsb1Path() {
        return usb1Path;
    }

    public String getUsb2Path() {
        return usb2Path;
    }

    public void setCallback(Callback callback) {
        this.mCallback = callback;
    }

    public interface Callback {
        /**
         * usb1，usb2挂载变化
         */
        void onUsbStateChanged(boolean u1Mounted, boolean u2Mounted);

        /**
         * usb1挂载变化
         */
        void onUsb1StateChanged(boolean mounted);

        /**
         * usb2挂载变化
         */
        void onUsb2StateChanged(boolean mounted);
    }

    public UsbUtil(Context ctx) {
        mContext = ctx;
        registerReceiver();
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addDataScheme("file");
        // filter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        // Usb设备异常，没法读取
        // filter.addAction(Intent.ACTION_MEDIA_UNMOUNTABLE);
        // filter.addAction(Intent.ACTION_MEDIA_REMOVED);
        filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_EJECT);
        mContext.registerReceiver(mReceiver, filter);
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "onReceive:" + intent + "," + intent.getData().toString());
            switch (intent.getAction()) {
                case Intent.ACTION_MEDIA_MOUNTED:
                    if (mCallback != null) {
                        if (intent.getData() != null
                                && intent.getData().toString().contains(usb1Path)) {
                            mCallback.onUsb1StateChanged(true);
                        }
                        if (intent.getData() != null
                                && intent.getData().toString().contains(usb2Path)) {
                            mCallback.onUsb2StateChanged(true);
                        }
                    }
                    break;
                case Intent.ACTION_MEDIA_EJECT:
                    if (mCallback != null) {
                        if (intent.getData() != null
                                && intent.getData().toString().contains(usb1Path)) {
                            mCallback.onUsb1StateChanged(false);
                        }
                        if (intent.getData() != null
                                && intent.getData().toString().contains(usb2Path)) {
                            mCallback.onUsb2StateChanged(false);
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * usb设备类型，U盘是UsbConstants.USB_CLASS_MASS_STORAGE
     * usbInterface.getInterfaceClass();
     */
    public boolean isUsb1Mounted() {
        boolean isMounted = new File(usb1Path).exists();
        Log.i(TAG, "Usb3.0挂载? " + isMounted);
        return isMounted;
    }

    public boolean isUsb2Mounted() {
        boolean isMounted = new File(usb2Path).exists();
        Log.i(TAG, "Usb2.0挂载? " + isMounted);
        return isMounted;
    }

    public void getUsbDevices() {
        UsbManager mUsbManager = mContext.getSystemService(UsbManager.class);
        HashMap<String, UsbDevice> map = mUsbManager.getDeviceList();
        if (map.isEmpty()) {
            Log.w(TAG, "no usb device!");
            return;
        }
        Log.i(TAG, "------------device list start-----------");
        for (UsbDevice dev : map.values()) {
            Log.i(TAG, dev.toString());
            int interfaceCount = dev.getInterfaceCount();
            if (interfaceCount > 0) {
                int maxPackSize = 0;
                for (int i = 0; i < interfaceCount; i++) {
                    UsbInterface usbInterface = dev.getInterface(i);
                    if (usbInterface.getEndpointCount() > 0) {
                        for (int j = 0; j < usbInterface.getEndpointCount(); j++) {
                            int size = usbInterface.getEndpoint(j).getMaxPacketSize();
                            if (maxPackSize < size) {
                                maxPackSize = size;
                                Log.i(TAG, "maxPackSize " + maxPackSize);
                            }
                        }
                    }
                }
                if (maxPackSize > USB_2_PACKET_SIZE) {
                    // 2.0
                    Log.i(TAG, "usb 3.0 已挂载");
                } else {
                    // 3.0
                    Log.i(TAG, "usb 2.0 已挂载");
                }
            }
        }
        Log.i(TAG, "------------device list start-----------");
    }

    public List<String> getUsbPathByStorageManager() {
        final StorageManager manager = mContext.getSystemService(StorageManager.class);
        final List<StorageVolume> volumes = manager.getStorageVolumes();
        final List<String> paths = new ArrayList<>();
        volumes.forEach(volume -> {
            File file = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                file = volume.getDirectory();
            } else {
                try {
                    file = (File) StorageVolume.class.getMethod("getPathFile").invoke(volume);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (!volume.isPrimary()) {
                String state = volume.getState();
                Log.i(TAG, "state=" + state
                        + ",path=" + (file != null ? file.getAbsolutePath() : "null")
                        + ",isRemovable=" + volume.isRemovable());
                if(file!=null &&  Environment.MEDIA_MOUNTED.equals(state)){
                    paths.add(file.getAbsolutePath());
                }
            }
        });
        return paths;
    }

    public void release() {
        if (mContext != null) {
            mContext.unregisterReceiver(mReceiver);
        }
        mCallback = null;
    }
}
