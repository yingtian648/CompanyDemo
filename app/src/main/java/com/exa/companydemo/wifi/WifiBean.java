package com.exa.companydemo.wifi;

import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;

/**
 * @author lsh
 * Date 2024/3/11 17:40
 */
public class WifiBean {
    public static final int STATE_CONNECTED = 1;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_DISCONNECTED = 3;

    public static final String WEP = "WEP";
    public static final String PSK = "PSK";
    public static final String EAP = "EAP";
    private String BSSID;
    private String SSID;
    private String capabilities;
    private int channelWidth;
    private long createTime;
    private boolean isSave;
    private int level;
    private Integer networkId;
    private String password;
    private long timestamp;
    private long updateTime;
    private int rssi;
    private int wifiConnectStatus;

    public final Integer getNetworkId() {
        return this.networkId;
    }

    public final void setNetworkId(Integer num) {
        this.networkId = num;
    }

    public final String getSSID() {
        return this.SSID;
    }

    public final void setSSID(String str) {
        this.SSID = str;
    }

    public final String getPassword() {
        return this.password;
    }

    public final void setPassword(String str) {
        this.password = str;
    }

    public final String getBSSID() {
        return this.BSSID;
    }

    public final void setBSSID(String str) {
        this.BSSID = str;
    }

    public final long getTimestamp() {
        return this.timestamp;
    }

    public final void setTimestamp(long j) {
        this.timestamp = j;
    }

    public final long getUpdateTime() {
        return this.updateTime;
    }

    public final void setUpdateTime(long j) {
        this.updateTime = j;
    }

    public final long getCreateTime() {
        return this.createTime;
    }

    public final void setCreateTime(long j) {
        this.createTime = j;
    }

    public final int getLevel() {
        return this.level;
    }

    public final void setLevel(int i) {
        this.level = i;
    }

    public final int getChannelWidth() {
        return this.channelWidth;
    }

    public final void setChannelWidth(int i) {
        this.channelWidth = i;
    }

    public final String getCapabilities() {
        return this.capabilities;
    }

    public final void setCapabilities(String str) {
        this.capabilities = str;
    }

    public final boolean isSave() {
        return this.isSave;
    }

    public final void setSave(boolean z) {
        this.isSave = z;
    }

    public final int getWifiConnectStatus() {
        return this.wifiConnectStatus;
    }

    public final void setWifiConnectStatus(int wifiPointStatus) {
        this.wifiConnectStatus = wifiPointStatus;
    }

    public final void set(ScanResult scanResult) {
        if (scanResult == null) {
            return;
        }
        this.SSID = formatSSID(scanResult.SSID);
        this.BSSID = scanResult.BSSID;
        this.capabilities = scanResult.capabilities;
        this.channelWidth = scanResult.channelWidth;
        this.timestamp = scanResult.timestamp;
        this.level = scanResult.level;
        this.updateTime = System.currentTimeMillis();
        if (this.createTime == 0) {
            System.currentTimeMillis();
        }
    }

    public final void set(WifiInfo wifiInfo) {
        if (wifiInfo == null) {
            return;
        }
        this.SSID = formatSSID(wifiInfo.getSSID());
        this.BSSID = wifiInfo.getBSSID();
        this.networkId = wifiInfo.getNetworkId();
        this.isSave = true;
        this.level = wifiInfo.getRssi();
        this.updateTime = System.currentTimeMillis();
        if (wifiInfo.getSupplicantState() == SupplicantState.COMPLETED) {
            this.wifiConnectStatus = STATE_CONNECTED;
        }
        if (wifiInfo.getSupplicantState() == SupplicantState.AUTHENTICATING) {
            this.wifiConnectStatus = STATE_CONNECTING;
        }
        if (wifiInfo.getSupplicantState() == SupplicantState.DISCONNECTED) {
            this.wifiConnectStatus = STATE_DISCONNECTED;
        }
        if (this.createTime == 0) {
            System.currentTimeMillis();
        }
    }

    private String formatSSID(String ssid) {
        return (!ssid.startsWith("\"")
                || !ssid.endsWith("\"")) ? ssid : ssid.substring(1, ssid.length() - 1);
    }

    public final boolean getSecurity() {
        String str = this.capabilities;
        if (str != null && str.contains(WEP)) {
            return true;
        }
        String str2 = this.capabilities;
        if (str2 != null && str2.contains(PSK)) {
            return true;
        }
        String str3 = this.capabilities;
        return str3 != null && str3.contains(EAP);
    }

    @Override
    public String toString() {
        return "WifiPoint(SSID=" + this.SSID
                + ", BSSID=" + this.BSSID
                + ", networkId=" + this.networkId
                + ", isSave=" + this.isSave
                + ", wifiConnectStatus=" + this.wifiConnectStatus
                + ", capabilities = " + ((Object) this.capabilities)
                + ')';
    }
}
