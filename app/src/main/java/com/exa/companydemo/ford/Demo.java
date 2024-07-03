package com.exa.companydemo.ford;

import android.content.Context;

/**
 * @author lsh
 * @date 2024/6/24 18:22
 * @description
 */
public class Demo {

    private Context mContext;
    private FNVGnssManager mFnvGnssManager;

    private final ServiceConnectListener mListener = new ServiceConnectListener() {
        @Override
        public void onServiceConnected(FNVGnssManager manager) {
            mFnvGnssManager = manager;
            registerListener();
        }

        @Override
        public void onServiceDisconnected() {
            mFnvGnssManager = null;
        }
    };

    private final FNVGnssManager.NmeaListener mNmeaListener = new FNVGnssManager.NmeaListener() {
        @Override
        public void onNmeaGPGGAChanged(NmeaGPGGA ggaData) {
            //TODO
        }

        @Override
        public void onNmeaGPRMCChanged(NmeaGPRMC rmcData) {
            //TODO
        }
    };

    public Demo(Context context) {
        this.mContext = context;
        startListenNmeaDataChange(mContext);
    }

    public void startListenNmeaDataChange(Context context) {
        FNVGnssManager.init(context, mListener);
    }

    private void registerListener() {
        mFnvGnssManager.registerNmeaListener(mNmeaListener);
    }

    private void unRegisterListener() {
        mFnvGnssManager.unregisterNmeaListener(mNmeaListener);
    }
}
