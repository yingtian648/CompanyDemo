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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * @author lsh
 */
public class TunerManager {
    private final static String TAG = "TunerManager";

    public interface Callback {
        default void onConfigurationChanged(RadioManager.BandConfig config) {
        }

        default void onProgramInfoChanged(RadioManager.ProgramInfo info) {
        }

        default void onSearchListChanged(List<RadioStation> infoList) {
        }

        default void onMuteChanged(boolean mute) {
        }
    }

    private Context mContext;
    private HandlerThread mScanHandlerThread;
    private Handler mScanHandler;
    private Callback mCallback;
    private final Object mTuneLock = new Object();
    /**
     * fm频率 87.5-108
     */
    private final int MIN_FM_FREQUENCY = 87500;
    private final int MAX_FM_FREQUENCY = 108000;
    /**
     * am频率 531-1629
     */
    private final int MIN_AM_FREQUENCY = 531;
    private final int MAX_AM_FREQUENCY = 1629;

    public TunerManager(Context context, Callback callback) {
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
        return openRadioBandInternal(band);
    }

    public void setMute(boolean mute) {
        if (!isRadioTunerValid()) {
            return;
        }
        Log.d(TAG, "setMute:" + mute);
        mTuner.setMute(mute);
        onMuteChanged(mute);
    }

    public boolean isMute() {
        if (!isRadioTunerValid()) {
            return false;
        }
        return mTuner.getMute();
    }

    public int play() {
        if (!isRadioTunerValid()) {
            return -1;
        }
        if (mTuner.getMute()) {
            mTuner.setMute(false);
            if (!mTuner.getMute()) {
                onMuteChanged(false);
            }
        }
        RadioManager.BandConfig[] config = new RadioManager.BandConfig[1];
        mTuner.getConfiguration(config);
        Log.d(TAG, "play " + config[0].toString());
        return 0;
    }


    public void stopScan() {
        if (isRadioTunerValid()) {
            synchronized (mTuneLock) {
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
                    true, mTunnerCallback, mScanHandler);
        }
        return RadioManager.STATUS_OK;
    }

    private boolean isRadioTunerValid() {
        if (null == mRadioManager) {
            Log.d(TAG, "isRadioTunerValid mRadioManager == null");
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

    public int search() {
        Log.d(TAG, "search");
        if (!isRadioTunerValid()) {
            return -1;
        }
        ProgramList list = mTuner.getDynamicProgramList(null);
        if (mCurrentRadioBand == RADIO_BAND_FM) {
            mFmScanList.clear();
        } else if (mCurrentRadioBand == RADIO_BAND_AM) {
            mAmScanList.clear();
        }
        if (list != null) {
            Log.d(TAG, "search start list.isEmpty " + list.toList().isEmpty());
            list.registerListCallback(new ProgramList.ListCallback() {
                @Override
                public void onItemChanged(ProgramSelector.Identifier id) {
                    Log.d(TAG, "onItemChanged id:" + id.toString());
                }
            });
            list.addOnCompleteListener(() -> {
                List<RadioManager.ProgramInfo> pList = list.toList();
                Log.d(TAG, "search onComplete " + pList);
                for (RadioManager.ProgramInfo info : pList) {
                    if (info.getSelector().getPrimaryId().getType() == RADIO_BAND_FM) {
                        mFmScanList.add(new RadioStation(info.getChannel(),
                                info.getSelector().getPrimaryId().getType()));
                    } else if (info.getSelector().getPrimaryId().getType() == RADIO_BAND_AM) {
                        mAmScanList.add(new RadioStation(info.getChannel(),
                                info.getSelector().getPrimaryId().getType()));
                    }
                }
                onSearchListChanged(mCurrentRadioBand == RADIO_BAND_FM ? mFmScanList : mFmScanList);
            });
        }
        return 0;
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
    public void seek(boolean forward) {
        synchronized (mTuneLock) {
            mTuner.cancel();
            int res = mTuner.scan(
                    forward ? RadioTuner.DIRECTION_UP : RadioTuner.DIRECTION_DOWN, false);
            if (res != RadioManager.STATUS_OK) {
                Log.w(TAG, "Seek failed with result of " + res);
            }
        }
    }

    /**
     * 逐步设置频率
     */
    public int step(boolean forward, boolean skipSubChannel) {
        if (!isRadioTunerValid()) {
            return -1;
        }
        try {
            mTuner.step(forward ? RadioTuner.DIRECTION_UP : RadioTuner.DIRECTION_DOWN, skipSubChannel);
        } catch (Exception e) {
            Log.d(TAG, "seekDownByStep catch exection do nothing.");
        }
        return 0;
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

    public RadioStation getCurrentStation() {
        if (mChannel != -1 && mTuner != null) {
            RadioManager.ProgramInfo[] info = new RadioManager.ProgramInfo[1];
            int status = mTuner.getProgramInformation(info);
            if (status == RadioManager.STATUS_OK && info[0] != null) {
                mChannel = info[0].getChannel();
                return new RadioStation(mChannel, mCurrentRadioBand);
            }
        }
        return new RadioStation(mChannel, mCurrentRadioBand);
    }

    /**
     * for band switch AM bind
     */
    public static final int RADIO_BAND_AM = RadioManager.BAND_AM;
    /**
     * for band switch FM bind
     */
    public static final int RADIO_BAND_FM = RadioManager.BAND_FM;

    public List<RadioStation> getScanList(int type) {
        if (type == RADIO_BAND_FM) {
            return mFmScanList;
        }
        if (type == RADIO_BAND_AM) {
            return mAmScanList;
        }
        return null;
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

    private void programSort(List<RadioStation> scanList) {
        Collections.sort(scanList, mSort);
    }

    static class ProgramSort implements Comparator {

        @Override
        public int compare(Object o1, Object o2) {
            RadioStation s1 = (RadioStation) o1;
            RadioStation s2 = (RadioStation) o2;
            return s1.getChannel() - (s2.getChannel());
        }
    }

    private ProgramSort mSort = new ProgramSort();
    private final HandlerThread mCallbackThread = new HandlerThread("tuner_manager");
    private RadioManager mRadioManager;
    private List<RadioManager.ModuleProperties> mModules;
    private List<RadioManager.BandDescriptor> mAmFmRagionConfig;
    private RadioManager.FmBandDescriptor mFmDescriptor;
    private RadioManager.AmBandDescriptor mAmDescriptor;
    private RadioManager.FmBandConfig mFmConfig;
    private RadioManager.AmBandConfig mAmConfig;
    private RadioTuner mTuner;
    private List<RadioStation> mFmScanList = new ArrayList<>();
    private List<RadioStation> mAmScanList = new ArrayList<>();
    private int mCurrentRadioBand = RadioManager.BAND_FM;
    private int mChannel = -1;
    private RadioTuner.Callback mTunnerCallback = new RadioTuner.Callback() {
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
            if (programList.size() > 0) {
                int type = 0;
                List<RadioStation> scanList = new ArrayList<>();
                for (int i = 0; i < programList.size(); i++) {
                    ProgramSelector.Identifier primaryId = programList.get(i).getSelector().getPrimaryId();
                    type = programList.get(i).getSelector().getProgramType();
                    scanList.add(new RadioStation((int) primaryId.getValue(), type - 1));
                }

                if (scanList.get(0).getChannel() <= MAX_AM_FREQUENCY) {
                    mAmScanList.clear();
                    mAmScanList.addAll(scanList);
                    programSort(mAmScanList);
                    onSearchListChanged(mAmScanList);
                } else {
                    mFmScanList.clear();
                    mFmScanList.addAll(scanList);
                    programSort(mFmScanList);
                    onSearchListChanged(mFmScanList);
                }
            }

        }

        @Override
        public void onParametersUpdated(Map<String, String> parameters) {
            super.onParametersUpdated(parameters);
            Log.d(TAG, "onParametersUpdated");
        }
    };

    private void onConfigChanged(RadioManager.BandConfig config) {
        mCallback.onConfigurationChanged(config);
    }

    private void onProgramChanged(RadioManager.ProgramInfo info) {
        mCallback.onProgramInfoChanged(info);
    }

    private void onSearchListChanged(List<RadioStation> list) {
        mCallback.onSearchListChanged(list);
    }

    private void onMuteChanged(boolean mute) {
        mCallback.onMuteChanged(mute);
    }

    public void release() {
        stopScan();
        if (mTuner != null) {
            mTuner.close();
        }
    }
}
