package com.ford.sync.fnvservice.gnss;

import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

import com.ford.sync.fnvservice.FordFnv;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GnssManagerTest {

    private static final String CHINA_LATITUDE = "ChinaShiftedLatitude";
    private static final String CHINA_LONGITUDE = "ChinaShiftedLongitude";
    private static final String PAIRING_KEY = "pairingKey";
    @Mock
    private FordFnv mockFordFnv;

    @Mock
    private IBinder mockBinder;

    @Mock
    private IGnss mockGnss;

    private GnssManager gnssManager;
    private List<GnssManager.GnssNmeaDataListener> mListeners;


    private final GnssManager.GnssNmeaDataListener gnssNmeaDataListener = new GnssManager.GnssNmeaDataListener() {

        @Override
        public void onGnssGgaDataChanged(GgaNmeaData ggaNmeaData) {
            System.out.println("onGnssGgaDataChanged:" + ggaNmeaData);
        }

        @Override
        public void onGnssRmcDataChanged(RmcNmeaData rmcNmeaData) {
            System.out.println("onGnssGgaDataChanged:" + rmcNmeaData);
        }
    };

    @Before
    public void setUp() throws Exception {
        when(IGnss.Stub.asInterface(mockBinder)).thenReturn(mockGnss);
        gnssManager = new GnssManager(mockFordFnv, mockBinder);
        mListeners = getField(gnssManager);
    }

    @SuppressWarnings("unchecked")
    private <T> T getField(GnssManager gnssManager) throws Exception {
        Field field = GnssManager.class.getDeclaredField("mListeners");
        field.setAccessible(true);
        return (T) field.get(gnssManager);
    }

    @Test
    public void testInit() throws RemoteException {
        gnssManager.init();
    }

    @Test
    public void testRegisterNmeaDataListener() throws RemoteException {
        gnssManager.registerNmeaDataListener(gnssNmeaDataListener);
        assertTrue(mListeners.contains(gnssNmeaDataListener));
    }

    @Test
    public void testUnRegisterNmeaDataListener() throws RemoteException {
        gnssManager.unregisterNmeaDataListener(gnssNmeaDataListener);
        assertFalse(mListeners.contains(gnssNmeaDataListener));
    }

    @Test
    public void testPushLocationShiftedData() throws RemoteException {
        Bundle bundle = new Bundle();
        bundle.putLong(PAIRING_KEY, 10001L);
        bundle.putDouble(CHINA_LATITUDE, 39.992188);
        bundle.putDouble(CHINA_LONGITUDE, 116.313256949);
        gnssManager.pushLocationShiftedData(bundle);
    }

    @Test
    public void testRelease() throws RemoteException {
        gnssManager.release();
        assertTrue(mListeners.isEmpty());
    }
}