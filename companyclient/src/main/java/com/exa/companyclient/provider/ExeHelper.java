package com.exa.companyclient.provider;

import com.exa.companyclient.Constants;
import com.exa.companyclient.base.App;
import com.exa.companyclient.utils.L;

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
        Constants.getFixPool().execute(() -> {
            SystemMediaProviderUtil.getSystemMediaProviderData(App.getContext(), Constants.SystemMediaType.Audio);
            L.d("\n");
            L.d("\n");
            L.d("\n");
            SystemMediaProviderUtil.getSystemMediaProviderData(App.getContext(), Constants.SystemMediaType.Video);
            L.d("\n");
            L.d("\n");
            L.d("\n");
            SystemMediaProviderUtil.getSystemMediaProviderData(App.getContext(), Constants.SystemMediaType.Image);
        });
    }
}
