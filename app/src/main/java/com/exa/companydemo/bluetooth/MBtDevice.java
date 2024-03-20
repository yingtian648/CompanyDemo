package com.exa.companydemo.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

/**
 * @author  lsh
 * Date 2024/3/12 14:00
 */
public class MBtDevice implements Parcelable {
    private BluetoothDevice device;
    private boolean connect = false;
    private int rssi = 1;

    public MBtDevice() {
    }

    public MBtDevice(BluetoothDevice device, int rssi) {
        this.device = device;
        this.rssi = rssi;
    }

    public MBtDevice(BluetoothDevice device) {
        this.device = device;
    }

    public MBtDevice(BluetoothDevice device, boolean connect) {
        this.device = device;
        this.connect = connect;
    }

    protected MBtDevice(Parcel in) {
        device = in.readParcelable(BluetoothDevice.class.getClassLoader());
        rssi = in.readInt();
    }

    public boolean isConnect() {
        return connect;
    }

    public static final Creator<MBtDevice> CREATOR = new Creator<MBtDevice>() {
        @Override
        public MBtDevice createFromParcel(Parcel in) {
            return new MBtDevice(in);
        }

        @Override
        public MBtDevice[] newArray(int size) {
            return new MBtDevice[size];
        }
    };

    public BluetoothDevice getDevice() {
        return device;
    }

    public String getRssi() {
        if (rssi > 0) {
            return "";
        } else {
            return String.valueOf(rssi);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeParcelable(device, flags);
        dest.writeBoolean(connect);
        dest.writeInt(rssi);
    }
}
