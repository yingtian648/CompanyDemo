package gxa.car.extlocationservice;

import android.os.Parcel;
import android.os.Parcelable;

public class GnssHwInfo implements Parcelable {
    private String mVersion;
    private String mModel;
    private String mManufacturer;

    public GnssHwInfo(String version, String model, String manufacturer) {
        mVersion = version;
        mModel = model;
        mManufacturer = manufacturer;
    }

    public GnssHwInfo() {
    }

    protected GnssHwInfo(Parcel in) {
        mVersion = in.readString();
        mModel = in.readString();
        mManufacturer = in.readString();
    }

    public static final Creator<GnssHwInfo> CREATOR = new Creator<GnssHwInfo>() {
        @Override
        public GnssHwInfo createFromParcel(Parcel in) {
            return new GnssHwInfo(in);
        }

        @Override
        public GnssHwInfo[] newArray(int size) {
            return new GnssHwInfo[size];
        }
    };

    public String getVersion() {
        return mVersion;
    }

    public String getModel() {
        return mModel;
    }

    public String getManufacturer() {
        return mManufacturer;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mVersion);
        dest.writeString(mModel);
        dest.writeString(mManufacturer);
    }
}
