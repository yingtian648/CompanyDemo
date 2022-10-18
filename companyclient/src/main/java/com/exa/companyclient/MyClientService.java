package com.exa.companyclient;

import android.app.Service;
import android.content.Intent;
import com.android.server.location.gnss.GnssLocationExtHelper;
import android.location.Location;
import android.os.IBinder;

import com.exa.baselib.utils.L;

import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 测试服务中绑定服务
 */
public class MyClientService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        L.d(getClass().getName() + ": onStartCommand");

        GnssLocationExtHelper provider = GnssLocationExtHelper.getInstance();
        provider.init(this, new GnssLocationExtListener());
        provider.bindServer();

        return super.onStartCommand(intent, flags, startId);
    }

    private final class GnssLocationExtListener implements GnssLocationExtHelper.Callback {
        @Override
        public void repoSvStatus(int svCount, int[] svidWithFlags, float[] cn0s, float[] svElevations, float[] svAzimuths, float[] svCarrierFreqs, float[] basebandCn0s) {
            L.d("repoSvStatus: " + svCount + ",svidWithFlags:" + Arrays.toString(svidWithFlags)
                    + ",cn0s:" + Arrays.toString(cn0s)
                    + ",svElevations:" + Arrays.toString(svElevations)
                    + ",svAzimuths:" + Arrays.toString(svAzimuths)
                    + ",svCarrierFreqs:" + Arrays.toString(svCarrierFreqs)
                    + ",basebandCn0s:" + Arrays.toString(basebandCn0s)
            );
        }

        @Override
        public void onLocationChanged(@NonNull Location location) {
            L.d("onLocationChanged: " + location);
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }
    }
}
