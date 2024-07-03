package com.exa.companydemo.ford;

import android.content.Context;
import android.os.IBinder;
import android.util.Log;

/**
 * @author lsh
 * @date 2024/6/25 15:09
 * @description
 */
public class FNVGnssManager {
    private static final String TAG = "FNVGnssMananger";
    private static FNVGnssManager mFnvGnssMananger;
    private Context mContext;
    private static boolean mServiceConnected = false;
    private static ServiceConnectListener mClientListener;
    private NmeaListener mListener;
    private final Object mLock = new Object();

    public interface NmeaListener {
        void onNmeaGPGGAChanged(NmeaGPGGA ggaData);

        void onNmeaGPRMCChanged(NmeaGPRMC rmcData);
    }

    protected FNVGnssManager(Context context) {
        mContext = context;
        bindService();
    }

    private void bindService() {
        // TODO bind service
    }

    /**
     * Carplay App should call this method first
     * @param context use for bind service
     * @param listener listen remote service connect status
     */
    public static synchronized void init(Context context, ServiceConnectListener listener) {
        if (context == null || listener == null) {
            Log.w(TAG, "Param is null");
        } else {
            mClientListener = listener;
            if (mFnvGnssMananger == null) {
                mFnvGnssMananger = new FNVGnssManager(context);
            }
        }
    }

    /**
     * when connected remote service this method will be called
     * @param service remote service binder
     */
    private void onServiceConnected(IBinder service){
        //TODO get remote service binder
        if(mClientListener!=null){
            mClientListener.onServiceConnected(mFnvGnssMananger);
        }
    }

    /**
     * listen nmea data change
     *
     * @param nmeaListener FNVGnssMananger.NmeaListener
     */
    public void registerNmeaListener(NmeaListener nmeaListener) {
        Log.w(TAG, "registerNmeaDataListener");
        mListener = nmeaListener;
        //TODO
    }

    /**
     * remove listener
     *
     * @param nmeaListener FNVGnssMananger.NmeaListener
     *
     */
    public void unregisterNmeaListener(NmeaListener nmeaListener) {
        mListener = null;
        //TODO
    }

    /**
     * NmeaGPGGA callback to client
     */
    private void onNmeaGPGGADataChanged(NmeaGPGGA gpgga){
        if(mListener!=null){
            mListener.onNmeaGPGGAChanged(gpgga);
        }
    }

    /**
     * NmeaGPRMC callback to client
     */
    private void onNmeaGPRMCDataChanged(NmeaGPRMC gprmc){
        if(mListener!=null){
            mListener.onNmeaGPRMCChanged(gprmc);
        }
    }

    /**
     * 回传到TCU
     * 上报偏转数据接口
     *
     * @param pairingKey    校验key
     * @param latitudeSign  0-1 0-南纬 1-北纬
     * @param latitudeInt   0-89 纬度整数
     * @param latitudeFrac  0.000001-0.999999  纬度小数（6位小数）
     * @param LongitudeSign 0-1 0-西经 1-东经
     * @param LongitudeInt  0-179   经度整数
     * @param LongitudeFrac 0.000001-0.999999  经度小数（6位小数）
     */
    public void reportShiftedLocation(
            long pairingKey,
            int latitudeSign,
            int latitudeInt,
            float latitudeFrac,
            int LongitudeSign,
            int LongitudeInt,
            float LongitudeFrac) {
        //TODO
    }

    public synchronized void release() {
        mFnvGnssMananger = null;
        this.mListener = null;
    }
}
