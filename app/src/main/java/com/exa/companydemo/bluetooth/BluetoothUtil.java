package com.exa.companydemo.bluetooth;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothA2dpSink;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAvrcpController;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadsetClient;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothPbapClient;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import androidx.core.app.ActivityCompat;

import static android.bluetooth.BluetoothAdapter.EXTRA_STATE;

/**
 * @author lsh
 * Date 2024/3/1 15:51
 * Desc 蓝牙自动配对
 * BluetoothPbapClient 电话簿/通讯录
 * BluetoothHeadsetClient 实现HFP协议，主要用于拨打、接听、挂断
 * BluetoothA2dp 音频传输协议
 * BluetoothAvrcpController 用于控制蓝牙音乐的播放暂停
 * BluetoothA2dpSink 连上后接收音频数据，source端作为音频的输入端对音频数据进行编码后，
 * 通过两个设备之间建立的ACL链路发送给对方设备(sink端)，在sink端收到音频数据后，
 * 进行解码操作还原出音频完成audio数据传输。
 */
public class BluetoothUtil {
    private final String TAG = "BluetoothTest";
    private BluetoothManager mBtManager;
    private BluetoothAdapter mBtAdapter;
    private Callback mCallback;
    private boolean mConnecting = false;
    private BluetoothDevice mConnectedDevice;
    private Context mContext;
    private final Object mLock = new Object();
    private Handler mHandler;
    private boolean mEnabled = false;
    private static final int HEADSET_CLIENT = 16;
    private static final int A2DP_SINK = 11;
    private static final int AVRCP_CONTROLLER = 12;
    /**
     * 延时校验是否开始扫描
     */
    private static final int DELAY_CHECK_DISCOVER = 2000;
    private int mRetryTime = 0;
    private BluetoothA2dp mBluetoothA2dp;
    private BluetoothHeadsetClient mBluetoothHeadsetClient;
    private BluetoothA2dpSink mBluetoothA2dpSink;
    private BluetoothAvrcpController mAvrcpController;
    private String mTargetDeviceName;
    private final List<BluetoothDevice> mConnectedList = new ArrayList<>();
    private static final UUID MY_UUID = UUID.randomUUID();
    private Executor mExecutor = Executors.newSingleThreadExecutor();
    private final BluetoothProfile.ServiceListener mServiceListener =
            new BluetoothProfile.ServiceListener() {

                @Override
                public void onServiceConnected(int profile, BluetoothProfile bluetoothProfile) {
                    Log.d(TAG, "mServiceListener onServiceConnected: " + profile);
                    switch (profile) {
                        case BluetoothProfile.A2DP:
                            mBluetoothA2dp = (BluetoothA2dp) bluetoothProfile;
                            break;
                        case HEADSET_CLIENT:
                            mBluetoothHeadsetClient = (BluetoothHeadsetClient) bluetoothProfile;
                            break;
                        case A2DP_SINK:
                            mBluetoothA2dpSink = (BluetoothA2dpSink) bluetoothProfile;
                            mHandler.obtainMessage(H.MSG_BOND_LIST_CHANGE).sendToTarget();
                            break;
                        case AVRCP_CONTROLLER:
                            mAvrcpController = (BluetoothAvrcpController) bluetoothProfile;
                            break;
                        default:
                            break;
                    }
                }

                @Override
                public void onServiceDisconnected(int profile) {
                    Log.d(TAG, "mServiceListener onServiceDisconnected: " + profile);
                    switch (profile) {
                        case BluetoothProfile.A2DP:
                            mBluetoothA2dp = null;
                            break;
                        case HEADSET_CLIENT:
                            mBluetoothHeadsetClient = null;
                            break;
                        case A2DP_SINK:
                            mBluetoothA2dpSink = null;
                            break;
                        case AVRCP_CONTROLLER:
                            mAvrcpController = null;
                            break;
                        default:
                            break;
                    }
                }
            };

    public void setCallback(Callback callback) {
        this.mCallback = callback;
    }

    public void setEnable(boolean enable) {
        if (mBtAdapter == null) {
            mHandler.obtainMessage(H.MSG_ON_FAIL, "该设备不支持蓝牙功能").sendToTarget();
            return;
        }
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_CONNECT)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "enable: 权限不足");
            mHandler.obtainMessage(H.MSG_ON_FAIL, "权限不足").sendToTarget();
            return;
        }
        if (enable) {
            if (!mBtAdapter.isEnabled()) {
                mBtAdapter.enable();
            }
        } else {
            if (mBtAdapter.isEnabled()) {
                mBtAdapter.disable();
            }
        }
    }

    @SuppressLint("MissingPermission")
    public void disConnect() {
        Log.d(TAG, "disConnect");
        mExecutor.execute(() -> {
            if (mBluetoothA2dpSink != null) {
                List<BluetoothDevice> devices = mBluetoothA2dpSink.getConnectedDevices();
                for (BluetoothDevice dev : devices) {
                    mBluetoothA2dpSink.disconnect(dev);
                    Log.d(TAG, "mBluetoothA2dpSink disConnect: " + dev.getName());
                }
            }
            if (mBluetoothHeadsetClient != null) {
                List<BluetoothDevice> devices = mBluetoothHeadsetClient.getConnectedDevices();
                for (BluetoothDevice dev : devices) {
                    mBluetoothHeadsetClient.disconnect(dev);
                    Log.d(TAG, "mBluetoothHeadsetClient disConnect: " + dev.getName());
                }
            }
        });
    }

    public boolean removeBondedDevice(BluetoothDevice device) {
        Log.d(TAG, "removeBondedDevice");
        if (device != null) {
            try {
                Method removeBond = BluetoothDevice.class.getMethod("removeBond");
                Boolean result = (Boolean) removeBond.invoke(device);
                Log.d(TAG, "disConnect success? " + result);
                return Boolean.TRUE.equals(result);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                Log.e(TAG, "disConnect: err", e);
            }
        }
        return false;
    }

    @SuppressLint("MissingPermission")
    public void checkToBond(BluetoothDevice device) {
        Log.d(TAG, "checkToBond: " + device + "," + stateToString(device.getBondState()));
        synchronized (mLock) {
            if (device.getBondState() == BluetoothDevice.BOND_NONE) {
                device.createBond();
            } else {
                if (mBluetoothA2dpSink != null) {
                    List<BluetoothDevice> devices = mBluetoothA2dpSink.getConnectedDevices();
                    boolean connected = false;
                    for (BluetoothDevice dev : devices) {
                        if (dev.getName() != null && dev.getName().equals(device.getName())) {
                            connected = true;
                        }
                    }
                    Log.d(TAG, "checkToBond: 是否已连接？" + connected);
                    if (!connected) {
                        connect(device);
                    } else {
                        mConnectedDevice = device;
                        mHandler.obtainMessage(H.MSG_ON_CONNECTED, new MBtDevice(device, 0))
                                .sendToTarget();
                    }
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void connect(BluetoothDevice device) {
        Log.d(TAG, "connect: " + device.getName());
        mExecutor.execute(() -> {
            if (mBluetoothA2dpSink != null) {
                mBluetoothA2dpSink.connect(device);
            }
            if (mBluetoothHeadsetClient != null) {
                mBluetoothHeadsetClient.connect(device);
            }
        });
    }

    public void release() {
        this.mCallback = null;
        if (mBtAdapter != null) {
            mBtAdapter.closeProfileProxy(BluetoothProfile.A2DP, mBluetoothA2dp);
            mBtAdapter.closeProfileProxy(A2DP_SINK, mBluetoothA2dpSink);
            mBtAdapter.closeProfileProxy(AVRCP_CONTROLLER, mAvrcpController);
            mBtAdapter.closeProfileProxy(HEADSET_CLIENT, mBluetoothHeadsetClient);
        }
        mBtAdapter = null;
        mHandler.removeCallbacksAndMessages(null);
        mContext.unregisterReceiver(mBTReceiver);
    }

    public interface Callback {
        /**
         * 蓝牙是否开启
         * @param enable 是否开启
         */
        default void onStateChange(boolean enable) {
        }

        /**
         * 蓝牙是否连接
         * @param connect 是否已连接
         * @param device 连接的设备
         */
        default void onConnectStateChange(boolean connect, MBtDevice device) {
        }

        /**
         * 失败
         * @param msg 错误消息
         */
        default void onFail(String msg) {
        }

        /**
         * 发现设备
         * @param device 设备
         */
        default void onFindDevice(MBtDevice device) {
        }

        /**
         * 绑定列表变化
         * @param devices 设备列表
         */
        default void onBondDeviceChange(List<MBtDevice> devices) {
        }
    }

    public BluetoothUtil(Context ctx) {
        this.mContext = ctx;
        init();
        registerReceiver();
    }

    @SuppressLint("MissingPermission")
    public void init() {
        this.mHandler = new H(Looper.getMainLooper());
        mBtManager = mContext.getSystemService(BluetoothManager.class);
        mBtAdapter = mBtManager.getAdapter();
        if (mBtAdapter == null) {
            mHandler.obtainMessage(H.MSG_ON_FAIL, "该设备不支持蓝牙功能").sendToTarget();
        } else {
            mEnabled = mBtAdapter.isEnabled();
            mHandler.obtainMessage(H.MSG_STATE_CHANGED).sendToTarget();
            mBtAdapter.getProfileProxy(mContext, this.mServiceListener, BluetoothProfile.A2DP);
            mBtAdapter.getProfileProxy(mContext, this.mServiceListener, HEADSET_CLIENT);
            mBtAdapter.getProfileProxy(mContext, this.mServiceListener, A2DP_SINK);
            mBtAdapter.getProfileProxy(mContext, this.mServiceListener, AVRCP_CONTROLLER);
        }
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        filter.addAction(BluetoothA2dpSink.ACTION_CONNECTION_STATE_CHANGED);
        filter.addAction(BluetoothHeadsetClient.ACTION_CONNECTION_STATE_CHANGED);
        filter.addAction(BluetoothPbapClient.ACTION_CONNECTION_STATE_CHANGED);
        mContext.registerReceiver(mBTReceiver, filter);
    }

    private final BroadcastReceiver mBTReceiver = new BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice dev = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            short rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
            int state;
            switch (action) {
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    state = intent.getIntExtra(EXTRA_STATE, 0);
                    Log.d(TAG, "ACTION_STATE_CHANGED state=" + state);
                    // 判断蓝牙功能已经开启
                    if (state == BluetoothAdapter.STATE_ON) {
                        mEnabled = true;
                        mHandler.obtainMessage(H.MSG_STATE_CHANGED).sendToTarget();
                        startScan();
                    } else if (state == BluetoothAdapter.STATE_OFF) {
                        mEnabled = false;
                        mHandler.obtainMessage(H.MSG_STATE_CHANGED).sendToTarget();
                    }
                    break;
                case BluetoothDevice.ACTION_FOUND:
                    mHandler.removeCallbacks(mRetryScanRunnable);
                    if (dev != null) {
                        if (TextUtils.isEmpty(dev.getName())
                                || TextUtils.isEmpty(dev.getName().trim())) {
                            break;
                        }
                        mHandler.obtainMessage(H.MSG_ON_FIND_DEVICE, new MBtDevice(dev, rssi))
                                .sendToTarget();
                        checkDeviceToBond(dev, rssi);
                    }
                    break;
                case BluetoothDevice.ACTION_BOND_STATE_CHANGED:
                    Log.d(TAG, "蓝牙连接状态改变");
                    mHandler.obtainMessage(H.MSG_BOND_LIST_CHANGE).sendToTarget();
                    if (dev != null) {
                        switch (dev.getBondState()) {
                            case BluetoothDevice.BOND_BONDING://正在配对
                                Log.d(TAG, "正在配对......");
                                break;
                            case BluetoothDevice.BOND_BONDED://配对成功
                                mConnectedDevice = dev;
                                mHandler.obtainMessage(H.MSG_ON_CONNECTED, new MBtDevice(dev, rssi))
                                        .sendToTarget();
                                Log.d(TAG, "完成配对");
                                break;
                            case BluetoothDevice.BOND_NONE://取消配对/未配对
                                Log.d(TAG, "取消配对");
                            default:
                                break;
                        }
                    }
                    break;
                case BluetoothDevice.ACTION_PAIRING_REQUEST:
                    Log.d(TAG, "蓝牙设备请求连接");
                    if (dev != null) {
                        dev.setPairingConfirmation(true);
                    }
                    break;
                case BluetoothDevice.ACTION_ACL_CONNECTED:
                    mConnectedDevice = dev;
                    Log.d(TAG, "蓝牙设备已连接");
                    mConnecting = false;
                    mHandler.obtainMessage(H.MSG_ON_CONNECTED, new MBtDevice(dev, rssi))
                            .sendToTarget();
                    mHandler.obtainMessage(H.MSG_BOND_LIST_CHANGE, new MBtDevice(dev, rssi))
                            .sendToTarget();
                    break;
                case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                    Log.d(TAG, "蓝牙设备已断开连接");
                    mConnecting = false;
                    mConnectedDevice = null;
                    mHandler.obtainMessage(H.MSG_ON_DIS_CONNECTED).sendToTarget();
                    mHandler.obtainMessage(H.MSG_BOND_LIST_CHANGE).sendToTarget();
                    break;
                case BluetoothA2dpSink.ACTION_CONNECTION_STATE_CHANGED:
                case BluetoothHeadsetClient.ACTION_CONNECTION_STATE_CHANGED:
                case BluetoothPbapClient.ACTION_CONNECTION_STATE_CHANGED:
                    checkProxyConnectState(intent);
                    break;
                default:
                    break;
            }
        }
    };

    private void checkProxyConnectState(Intent intent) {
        int preState = intent.getIntExtra(BluetoothProfile.EXTRA_PREVIOUS_STATE, 0);
        int state = intent.getIntExtra(BluetoothProfile.EXTRA_STATE, 0);
        Log.d(TAG, "onReceive:" + intent.getAction()
                + ", preState=" + preState + ", state=" + state);
        if (preState == BluetoothProfile.STATE_CONNECTED) {
            Log.d(TAG, "checkProxyConnectState: 已连接 " + intent.getAction());
        } else if (state == BluetoothProfile.STATE_DISCONNECTED) {
            Log.d(TAG, "checkProxyConnectState: 已断开 " + intent.getAction());
        }
    }

    @SuppressLint("MissingPermission")
    private void checkDeviceToBond(BluetoothDevice dev, int rssi) {
        synchronized (mLock) {
            Log.d(TAG, "发现蓝牙设备 " + dev.getName()
                    + ",rssi=" + rssi
                    + ",type=" + dev.getType()
                    + ",address=" + dev.getAddress()
                    + ",bondState=" + stateToString(dev.getBondState())
            );
            if (!mConnecting) {
                if (Objects.equals(dev.getName(), mTargetDeviceName)) {
                    checkToBond(dev);
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    public void startTest(String name) {
        this.mTargetDeviceName = name;
        Log.d(TAG, "startTest mBtAdapter=" + mBtAdapter);
        if (mBtAdapter == null) {
            mHandler.obtainMessage(H.MSG_ON_FAIL, "该设备不支持蓝牙功能").sendToTarget();
            return;
        }
        if (!mBtAdapter.isEnabled()) {
            mBtAdapter.enable();
        } else {
            startScan();
        }
    }

    @SuppressLint("MissingPermission")
    private void startScan() {
        Log.d(TAG, "startScan");
        synchronized (mLock) {
            mRetryTime = 0;
            if (mBtAdapter != null && !mBtAdapter.isDiscovering()) {
                mBtAdapter.startDiscovery();
            }
        }
        checkIsDiscovering();
    }

    private void checkIsDiscovering() {
        mHandler.postDelayed(mRetryScanRunnable, DELAY_CHECK_DISCOVER);
    }

    @SuppressLint("MissingPermission")
    private Runnable mRetryScanRunnable = () -> {
        mRetryTime++;
        Log.d(TAG, "mRetryScanRunnable mRetryTime=" + mRetryTime);
        if (mBtAdapter != null && !mBtAdapter.isDiscovering()) {
            mBtAdapter.startDiscovery();
        }
        checkIsDiscovering();
    };

    @SuppressLint("MissingPermission")
    private void stopScan() {
        Log.d(TAG, "stopScan");
        synchronized (mLock) {
            if (mBtAdapter.isDiscovering()) {
                mBtAdapter.cancelDiscovery();
            }
        }
    }

    private static String stateToString(int state) {
        switch (state) {
            case BluetoothDevice.BOND_NONE:
                return "BOND_NONE";
            case BluetoothDevice.BOND_BONDING:
                return "BOND_BONDING";
            case BluetoothDevice.BOND_BONDED:
                return "BOND_BONDED";
            default:
                break;
        }
        return "UNKNOWN(" + state + ")";
    }

    private final class H extends Handler {
        private static final int MSG_STATE_CHANGED = 1;
        private static final int MSG_ON_FAIL = 2;
        private static final int MSG_ON_FIND_DEVICE = 3;
        private static final int MSG_ON_CONNECTED = 4;
        private static final int MSG_ON_DIS_CONNECTED = 5;
        private static final int MSG_BOND_LIST_CHANGE = 6;

        public H(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_STATE_CHANGED:
                    onStateChange();
                    break;
                case MSG_ON_FAIL:
                    onFail(msg.obj.toString());
                    break;
                case MSG_ON_FIND_DEVICE:
                    onFindDevice((MBtDevice) msg.obj);
                    break;
                case MSG_ON_CONNECTED:
                    onConnectStateChange(true, (MBtDevice) msg.obj);
                    break;
                case MSG_ON_DIS_CONNECTED:
                    onConnectStateChange(false, null);
                    break;
                case MSG_BOND_LIST_CHANGE:
                    onBondListChange();
                    break;
                default:
                    break;
            }
        }

        private void onFindDevice(MBtDevice device) {
            if (mCallback != null) {
                mCallback.onFindDevice(device);
            }
        }

        private void onStateChange() {
            if (mCallback != null) {
                mCallback.onStateChange(mEnabled);
            }
        }

        private void onFail(String msg) {
            if (mCallback != null) {
                mCallback.onFail(msg);
            }
        }

        private void onConnectStateChange(boolean connect, MBtDevice device) {
            if (mCallback != null) {
                mCallback.onConnectStateChange(connect, device);
            }
        }

        @SuppressLint("MissingPermission")
        private void onBondListChange() {
            if (mCallback == null) {
                return;
            }
            mConnectedList.clear();
            List<MBtDevice> list = new ArrayList<>();
            if (mBluetoothA2dpSink != null) {
                List<BluetoothDevice> bds = mBluetoothA2dpSink.getConnectedDevices();
                if (bds != null) {
                    for (BluetoothDevice dev : bds) {
                        Log.d(TAG, "getConnectedDevices: " + dev.getName()
                                + "," + stateToString(dev.getBondState()));
                    }
                    mConnectedList.addAll(bds);
                    if (!bds.isEmpty()) {
                        mConnectedDevice = bds.get(0);
                        mCallback.onConnectStateChange(true,
                                new MBtDevice(mConnectedDevice, true));
                    }
                }
            }
            if (mBtAdapter != null) {
                Set<BluetoothDevice> deviceSet = mBtAdapter.getBondedDevices();
                if (!deviceSet.isEmpty()) {
                    for (BluetoothDevice dev : deviceSet) {
                        Log.d(TAG, "getBondedDevices: " + dev.getName()
                                + "," + stateToString(dev.getBondState()));
                        boolean connected = false;
                        for (BluetoothDevice device : mConnectedList) {
                            if (dev.getName() != null && dev.getName().equals(device.getName())) {
                                connected = true;
                            }
                        }
                        if (mConnectedDevice != null && dev.getName() != null
                                && dev.getName().equals(mConnectedDevice.getName())) {
                            connected = true;
                        }
                        list.add(new MBtDevice(dev, connected));
                    }
                }
            } else {
                for (BluetoothDevice device : mConnectedList) {
                    list.add(new MBtDevice(device, true));
                }
            }
            mCallback.onBondDeviceChange(list);
        }
    }
}
