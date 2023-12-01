package com.exa.companydemo.utils;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.view.Surface;

import java.nio.ByteBuffer;

/**
 * @author  lsh
 * @date  2021-5-13 14:27
 * @描述
 */
public class MediaCodecUtil {
    private final int width = 1280;
    private final int height = 720;
    private byte[] yuv420;
    private byte[] m_info;
    private MediaCodec mediaCodec;

    public void init() {
        MediaFormat format = MediaFormat.createVideoFormat("video/avc", 1280, 720);
        //设置颜色格式
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        //设置比特率(设置码率，通常码率越高，视频越清晰)
        format.setInteger(MediaFormat.KEY_BIT_RATE, 1000 * 1024);
        //设置帧率
        format.setInteger(MediaFormat.KEY_FRAME_RATE, 20);
        //关键帧间隔时间，通常情况下，你设置成多少问题都不大。
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 10);
        // 当画面静止时,重复最后一帧，不影响界面显示
        format.setLong(MediaFormat.KEY_REPEAT_PREVIOUS_FRAME_AFTER, 1000000 / 45);
        format.setInteger(MediaFormat.KEY_BITRATE_MODE, MediaCodecInfo.EncoderCapabilities.BITRATE_MODE_VBR);
        //设置复用模式
        format.setInteger(MediaFormat.KEY_COMPLEXITY, MediaCodecInfo.EncoderCapabilities.BITRATE_MODE_CBR);
        try {
            mediaCodec = MediaCodec.createEncoderByType("video/avc");
            mediaCodec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        } catch (Exception e) {
            e.printStackTrace();
            if (mediaCodec != null) {
                mediaCodec.reset();
                mediaCodec.stop();
                mediaCodec.release();
                mediaCodec = null;
            }
        }
    }

    public void startEncode() {
        if (mediaCodec != null) {
            mediaCodec.start();
        }
    }

    public Surface getSurface() {
        if (mediaCodec != null) {
            return mediaCodec.createInputSurface();
        }
        return null;
    }

    public int offerEncoder(byte[] input, byte[] output) {
        int pos = 0;
        byte[] yuv420 = new byte[width * height * 3 / 2];
        swapYV12toI420(input, m_info, width, height);
        try {
            ByteBuffer[] inputBuffers = mediaCodec.getInputBuffers();
            ByteBuffer[] outputBuffers = mediaCodec.getOutputBuffers();
            int inputBufferIndex = mediaCodec.dequeueInputBuffer(-1);
            if (inputBufferIndex >= 0) {
                ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
                inputBuffer.clear();
                inputBuffer.put(yuv420);
                mediaCodec.queueInputBuffer(inputBufferIndex, 0, yuv420.length, 0, 0);
            }

            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
            int outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 0);

            while (outputBufferIndex >= 0) {
                ByteBuffer outputBuffer = outputBuffers[outputBufferIndex];
                byte[] outData = new byte[bufferInfo.size];
                outputBuffer.get(outData);

                if (m_info != null) {
                    System.arraycopy(outData, 0, output, pos, outData.length);
                    pos += outData.length;
                } else { //保存pps sps 只有开始时 第一个帧里有， 保存起来后面用
                    ByteBuffer spsPpsBuffer = ByteBuffer.wrap(outData);
                    if (spsPpsBuffer.getInt() == 0x00000001) {
                        m_info = new byte[outData.length];
                        System.arraycopy(outData, 0, m_info, 0, outData.length);
                    } else {
                        return -1;
                    }
                }

                mediaCodec.releaseOutputBuffer(outputBufferIndex, false);
                outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 0);
            }
            //key frame   编码器生成关键帧时只有 00 00 00 01 65 没有pps sps， 要加上
            if (output[4] == 0x65) {
                System.arraycopy(output, 0, yuv420, 0, pos);
                System.arraycopy(m_info, 0, output, 0, m_info.length);
                System.arraycopy(yuv420, 0, output, m_info.length, pos);
                pos += m_info.length;
            }

        } catch (Throwable t) {
            t.printStackTrace();
        }

        return pos;
    }

    /**
     * yv12 转 yuv420p  yvu -> yuv
     */
    private void swapYV12toI420(byte[] yv12bytes, byte[] i420bytes, int width, int height) {
        System.arraycopy(yv12bytes, 0, i420bytes, 0, width * height);
        final int srcPos = width * height + width * height / 4;
        System.arraycopy(yv12bytes, srcPos, i420bytes, width * height, width * height / 4);
        System.arraycopy(yv12bytes, width * height, i420bytes, srcPos, width * height / 4);
    }
}
