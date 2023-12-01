package com.exa.companydemo.utils;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.os.Build;

import com.exa.baselib.utils.L;

import static android.content.Context.AUDIO_SERVICE;

/**
 * @author Liushihua
 * @date 2022-1-20 13:40
 * @desc Android O 以上有效
 */
public class AudioFocusManager {

    private AudioManager mAudioManager;
    private AudioAttributes audioAttribute;
    private AudioFocusRequest audioFocusRequest;
    public static AudioFocusManager getInstance(){
        return ClassHolder.instance;
    }

    private static class ClassHolder{
        private static AudioFocusManager instance  = new AudioFocusManager();
    }

    private int currAudioFocusState = AudioManager.AUDIOFOCUS_LOSS;

    public void init(Context context) {
        mAudioManager = (AudioManager) context.getSystemService(AUDIO_SERVICE);
        audioAttribute = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                    .setAudioAttributes(audioAttribute)
                    .setAcceptsDelayedFocusGain(false)
                    .setOnAudioFocusChangeListener(mOnAudioFocusChangeListener)
                    .build();
        }
    }

    private final AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener =
            focusChange -> {
                L.d("AudioFocusManager  onAudioFocusChange:" + focusChange);
                switch (focusChange) {
                    case AudioManager.AUDIOFOCUS_LOSS://-1
                        //对应AUDIOFOCUS_GAIN
                        //表示音频焦点请求者需要长期占有焦点，这里一般需要stop播放和释放
                        // 如果当前正在播放，需要记录是焦点丢失导致的暂停
                        currAudioFocusState = AudioManager.AUDIOFOCUS_LOSS;
                        stopPlayer();
                        break;
                    case AudioManager.AUDIOFOCUS_GAIN://1 获得焦点 这里可以进行恢复播放
                        resumePlayer();
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT://-2
                        //对应AUDIOFOCUS_GAIN_TRANSIENT
                        //表示音频焦点请求者需要短暂占有焦点，这里一般需要pause播放
                        // 如果当前正在播放，需要记录是焦点丢失导致的暂停
                        currAudioFocusState = AudioManager.AUDIOFOCUS_LOSS;
                        pausePlayer();
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK://-3
                        //对应AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK
                        //表示音频焦点请求者需要占有焦点，但是我也可以继续播放，只是需要降低音量或音量置为0
                        // 如果当前正在播放，需要记录是焦点丢失导致的暂停

                        break;
                    default:
                        break;
                }
            };

    /**
     * 继续播放 在 pause之后
     */
    private void resumePlayer() {

    }

    /**
     * 停止播放
     */
    private void stopPlayer() {

    }

    /**
     * 暂停播放
     */
    private void pausePlayer() {

    }

    /**
     * 申请焦点
     *
     * @return
     */
    public boolean requestAudioFocus() {
        L.dd();
        if (currAudioFocusState == AudioManager.AUDIOFOCUS_GAIN) {
            L.dd("已有焦点");
            return true;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int result = mAudioManager.requestAudioFocus(audioFocusRequest);
            if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                currAudioFocusState = AudioManager.AUDIOFOCUS_GAIN;
            }
        }
        L.dd(currAudioFocusState == AudioManager.AUDIOFOCUS_GAIN);
        return currAudioFocusState == AudioManager.AUDIOFOCUS_GAIN;
    }

    /**
     * 释放焦点
     */
    public void clearAudioFocus() {
        L.dd();
        int result = mAudioManager.abandonAudioFocus(mOnAudioFocusChangeListener);
        currAudioFocusState = AudioManager.AUDIOFOCUS_LOSS;
        L.dd("result:" + result);
    }
}
