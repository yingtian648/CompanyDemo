package com.exa.companydemo.radio;

import androidx.annotation.NonNull;

/**
 * @author lsh
 */
public class RadioStation {

    private int mChannel;
    private int mBand;

    public RadioStation(int channel, int band) {
        mChannel = channel;
        mBand = band;
    }

    public int getChannel() {
        return mChannel;
    }

    public int getRadioBand() {
        return mBand;
    }

    @NonNull
    @Override
    public String toString() {
        return (mBand == TunerManager.RADIO_BAND_AM ?
                "AM" : "FM") + " " + mChannel;
    }
}
