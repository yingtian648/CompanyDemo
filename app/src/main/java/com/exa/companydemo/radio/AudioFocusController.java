package com.exa.companydemo.radio;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.util.Log;

import gxa.car.audio.CarAudioAttributes;
import gxa.car.audio.CarAudioFocusRequest;
import gxa.car.audio.CarAudioManager;

/**
 * @author lsh
 */
public class AudioFocusController {

    private final static String TAG = "AudioFocusController";
    private final AudioManager mAudioManager;
    private final AudioFocusRequest mAudioFocusRequest;
    private CarAudioFocusRequest mCarGainFocusReq;
    private TunerManager mTunerController;
    private Context mContext;

    private boolean mHasFocus = false;

    public void setTunerController(TunerManager mTunerController) {
        this.mTunerController = mTunerController;
    }

    private void onAudioFocusChanged(int focusChanged) {
        Log.d(TAG, "onAudioFocusChanged focus:" + focusChanged);
        switch (focusChanged) {
            case AudioManager.AUDIOFOCUS_GAIN:
                mTunerController.setMute(false);
                mHasFocus = true;
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                if (mTunerController != null) {
                    mTunerController.setMute(true);
                }
                mHasFocus = false;
                break;
            case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT:
                // 获取到临时焦点，使用后需要取消焦点
            case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK:
                if (mTunerController != null) {
                    mTunerController.setMute(true);
                }
                break;
            default:
                break;
        }
    }

    public AudioFocusController(Context context, TunerManager controller) {
        this.mContext = context;
        mTunerController = controller;
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        AudioAttributes mAudioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();

        mAudioFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setOnAudioFocusChangeListener(this::onAudioFocusChanged)
                .setAudioAttributes(mAudioAttributes)
                .build();

    }

    public boolean requestRadioFocus() {
        if (mCarGainFocusReq == null) {
            CarAudioAttributes mCarAudioAttributes = new CarAudioAttributes.Builder()
                    .setUsage(CarAudioAttributes.AUDIO_USAGE_RADIO)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();
            mCarGainFocusReq = new CarAudioFocusRequest.Builder(CarAudioManager.AUDIOFOCUS_GAIN)
                    .setOnAudioFocusChangeListener(this::onAudioFocusChanged)
                    .setAudioAttributes(mCarAudioAttributes)
                    .build();
        }

        int ret = CarAudioManager.getInstance(mContext).requestAudioFocus(mCarGainFocusReq);
        Log.d(TAG, "requestRadioFocus result=" + ret);
        return ret == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    }

    public boolean abandonRadioFocus() {
        int ret = CarAudioManager.getInstance(mContext).abandonAudioFocus(mCarGainFocusReq);
        Log.d(TAG, "abandonRadioFocus result=" + ret);
        return ret == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    }


    private boolean requestFocus() {
        int ret = mAudioManager.requestAudioFocus(mAudioFocusRequest);
        Log.d(TAG, "requestFocus " + (ret == AudioManager.AUDIOFOCUS_REQUEST_GRANTED));
        return ret == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    }

    private boolean abandonFocus() {
        int ret = mAudioManager.abandonAudioFocusRequest(mAudioFocusRequest);
        Log.d(TAG, "abandonFocus " + (ret == AudioManager.AUDIOFOCUS_REQUEST_GRANTED));
        return ret == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    }


    public void requestMute(boolean isMute) {
        Log.d(TAG, "requestMute isMute:" + isMute);
        if (isMute) {
            if (abandonFocus()) {
                mTunerController.setMute(true);
            }
        } else {
            if (requestFocus()) {
                mTunerController.setMute(false);
            }
        }
    }

}
