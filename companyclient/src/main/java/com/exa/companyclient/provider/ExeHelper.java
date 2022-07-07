package com.exa.companyclient.provider;

import com.exa.baselib.utils.L;
import com.exa.companyclient.App;
import com.exa.baselib.BaseConstants;

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
        BaseConstants.getFixPool().execute(() -> {
            SystemMediaProviderUtil.getSystemMediaProviderData(App.getContext(), BaseConstants.SystemMediaType.Audio);
            L.d("\n");
            L.d("\n");
            L.d("\n");
            SystemMediaProviderUtil.getSystemMediaProviderData(App.getContext(), BaseConstants.SystemMediaType.Video);
            L.d("\n");
            L.d("\n");
            L.d("\n");
            SystemMediaProviderUtil.getSystemMediaProviderData(App.getContext(), BaseConstants.SystemMediaType.Image);
        });
    }

    public void exeGetMyMediaProviderData() {
        BaseConstants.getFixPool().execute(() -> {
            MyProviderUtil.testMyProvider(App.getContext());
        });
    }
}
