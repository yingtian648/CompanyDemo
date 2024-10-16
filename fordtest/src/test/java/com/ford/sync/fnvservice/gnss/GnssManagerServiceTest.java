package com.ford.sync.fnvservice.gnss;

import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.lang.reflect.Field;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class GnssManagerServiceTest {

    private static final String CHINA_LATITUDE = "ChinaShiftedLatitude";
    private static final String CHINA_LONGITUDE = "ChinaShiftedLongitude";
    private static final String PAIRING_KEY = "pairingKey";

    @Mock
    private Context mockContext;
    @Mock
    private IBinder mockBinder;

    @Mock
    private IGnss mockGnss;

    private GnssManagerService gnssManagerService;

    private final String GGA = "$GPGGA,073643.00,3204.787326,N,11846.791531,E,6,00,38.8,0.8,M,5.2,M,,*6A";
    private final String GGA1 = "$GPGGA,073644.00,3204.787249,N,11846.786558,E,6,00,38.8,0.8,M,5.2,M,,*6C";
    private final String RMC = "$GPRMC,073645.00,V,3204.787146,N,11846.781373,E,16.1,268.6,070424,6.3,W,E,V*7C";
    private final String RMC1 = "$GPRMC,073647.00,V,3204.786949,N,11846.770525,E,16.9,269.1,070424,6.3,W,E,V*7D";

    private final IGnssNmeaDataListener mListenerToService = new IGnssNmeaDataListener.Stub() {
        @Override
        public void onGnssGgaDataChanged(GgaNmeaData ggaNmeaData) throws RemoteException {
            System.out.println("onGnssGgaDataChanged:" + ggaNmeaData);
        }

        @Override
        public void onGnssRmcDataChanged(RmcNmeaData rmcNmeaData) throws RemoteException {
            System.out.println("onGnssRmcDataChanged:" + rmcNmeaData);
        }
    };

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        gnssManagerService = new GnssManagerService();
        gnssManagerService.init();
        setPrivateField(gnssManagerService, "mLastGga", GGA);
        setPrivateField(gnssManagerService, "mCurrGga", GGA);
        setPrivateField(gnssManagerService, "mLastRmc", RMC);
        setPrivateField(gnssManagerService, "mCurrRmc", RMC);
    }

    @Test
    public void testInit() throws RemoteException {
        gnssManagerService.init();
    }

    @Test
    public void testRegisterNmeaDataListener() throws RemoteException {
        gnssManagerService.registerNmeaDataListener(mListenerToService);
        setPrivateField(gnssManagerService, "mCurrGga", GGA1);
        setPrivateField(gnssManagerService, "mCurrRmc", RMC1);
    }

    @Test
    public void testUnRegisterNmeaDataListener() throws RemoteException {
        gnssManagerService.unregisterNmeaDataListener(mListenerToService);
    }

    @Test
    public void testPushLocationShiftedData() throws RemoteException {
        Bundle bundle = new Bundle();
        bundle.putLong(PAIRING_KEY, 10001L);
        bundle.putDouble(CHINA_LATITUDE, 39.992188);
        bundle.putDouble(CHINA_LONGITUDE, 116.313256949);
        gnssManagerService.pushLocationShiftedData(bundle);
    }

    private void setPrivateField(Object object, String fieldName, Object fieldValue) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(object, fieldValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}