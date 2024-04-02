package com.exa.companydemo.radio;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.radio.ProgramList;
import android.hardware.radio.ProgramSelector;
import android.hardware.radio.RadioManager;
import android.hardware.radio.RadioTuner;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * @author lsh
 */
public class TunerManager {
    private final static String TAG = "TunerManager";
    private RadioManager mRadioManager;
    private List<RadioManager.ModuleProperties> mModules;
    private RadioManager.FmBandDescriptor mFmDescriptor;
    private RadioManager.AmBandDescriptor mAmDescriptor;
    private RadioManager.FmBandConfig mFmConfig;
    private RadioManager.AmBandConfig mAmConfig;
    private RadioTuner mTuner;
    private int mCurrentRadioBand = RadioManager.BAND_FM;
    private Context mContext;
    private HandlerThread mScanHandlerThread;
    private Handler mScanHandler;
    private TunerListener mCallback;
    private final Object mTuneLock = new Object();
    /**
     * fm频率 87.5-108
     */
    private static final int MIN_FM_FREQUENCY = 87500;
    private static final int MAX_FM_FREQUENCY = 108000;
    /**
     * am频率 531-1629
     */
    private static final int MIN_AM_FREQUENCY = 531;
    private static final int MAX_AM_FREQUENCY = 1629;
    /**
     * 搜索时停台时长超过1秒——搜索成功
     */
    private static final int FREQUENCY_STOPPING_TIME = 3000;
    /**
     * 当前频道
     */
    private int mChannel = -1;
    private RadioManager.ProgramInfo mProgramInfo;
    private int mStartScanFrequency = -1;
    private int mLastFrequency = -1;

    public interface TunerListener {

        /**
         * 报错
         *
         * @param msg 信息
         */
        default void onError(String msg) {
        }

        /**
         * 配置变化
         *
         * @param config 配置信息
         */
        default void onConfigurationChanged(RadioManager.BandConfig config) {
        }

        /**
         * 电台信息变化
         *
         * @param info 电台信息
         */
        default void onProgramInfoChanged(RadioManager.ProgramInfo info) {
        }

        /**
         * 搜索列表变化
         *
         * @param infoList 电台列表
         */
        default void onSearchListChanged(List<RadioManager.ProgramInfo> infoList) {
        }

        /**
         * 静音状态变化
         *
         * @param mute 是否静音
         */
        default void onMuteChanged(boolean mute) {
        }

        /**
         * 搜索完成
         *
         * @param success   是否成功
         * @param frequency 频率
         */
        default void onScanComplete(boolean success, int frequency) {
        }
    }

    public TunerManager(Context context, TunerListener callback) {
        this.mContext = context;
        this.mCallback = callback;
        initTuner();
    }

    private RadioManager.BandConfig getRadioConfig(int selectedRadioBand) {
        switch (selectedRadioBand) {
            case RadioManager.BAND_AM:
                // no break
            case RadioManager.BAND_AM_HD:
                return mAmConfig;
            case RadioManager.BAND_FM:
                // no break
            case RadioManager.BAND_FM_HD:
                return mFmConfig;
            default:
                return null;
        }
    }

    public int switchBand(int band) {
        Log.i(TAG, "switchBand: " + band);
        return openRadioBandInternal(band);
    }

    public boolean setMute(boolean mute) {
        Log.d(TAG, "setMute:" + mute);
        if (!isRadioTunerValid()) {
            return false;
        }
        mTuner.setMute(mute);
        onMuteChanged(mTuner.getMute());
        return mTuner.getMute() == mute;
    }

    public boolean isMute() {
        if (!isRadioTunerValid()) {
            return false;
        }
        return mTuner.getMute();
    }

    public boolean play() {
        Log.i(TAG, "play");
        if (isRadioTunerValid()) {
            if (mTuner.getMute()) {
                int res = mTuner.setMute(false);
                onMuteChanged(res == RadioManager.STATUS_OK);
            }
            return !mTuner.getMute();
        } else {
            return false;
        }
    }


    public void stopScan() {
        if (isRadioTunerValid()) {
            synchronized (mTuneLock) {
                mScanHandler.removeCallbacks(mFrequencyCheckRunnable);
                int res = mTuner.cancel();
                Log.d(TAG, "stopScan: " + (res == RadioManager.STATUS_OK));
            }
        }
    }

    private int openRadioBandInternal(int radioBand) {
        mCurrentRadioBand = radioBand;
        RadioManager.BandConfig config = getRadioConfig(radioBand);
        Log.d(TAG, "openRadioBandInternal radioBand:" + radioBand);
        if (config == null) {
            Log.e(TAG, "Cannot create config for radio band: " + radioBand);
            return RadioManager.STATUS_ERROR;
        }
        if (mTuner != null) {
            mTuner.setConfiguration(config);
        } else {
            mTuner = mRadioManager.openTuner(mModules.get(0).getId(), config,
                    true, mTunerCallback, mScanHandler);
        }
        return RadioManager.STATUS_OK;
    }

    private boolean isRadioTunerValid() {
        if (null == mRadioManager) {
            Log.w(TAG, "isRadioTunerValid mRadioManager == null");
            return false;
        }
        if (null == mTuner) {
            int radioStatus = openRadioBandInternal(mCurrentRadioBand);
            boolean openTunerOK = radioStatus != RadioManager.STATUS_ERROR;
            Log.w(TAG, "isRadioTunerValid: mTuner is null, openTunerOK failed");
            return openTunerOK;
        }
        return true;
    }

    public boolean isFmStation(int frequency) {
        return frequency >= MIN_FM_FREQUENCY && frequency <= MAX_FM_FREQUENCY;
    }

    public boolean isAmStation(int frequency) {
        return frequency >= MIN_AM_FREQUENCY && frequency <= MAX_AM_FREQUENCY;
    }

    /**
     * 获取已发现电台的动态列表
     */
    public boolean getFondedListAsync() {
        Log.d(TAG, "search");
        if (!isRadioTunerValid()) {
            return false;
        }
        // null获取完整列表
        ProgramList list = mTuner.getDynamicProgramList(null);
        if (list != null) {
            Log.d(TAG, "search start list.isEmpty? " + list.toList().isEmpty());
            list.registerListCallback(new ProgramList.ListCallback() {
                @Override
                public void onItemChanged(ProgramSelector.Identifier id) {
                    Log.d(TAG, "getDynamicProgramList onItemChanged id:" + id.toString());
                }
            });
            list.addOnCompleteListener(() -> {
                Log.d(TAG, "getDynamicProgramList onComplete");
                onSearchListChanged(list.toList());
            });
        }
        return true;
    }

    public int pause() {
        if (!isRadioTunerValid()) {
            return -1;
        }
        if (!mTuner.getMute()) {
            mTuner.setMute(true);
            if (mTuner.getMute()) {
                onMuteChanged(true);
            }
        }
        return 0;
    }

    /**
     * See {@link RadioTuner#scan}.
     */
    public boolean scan(boolean forward) {
        if (!isRadioTunerValid()) {
            return false;
        }
        synchronized (mTuneLock) {
            mScanHandler.removeCallbacks(mFrequencyCheckRunnable);
            mTuner.cancel();
            mStartScanFrequency = mChannel;
            mLastFrequency = mChannel;
            int res = mTuner.scan(
                    forward ? RadioTuner.DIRECTION_UP : RadioTuner.DIRECTION_DOWN, false);
            boolean started = res == RadioManager.STATUS_OK;
            if (started) {
                startFrequencyStoppingCheck();
            }
            Log.w(TAG, "start scan result: " + (started ? "OK" : res));
            return started;
        }
    }

    /**
     * 逐步设置频率
     */
    public boolean step(boolean forward, boolean skipSubChannel) {
        if (!isRadioTunerValid()) {
            return false;
        }
        int res = mTuner.step(forward ? RadioTuner.DIRECTION_UP : RadioTuner.DIRECTION_DOWN, skipSubChannel);
        boolean started = res == RadioManager.STATUS_OK;
        Log.w(TAG, "start step result: " + (started ? "OK" : res));
        return started;
    }

    public boolean setFmFrequency(int frequency) {
        Log.d(TAG, "setFmFrequency frequency:" + frequency);
        synchronized (mTuneLock) {
            if (isRadioTunerValid()) {
                try {
                    int result = mTuner.tune(frequency, 0);
                    Log.d(TAG, "setFmFrequency frequency:" + frequency + ",result:" + result);
                    return result == RadioManager.STATUS_OK;
                } catch (Exception e) {
                    Log.e(TAG, "setFmFrequency frequency:" + frequency, e);
                }
            }
            return false;
        }
    }

    public boolean setAmFrequency(int frequency) {
        Log.d(TAG, "setAmFrequency frequency:" + frequency);
        synchronized (mTuneLock) {
            if (isRadioTunerValid()) {
                try {
                    int result = mTuner.tune(frequency, 0);
                    Log.d(TAG, "setAmFrequency frequency:" + frequency + ",result:" + result);
                    return result == RadioManager.STATUS_OK;
                } catch (Exception e) {
                    Log.e(TAG, "setAmFrequency frequency:" + frequency, e);
                }
            }
            return false;
        }
    }

    public int setRadioFreq(int frequency) {
        if (!isRadioTunerValid()) {
            return 0;
        }
        synchronized (mTuneLock) {
            try {
                int result = mTuner.tune(frequency, 0);
                Log.d(TAG, "setRadioFreq frequency:" + frequency + ",result:" + result);
            } catch (Exception e) {
                Log.e(TAG, "setRadioFreq frequency:" + frequency, e);
            }
        }
        return 0;
    }

    public int getCurrentChannel() {
        return mChannel;
    }

    @SuppressLint("WrongConstant")
    private void initTuner() {
        Log.d(TAG, "initTuner");
        mScanHandlerThread = new HandlerThread("TunerScanThread");
        mScanHandlerThread.start();
        mScanHandler = new Handler(mScanHandlerThread.getLooper());
        mRadioManager = (RadioManager) mContext.getSystemService("broadcastradio");
        if (mRadioManager != null) {
            mModules = new ArrayList<>();
            int status = mRadioManager.listModules(mModules);
            if (status != RadioManager.STATUS_OK) {
                Log.e(TAG, "initTuner listModules fail! status:" + status);
                return;
            }
            if (mModules.size() == 0) {
                Log.e(TAG, "initTuner mModules is 0 , no work");
                return;
            }
            for (RadioManager.ModuleProperties m : mModules) {
                Log.d(TAG, "initTuner module:" + m.toString());
            }
            for (RadioManager.BandDescriptor band : mModules.get(0).getBands()) {
                int region = band.getRegion();
                Log.d(TAG, "initTuner region:" + region);
                if (band.isAmBand()) {
                    mAmDescriptor = (RadioManager.AmBandDescriptor) band;
                }
                if (band.isFmBand()) {
                    mFmDescriptor = (RadioManager.FmBandDescriptor) band;
                }
                if (mFmDescriptor != null && mAmDescriptor != null) {
                    break;
                }
            }
            if (mFmDescriptor == null || mAmDescriptor == null) {
                Log.e(TAG, "initTuner AM or FM bands could not load");
            } else {
                mFmConfig = new RadioManager.FmBandConfig.Builder(mFmDescriptor)
                        .setStereo(true)
                        .build();
                Log.e(TAG, "initTuner mFmConfig=" + mFmConfig);
                mAmConfig = new RadioManager.AmBandConfig.Builder(mAmDescriptor)
                        .setStereo(true)
                        .build();
                Log.e(TAG, "initTuner mAmConfig=" + mFmConfig);
            }
        } else {
            Log.e(TAG, "No service published for: broadcastradio");
        }
    }

    private final RadioTuner.Callback mTunerCallback = new RadioTuner.Callback() {
        @Override
        public void onError(int status) {
            super.onError(status);
            Log.d(TAG, "onError status:" + status);
        }

        @Override
        public void onTuneFailed(int result, ProgramSelector selector) {
            super.onTuneFailed(result, selector);
            Log.d(TAG, "onTuneFailed:" + result + ",selector:" + selector.toString());
        }

        @Override
        public void onConfigurationChanged(RadioManager.BandConfig config) {
            super.onConfigurationChanged(config);
            Log.d(TAG, "onConfigurationChanged:" + config.toString());
            mCurrentRadioBand = config.getType();
            if (mCurrentRadioBand == RadioManager.BAND_FM) {
                setRadioFreq(MIN_FM_FREQUENCY);
            } else if (mCurrentRadioBand == RadioManager.BAND_AM) {
                setRadioFreq(MIN_AM_FREQUENCY);
            }
            onConfigChanged(config);
        }

        @Override
        public void onProgramInfoChanged(RadioManager.ProgramInfo info) {
            super.onProgramInfoChanged(info);
            Log.d(TAG, "onProgramInfoChanged channel=" + info.getChannel()
                    + ",sunChannel=" + info.getSubChannel());
            mChannel = info.getChannel();
            mProgramInfo = info;
            onProgramChanged(info);
            if (mTuner != null) {
                if (mTuner.getMute()) {
                    play();
                }
            }
        }

        @Override
        public void onTrafficAnnouncement(boolean active) {
            super.onTrafficAnnouncement(active);
            Log.d(TAG, "onTrafficAnnouncement");
        }

        @Override
        public void onEmergencyAnnouncement(boolean active) {
            super.onEmergencyAnnouncement(active);
            Log.d(TAG, "onEmergencyAnnouncement");
        }

        @Override
        public void onAntennaState(boolean connected) {
            super.onAntennaState(connected);
            Log.d(TAG, "onAntennaState");
        }

        @Override
        public void onControlChanged(boolean control) {
            super.onControlChanged(control);
            Log.d(TAG, "onControlChanged");
        }

        @Override
        public void onBackgroundScanAvailabilityChange(boolean isAvailable) {
            super.onBackgroundScanAvailabilityChange(isAvailable);
            Log.d(TAG, "onBackgroundScanAvailabilityChange");
        }

        @Override
        public void onBackgroundScanComplete() {
            super.onBackgroundScanComplete();
            Log.d(TAG, "onBackgroundScanComplete");
        }

        @Override
        public void onProgramListChanged() {
            super.onProgramListChanged();
            Log.d(TAG, "onProgramListChanged");
            List<RadioManager.ProgramInfo> programList = mTuner.getProgramList(null);
            if (programList == null) {
                Log.d(TAG, "programList is null");
                return;
            }
            Log.d(TAG, "onProgramListChanged programList.size=" + programList.size());
            onSearchListChanged(programList);
        }

        @Override
        public void onParametersUpdated(Map<String, String> parameters) {
            super.onParametersUpdated(parameters);
            Log.d(TAG, "onParametersUpdated");
        }
    };

    private final Runnable mFrequencyCheckRunnable = () -> {
        if (mLastFrequency == mChannel) {
            onScanComplete(mChannel);
        } else {
            mLastFrequency = mChannel;
            startFrequencyStoppingCheck();
        }
    };

    /**
     * 开始频率停台检测
     */
    private void startFrequencyStoppingCheck() {
        mScanHandler.postDelayed(mFrequencyCheckRunnable, FREQUENCY_STOPPING_TIME);
    }

    private void sortProgramList(List<RadioManager.ProgramInfo> list) {
        list.sort(Comparator.comparingInt(RadioManager.ProgramInfo::getChannel));
    }

    private void onConfigChanged(RadioManager.BandConfig config) {
        mCallback.onConfigurationChanged(config);
    }

    private void onProgramChanged(RadioManager.ProgramInfo info) {
        mCallback.onProgramInfoChanged(info);
    }

    private void onSearchListChanged(List<RadioManager.ProgramInfo> list) {
        mCallback.onSearchListChanged(list);
    }

    /**
     * 搜索完成
     * 1.如果搜索失败，会回到搜索开始的频道
     * @param channel 频道
     */
    private void onScanComplete(int channel) {
        Log.i(TAG, "onScanComplete: " + channel + ", " + mStartScanFrequency);
        mCallback.onScanComplete(mStartScanFrequency != channel, channel);
    }

    private void onMuteChanged(boolean mute) {
        mCallback.onMuteChanged(mute);
    }

    public void release() {
        stopScan();
        if (mTuner != null) {
            mTuner.close();
        }
        mScanHandler.removeCallbacksAndMessages(null);
    }
}
