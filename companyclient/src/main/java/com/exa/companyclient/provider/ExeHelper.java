package com.exa.companyclient.provider;

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
            EventBus.getDefault().post(new EventBean("获取媒体文件：音频 >> ",files));
            L.d("\n");
            L.d("\n");
            L.d("\n");
            ArrayList<Files> files2 = SystemMediaProviderUtil.getSystemMediaProviderData(App.getContext(), BaseConstants.SystemMediaType.Video);
            EventBus.getDefault().post(new EventBean("获取媒体文件：视频 >> ",files2));
            L.d("\n");
            L.d("\n");
            L.d("\n");
            ArrayList<Files> files3 = SystemMediaProviderUtil.getSystemMediaProviderData(App.getContext(), BaseConstants.SystemMediaType.Image);
            EventBus.getDefault().post(new EventBean("获取媒体文件：图片 >> ",files3));
        });
    }

    public void exeGetMyMediaProviderData() {
        BaseConstants.getFixPool().execute(() -> {
            MyProviderUtil.testMyProvider(App.getContext());
        });
    }
}
