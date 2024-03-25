package com.exa.companydemo.wifi;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;

import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

/**
 * @author lsh
 * Date 2024/3/1 13:56
 */
public class WifiUtil {
    private final String TAG = "WifiTest";
    private WifiManager mWifiManager;
    private ConnectivityManager mConnectManager;
    private Callback mCallback;
    private String targetSSID;
    private String targetPwd;
    private boolean mStartScan = false;
    private boolean mConnected = false;
    private boolean mPendingConnect = false;
    private final Object mLock = new Object();
    private Context mContext;
    private boolean mEnable = false;

    public static final String WEP = "WEP";
    public static final String WPA = "WPA";
    public static final String PSK = "PSK";
    public static final String EAP = "EAP";
    private String mIp;

    public void setCallback(Callback callback) {
        this.mCallback = callback;
    }

    public boolean disConnect() {
        boolean result = mWifiManager.disconnect();
        Log.d(TAG, "disConnect: " + result);
        return result;
    }

    public void connect(ScanResult result, String pwd) {
        connectWifiPoint(result, pwd);
    }

    public interface Callback {
        default void onStateChange(boolean enable) {
        }

        default void onConnected(WifiInfo info) {
        }

        default void onDisConnected() {
        }

        default void onError(String msg) {
        }

        default void onScanListChanged(List<MResult> list) {
        }

        default void onWifiIpChanged(String ip) {
        }
    }

    public WifiUtil(Context ctx, Callback callback) {
        this.mCallback = callback;
        this.mContext = ctx;
        init();
    }

    public void init() {
        mWifiManager = mContext.getSystemService(WifiManager.class);
        mConnectManager = mContext.getSystemService(ConnectivityManager.class);
        NetworkRequest request = new NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .build();
        mConnectManager.registerNetworkCallback(request, mNetworkCallback);
        onStateChange();
        registerBroadCast(mContext);
    }

    private void registerBroadCast(Context ctx) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        ctx.registerReceiver(mReceiver, filter);
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: " + intent);
            switch (intent.getAction()) {
                case WifiManager.WIFI_STATE_CHANGED_ACTION:
                    onStateChange();
                    break;
                case WifiManager.SCAN_RESULTS_AVAILABLE_ACTION:
                    onScanListChange();
                    break;
                default:
                    break;
            }
        }
    };

    private void connectWifiPoint(ScanResult result, String pwd) {
        Log.d(TAG, "connectWifiPoint mPendingConnect=" + mPendingConnect + "," + result);
        synchronized (mLock) {
            if (!mPendingConnect) {
                mPendingConnect = true;
                WifiBean bean = new WifiBean();
                bean.set(result);
                WifiConfiguration config = createWifiConfiguration(bean, pwd);
//                mWifiManager.connect(config, mActionListener);
            }
        }
    }

    /**
     * 创建连接配置
     */
    public WifiConfiguration createWifiConfiguration(WifiBean wifiBean, String str) {
        WifiConfiguration wifiConfiguration = new WifiConfiguration();
        if (str == null) {
            wifiConfiguration.hiddenSSID = false;
            wifiConfiguration.status = WifiConfiguration.Status.ENABLED;
            wifiConfiguration.SSID = "\"" + wifiBean.getSSID() + "\"";
            if (wifiBean.getCapabilities().contains(WEP)) {
                wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                wifiConfiguration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
                wifiConfiguration.wepTxKeyIndex = 0;
                wifiConfiguration.wepKeys[0] = "";
            } else if (wifiBean.getCapabilities().contains(PSK)) {
                wifiConfiguration.preSharedKey = "";
            } else if (wifiBean.getCapabilities().contains(EAP)) {
                wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP);
                wifiConfiguration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                wifiConfiguration.allowedPairwiseCiphers.set(1);
                wifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                wifiConfiguration.allowedProtocols.set(0);
                wifiConfiguration.preSharedKey = "";
            } else {
                wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                wifiConfiguration.preSharedKey = null;
            }
        } else {
            wifiConfiguration.allowedAuthAlgorithms.clear();
            wifiConfiguration.allowedGroupCiphers.clear();
            wifiConfiguration.allowedKeyManagement.clear();
            wifiConfiguration.allowedPairwiseCiphers.clear();
            wifiConfiguration.allowedProtocols.clear();
            wifiConfiguration.SSID = "\"" + wifiBean.getSSID() + "\"";
            if (wifiBean.getCapabilities().contains(WEP)) {
                wifiConfiguration.preSharedKey = "\"" + str + "\"";
                wifiConfiguration.hiddenSSID = true;
                wifiConfiguration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
                wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
                wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            } else if (wifiBean.getCapabilities().contains(WPA)) {
                wifiConfiguration.hiddenSSID = true;
                wifiConfiguration.preSharedKey = "\"" + str + "\"";
                wifiConfiguration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                wifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                wifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            } else {
                wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            }
        }
        Log.d(TAG, "createWifiConfiguration: " + wifiConfiguration.toString());
        return wifiConfiguration;
    }


//    private WifiManager.ActionListener mActionListener = new WifiManager.ActionListener() {
//
//        @Override
//        public void onSuccess() {
//            Log.d(TAG, "ActionListener onSuccess");
//            mConnected = true;
//            mPendingConnect = false;
//        }
//
//        @Override
//        public void onFailure(int i) {
//            mPendingConnect = false;
//            Log.d(TAG, "ActionListener onFailure");
//        }
//    };


    private final ConnectivityManager.NetworkCallback mNetworkCallback =
            new ConnectivityManager.NetworkCallback() {
                @Override
                public void onAvailable(@NonNull Network network) {
                    Log.d(TAG, "onAvailable network=" + network);
                    NetworkCapabilities cap = mConnectManager.getNetworkCapabilities(network);
                    if (cap != null && cap.getTransportInfo() != null) {
                        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
                        mConnected = true;
                        if (mCallback != null) {
                            mCallback.onConnected(wifiInfo);
                        }
                    }
                    mPendingConnect = false;
                }

                @Override
                public void onLost(@NonNull Network network) {
                    Log.d(TAG, "onLost network=" + network);
                    mConnected = false;
                    mPendingConnect = false;
                    if (mCallback != null) {
                        mCallback.onDisConnected();
                    }
                }

                @Override
                public void onLinkPropertiesChanged(@NonNull Network network,
                                                    @NonNull LinkProperties linkProperties) {
                    Log.d(TAG, "onLinkPropertiesChanged=" + network + "," + linkProperties);
                    for (LinkAddress a : linkProperties.getLinkAddresses()) {
                        if (a.getAddress() instanceof Inet4Address) {
                            mIp = numericToTextFormat(a.getAddress().getAddress());
                            if (mIp != null && mCallback != null) {
                                mCallback.onWifiIpChanged(mIp);
                            }
                            break;
                        }
                    }
                }
            };

    /**
     * 数字转IP地址
     */
    private String numericToTextFormat(byte[] src) {
        if (src.length != 4) {
            return null;
        }
        return (src[0] & 0xff)
                + "." + (src[1] & 0xff)
                + "." + (src[2] & 0xff)
                + "." + (src[3] & 0xff);
    }


    public void startTest(String ssid, String pwd) {
        Log.d(TAG, "startWifiTest ssid=" + ssid + ",pwd=" + pwd);
        if (ssid == null) {
            if (mCallback != null) {
                mCallback.onError("参数错误：ssid is null,pwd=" + pwd);
            }
            return;
        }
        targetSSID = ssid;
        targetPwd = pwd;
        mStartScan = false;
        setEnable(true);
        startScan();
    }

    private void onStateChange() {
        mEnable = mWifiManager.isWifiEnabled();
        if (mCallback != null) {
            mCallback.onStateChange(mEnable);
        }
        if (mEnable) {
            startScan();
        } else {
            mStartScan = false;
        }
    }

    private void onConnectStateChange() {
        if (mEnable && mCallback != null) {
            mCallback.onConnected(mWifiManager.getConnectionInfo());
        }
    }

    private void onScanListChange() {
        if (ActivityCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.w(TAG, "onScanListChange: 权限不足");
            return;
        }
        List<MResult> list = filterResultList(mWifiManager.getScanResults());
        for (MResult result : list) {
            if (targetSSID != null && targetSSID.equals(result.SSID)) {
                Log.d(TAG, "onScanListChange 扫描到测试要连接的wifi热点，connect...");
                connect(result.getResult(), targetPwd);
                break;
            }
        }
        if (mCallback != null) {
            mCallback.onScanListChanged(list);
        }
    }

    public void setEnable(boolean enable) {
        Log.d(TAG, "setWifiEnable isWifiEnabled=" + mWifiManager.isWifiEnabled());
        if (enable) {
            if (!mWifiManager.isWifiEnabled()) {
                mWifiManager.setWifiEnabled(true);
            }
        } else {
            if (mWifiManager.isWifiEnabled()) {
                mWifiManager.setWifiEnabled(false);
            }
        }
    }

    private void startScan() {
        Log.d(TAG, "startScan mStartScan=" + mStartScan);
        synchronized (mLock) {
            if (!mStartScan) {
                mStartScan = mWifiManager.startScan();
                Log.d(TAG, "startScan result=" + mStartScan);
            }
        }
    }

    private boolean isWifiConnected() {
        return mWifiManager.getConnectionInfo() != null;
    }

    public void release() {
        if (mContext != null) {
            mContext.unregisterReceiver(mReceiver);
        }
        this.mCallback = null;
        mContext = null;
    }

    private List<MResult> filterResultList(List<ScanResult> list) {
        if (list == null) {
            return null;
        }
        List<MResult> temp = new ArrayList<>();
        for (ScanResult res : list) {
            if (!TextUtils.isEmpty(res.SSID)) {
                temp.add(new MResult(res, res.SSID));
            }
        }
        Set<MResult> set = new HashSet<>(temp);
        return new ArrayList<>(set);
    }

    public static class MResult {
        private final ScanResult result;
        private final String SSID;
        private boolean connected;

        public MResult(ScanResult result, String SSID) {
            this.result = result;
            this.SSID = SSID;
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            if (obj instanceof MResult) {
                return this.SSID.equals(((MResult) obj).SSID);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }

        public String getSSID() {
            return SSID;
        }

        public ScanResult getResult() {
            return result;
        }

        public void setConnected(boolean connected) {
            this.connected = connected;
        }

        public boolean isConnected() {
            return connected;
        }
    }
}
