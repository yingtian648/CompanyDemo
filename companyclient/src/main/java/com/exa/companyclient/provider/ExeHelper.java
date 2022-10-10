package com.exa.companyclient.provider;

import android.content.Context;

import com.exa.baselib.bean.Files;
import com.exa.baselib.utils.L;
import com.exa.companyclient.App;
import com.exa.baselib.BaseConstants;
import com.exa.baselib.bean.EventBean;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class ExeHelper {
    private ExeHelper() {
    }

    public static ExeHelper getInstance() {
        return ClassHolder.helper;
    }

    private static class ClassHolder {
        private static ExeHelper helper = new ExeHelper();
    }

    public void exeGetSystemMediaProviderData() {
        EventBus.getDefault().post(new EventBean("开始获取结果..."));
        BaseConstants.getFixPool().execute(() -> {
            ArrayList<Files> files = SystemMediaProviderUtil.getSystemMediaProviderData(App.getContext(), BaseConstants.SystemMediaType.Audio);
            EventBus.getDefault().post(new EventBean(2, "获取媒体文件：音频 >> ", files));
            L.d("\n");
            L.d("\n");
            L.d("\n");
            ArrayList<Files> files2 = SystemMediaProviderUtil.getSystemMediaProviderData(App.getContext(), BaseConstants.SystemMediaType.Video);
            EventBus.getDefault().post(new EventBean(3, "获取媒体文件：视频 >> ", files2));
            L.d("\n");
            L.d("\n");
            L.d("\n");
            ArrayList<Files> files3 = SystemMediaProviderUtil.getSystemMediaProviderData(App.getContext(), BaseConstants.SystemMediaType.Image);
            EventBus.getDefault().post(new EventBean(1, "获取媒体文件：图片 >> ", files3));
            L.d("\n");
            L.d("\n");
            L.d("\n");
            getSystemAudioThumbs(files);
        });
    }

    public void getSystemAudioThumbs(ArrayList<Files> files) {
        EventBus.getDefault().post(new EventBean("开始获取歌曲封面..."));
        List<String> thumbIds = new ArrayList<>();
        for (int i = 0; i < files.size(); i++) {
            if (files.get(i).path != null && files.get(i).album_id != null && !thumbIds.contains(files.get(i).album_id)) {
                thumbIds.add(files.get(i).album_id);
            }
        }
        if (!thumbIds.isEmpty()) {
            List<Files> filesThumb = SystemMediaProviderUtil.getAudioAlbumThumbnail(App.getContext(), thumbIds.toArray(new String[]{}));
            EventBus.getDefault().post(new EventBean(4, "获取媒体文件：音频封面 >> ", filesThumb));
        }
    }

    public void exeGetMyMediaProviderData() {
        BaseConstants.getFixPool().execute(() -> {
            MyProviderUtil.testMyProvider(App.getContext());
        });
    }
}
