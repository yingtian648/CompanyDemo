package com.exa.companyclient.provider;

import com.exa.baselib.bean.Files;
import com.exa.baselib.utils.L;
import com.exa.companyclient.App;
import com.exa.baselib.BaseConstants;
import com.exa.baselib.bean.EventBean;

import org.greenrobot.eventbus.EventBus;

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
            List<Files> files = SystemMediaProviderUtil.getSystemMediaProviderData(App.getContext(), BaseConstants.SystemMediaType.Audio);
            EventBus.getDefault().post(new EventBean(files));
            L.d("\n");
            L.d("\n");
            L.d("\n");
            List<Files> files2 = SystemMediaProviderUtil.getSystemMediaProviderData(App.getContext(), BaseConstants.SystemMediaType.Video);
            EventBus.getDefault().post(new EventBean(files2));
            L.d("\n");
            L.d("\n");
            L.d("\n");
            List<Files> files3 = SystemMediaProviderUtil.getSystemMediaProviderData(App.getContext(), BaseConstants.SystemMediaType.Image);
            EventBus.getDefault().post(new EventBean(files3));
        });
    }

    public void exeGetMyMediaProviderData() {
        BaseConstants.getFixPool().execute(() -> {
            MyProviderUtil.testMyProvider(App.getContext());
        });
    }
}
