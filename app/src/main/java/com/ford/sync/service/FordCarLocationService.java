///**
// * @file FordCarLocationService.java
// * @author Chen Guangming
// * @email chen.guangming@zlingsmart.com
// * @date 2024-7-26
// * @copyright Copyright (c) 2024 zlingsmart
// */
//package com.ford.sync.service;
//
//import android.car.hardware.CarPropertyValue;
//import android.car.hardware.location.IFordCarLocationService;
//import android.car.hardware.power.FordCarPowerManager;
//import android.car.hardware.power.FordCarPowerManager.FordCarPowerStateListener;
//import android.content.Context;
//import android.os.Bundle;
//import android.os.IHwBinder;
//import android.os.RemoteException;
//import android.os.SystemClock;
//import android.os.Handler;
//import android.os.Looper;
//import android.util.AtomicFile;
//import android.util.JsonReader;
//import android.util.JsonWriter;
//import android.util.Log;
//import com.android.car.systeminterface.SystemInterface;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.OutputStreamWriter;
//import java.io.PrintWriter;
//import java.util.Date;
//import java.util.Timer;
//import java.util.TimerTask;
//
//import vendor.zlsmart.gnssext.V1_0.IGnssExt;
//import vendor.zlsmart.gnssext.V1_0.GnssExtData;
//import vendor.zlsmart.gnssext.V1_0.LocationShiftedData;
//import vendor.zlsmart.gnssext.V1_0.ICompassDirCallback;
//import vendor.zlsmart.gnssext.V1_0.FaultInfo;
//import vendor.zlsmart.gnssext.V1_0.CompassDir;
//import vendor.zlsmart.gnssext.V1_0.FixDimension;
//import vendor.zlsmart.gnssext.V1_0.TechMaskBits;
//
///**
// * Implement the API of FordCarLocationManager.
// *
// * @hide
// */
//public class FordCarLocationService extends IFordCarLocationService.Stub implements CarServiceBase,
//        IHwBinder.DeathRecipient {
//
//    private static final String TAG = "FordCarLocationService";
//    private static final byte MSG_TYPE_METADATA_TIME = 0;       // Meta Data/Time
//    private static final byte MSG_TYPE_LOCATION_1 = 1;          // GNSS, Dead Reckoning combined to estimate vehicle’s location
//    private static final byte MSG_TYPE_LOCATION_2 = 2;          // Location supplementary information, such as Heading, Velocity etc.
//    private static final byte MSG_TYPE_LOCATION_QUALITY = 3;    // Location Quality
//    private static final byte MSG_TYPE_SENSOR_QUALITY = 4;      // Sensor Quality
//    private static final byte MSG_TYPE_LOCATION_3 = 6;          // Same as Location 1, but with shifted coordinates only for China
//    private static final byte MSG_TYPE_LOCATION_4 = 7;          // RAW GNSS from Chipset without Dead Reckoning
//    private static final byte MSG_TYPE_LOCATION_5 = 8;          // Map Match Feedback from embedded nav, if equipped
//    private static final int TOTAL_MESSAGE_LENGTH = 64;
//    private static final int PER_MESSAGE_LENGTH = 8;
//    private static final long CAN_MESSAGE_INTERVAL_MS = 1000L;
//    private static final long RETRY_CONNECT_DELAY_MS = 1000L;
//
//    private final Context mContext;
//    private final Handler mHandler;
//    private final CarPropertyService mPropertyService;
//    private final boolean mIsFNV2 = true; // fixme use prop
//    private final Object mLock = new Object();
//    private final LocationStorage mLocationStorage = new LocationStorage();
//    private final byte[] m45EMessages = new byte[TOTAL_MESSAGE_LENGTH];
//    private final AbstractMessageBuilder[] mCanMessageBuilders;
//
//    private IGnssExt mGnssExtService;
//    private Timer mCanMsgTimer;
//    private boolean mShiftedLocationReceived = false;
//
//    private ICompassDirCallback.Stub mCompassDirCallback = new ICompassDirCallback.Stub() {
//        @Override
//        public void onCompassDirChanged(byte direction) throws RemoteException {
//            Log.i(TAG, " OnCompassData : " + direction);
//            //mPropertyService.setProperty(new CarPropertyValue(557851251, 0, Integer.valueOf(direction)), null);
//        }
//    };
//
//    public FordCarLocationService(Context context, CarPropertyService service) {
//        this.mContext = context;
//        this.mPropertyService = service;
//        this.mHandler = new Handler(Looper.getMainLooper());
//
//        if (mIsFNV2) {
//            mCanMessageBuilders = new AbstractMessageBuilder[]{
//                    new MetaDataTimeBuilder(),
//                    new LocationXBuilder(MSG_TYPE_LOCATION_1),
//                    new Location2Builder(),
//                    new LocationQualityBuilder(),
//                    new SensorQualityBuilder(),
//                    new LocationXBuilder(MSG_TYPE_LOCATION_3),
//                    new LocationXBuilder(MSG_TYPE_LOCATION_4),
//                    new LocationXBuilder(MSG_TYPE_LOCATION_5)
//            };
//        } else {
//            mCanMessageBuilders = new AbstractMessageBuilder[]{
//                    new LocationQualityBuilder(),
//                    new SensorQualityBuilder(),
//                    new MetaDataTimeBuilder(),
//                    new LocationXBuilder(MSG_TYPE_LOCATION_1),
//                    new Location2Builder(),
//                    new LocationXBuilder(MSG_TYPE_LOCATION_4),
//                    new LocationXBuilder(MSG_TYPE_LOCATION_5),
//                    new LocationXBuilder(MSG_TYPE_LOCATION_3),
//            };
//        }
//
//        for (int i = 0; i < mCanMessageBuilders.length; i++) {
//            // fix the position of each sub message in whole message body
//            mCanMessageBuilders[i].setOutput(m45EMessages, i * PER_MESSAGE_LENGTH);
//        }
//    }
//
//    @Override
//    public void init() {
//        Log.i(TAG, "Location Service init");
//        mGnssExtService = getGnssExtService();
//        if (mGnssExtService == null) {
//            Log.e(TAG, "GnssExt HAL not available. try again later");
//
//            mHandler.postDelayed(this::init, RETRY_CONNECT_DELAY_MS);
//
//            return;
//        }
//
//        initCanMsgSendTask();
//
//        mLocationStorage.init(mContext);
//    }
//
//    private void initCanMsgSendTask() {
//        if (mCanMsgTimer != null) {
//            mCanMsgTimer.cancel();
//        }
//
//        mCanMsgTimer = new Timer();
//        mCanMsgTimer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                synchronized (mLock) {
//                    // we check the timestamp judge is it last known data
//                    mLocationStorage.setLastKnown();
//                    Log.i(TAG, "isLastKnown " + mLocationStorage.isLastKnown());
//
//                    for (AbstractMessageBuilder mb : mCanMessageBuilders) {
//                        mb.build();
//                    }
//
//                    // mPropertyService.setProperty(new CarPropertyValue(560996902, 0, m45EMessages), null);
//                }
//
//                printMessageContent();
//            }
//        }, 0L, CAN_MESSAGE_INTERVAL_MS);
//    }
//
//    private void printMessageContent() {
//        StringBuilder sb = new StringBuilder();
//        synchronized (mLock) {
//            for (int i = 0; i < mCanMessageBuilders.length; i++) {
//                sb.append(mCanMessageBuilders[i].name()).append(":");
//                for (int j = 0; j < PER_MESSAGE_LENGTH; j++) {
//                    sb.append(String.format("0x%02x,", m45EMessages[i * PER_MESSAGE_LENGTH + j]));
//                }
//                sb.append("\n");
//            }
//        }
//        Log.d(TAG, sb.toString());
//    }
//
//    @Override
//    public void release() {
//        Log.i(TAG, "Location Service release");
//
//        if (mGnssExtService != null) {
//            try {
//                mGnssExtService.unlinkToDeath(this);
//                mGnssExtService = null;
//            } catch (RemoteException e) {
//                Log.i(TAG, "unlinkToDeath fail: " + e.getMessage());
//            }
//        }
//
//        if (mCanMsgTimer != null) {
//            mCanMsgTimer.cancel();
//            mCanMsgTimer = null;
//        }
//
//        mLocationStorage.release();
//    }
//
//    @Override
//    public void dump(PrintWriter writer) {
//    }
//
//    private IGnssExt getGnssExtService() {
//        try {
//            IGnssExt gnssExt = IGnssExt.getService(mIsFNV2 ? "default" : "fnv3");
//            if (gnssExt != null) {
//                gnssExt.linkToDeath(this, 0L);
//
//                gnssExt.registerCompassDirCallback(mCompassDirCallback);
//
//                return gnssExt;
//            }
//        } catch (Exception err) {
//            Log.w(TAG, "getGnssExtService fail: " + err.getMessage());
//        }
//
//        return null;
//    }
//
//    @Override
//    public void serviceDied(long cookie) {
//        Log.w(TAG, "GnssExt HAL Service died.");
//        try {
//            mGnssExtService.unlinkToDeath(this);
//        } catch (RemoteException err) {
//            Log.e(TAG, "Failed to unlinkToDeath", err);
//        }
//
//        Log.d(TAG, "reconnect to GnssExt HAL Service");
//        mGnssExtService = getGnssExtService();
//        if (mGnssExtService == null) {
//            mHandler.postDelayed(this::init, RETRY_CONNECT_DELAY_MS);
//        }
//    }
//
//    @Override
//    public void handleLocationMessage(Bundle bundle) {
//        long pairingKey = bundle.getLong("pairingKey");
//
//        Log.i(TAG, "handleLocationMessage pairingKey = " + pairingKey);
//        try {
//            if (mGnssExtService != null) {
//
//                synchronized (mLock) {
//                    mLocationStorage.update(mGnssExtService.getGnssExtData(pairingKey),
//                            bundle.getDouble("ChinaShiftedLatitude"), bundle.getDouble("ChinaShiftedLongitude"));
//                }
//
//                if (!mShiftedLocationReceived) {
//                    Log.e(TAG, "first time received shifted location, reschedule timer");
//                    mShiftedLocationReceived = true;
//
//                    // we reschedule timer to keep the pace of 45E and receiving shifted data consistent
//                    initCanMsgSendTask();
//                }
//            } else {
//                Log.e(TAG, "handleLocationMessage -> GnssExtService is null");
//            }
//        } catch (Exception err) {
//            Log.i(TAG, "handleLocationMessage fail: " + err.getMessage());
//        }
//    }
//
//    /**
//     * store the latitude and longitude
//     */
//    private static class Coordinates {
//
//        protected static final double LATITUDE_DEFAULT = 39.990912d;
//        protected static final double LONGITUDE_DEFAULT = 116.307158d;
//
//        private final String mTag;
//
//        public double mLatitude = LATITUDE_DEFAULT;
//        public double mLongitude = LONGITUDE_DEFAULT;
//
//        public Coordinates(String tag) {
//            mTag = tag;
//        }
//
//        public double getLatitude() {
//            return mLatitude;
//        }
//
//        public double getLongitude() {
//            return mLongitude;
//        }
//
//        public void setLatitude(double latitude) {
//            mLatitude = latitude;
//        }
//
//        public void setLongitude(double longitude) {
//            mLongitude = longitude;
//        }
//
//        public boolean updateLocation(double latitude, double longitude) {
//            if (Double.compare(latitude, 0.0) == 0 && Double.compare(longitude, 0.0) == 0) {
//                Log.w(TAG, "invalid coordinates of " + mTag);
//                return false;
//            }
//
//            mLatitude = latitude;
//            mLongitude = longitude;
//
//            return true;
//        }
//    }
//
//    /**
//     * cache the data in memory or storage, and check is it the last known value
//     */
//    private static class LocationStorage implements FordCarPowerStateListener {
//
//        private static final String FILENAME = "ford_location_cache.json";
//
//        // Constants for location serialization.
//        private static final String LATITUDE_RAW = "latitude_raw";
//        private static final String LONGITUDE_RAW = "longitude_raw";
//        private static final String LATITUDE_FINAL = "latitude_final";
//        private static final String LONGITUDE_FINAL = "longitude_final";
//        private static final String LATITUDE_SHIFTED = "latitude_shifted";
//        private static final String LONGITUDE_SHIFTED = "longitude_shifted";
//        private static final String COMPASS_DIR = "compass_dir";
//        private static final String HEADING = "heading";
//        private static final String UPDATE_TIME = "update_time";
//
//        private static final long LAST_KNOWN_INTERVAL_MS = 2000L;
//        private static final int FULL_CIRCLE_DEGREE = 360;
//        private static final int SATELLITE_COUNT_MAX = 15;
//        private static final byte DATA_GOOD_USE = 1;
//        private static final byte DATA_BAD_USE = 0;
//
//        private final Coordinates mCoordRaw = new Coordinates("raw");
//        private final Coordinates mCoordFinal = new Coordinates("final");
//        private final Coordinates mCoordShifted = new Coordinates("shifted");
//        private final Coordinates mCoordDefault = new Coordinates("default");
//
//        private FordCarPowerManager mCarPowerManager;
//
//        private GnssExtData mGnssExtData = new GnssExtData(); // avoid null exception if never updated
//        private float mHeading;
//        private byte mCompassDir;
//
//        private long mLastUpdateTime = SystemClock.elapsedRealtime();
//        private long mCacheTime = 0L;
//        private boolean mIsLastKnown = true;
//
//        public void init(Context context) {
//            mCarPowerManager = CarLocalServices.createFordCarPowerManager(context);
//            if (mCarPowerManager != null) { // null case happens for testing.
//                mCarPowerManager.setFordListener(this);
//            } else {
//                Log.w(TAG, "mCarPowerManager is null");
//            }
//
//            readLocationFromCacheFile();
//        }
//
//        public void release() {
//            if (mCarPowerManager != null) {
//                mCarPowerManager.clearListener();
//            }
//        }
//
//        public void update(GnssExtData extData, double shiftedLatitude, double shiftedLongitude) {
//            mGnssExtData = extData;
//
//            // we need update last known of location1,location3,location4,compass,heading
//            if (mCoordRaw.updateLocation(extData.coordRaw.latitude, extData.coordRaw.longitude)
//                    && mCoordFinal.updateLocation(extData.coordFinal.latitude, extData.coordFinal.longitude)
//                    && mCoordShifted.updateLocation(shiftedLatitude, shiftedLongitude)) {
//                mLastUpdateTime = SystemClock.elapsedRealtime();
//            }
//
//            updateHeadingAndDirection(extData.heading, extData.compassDir);
//        }
//
//        private void updateHeadingAndDirection(float heading, byte compassDir) {
//            // FNV3 TCU will pass 360°, we need to handle it.
//            mHeading = (heading >= FULL_CIRCLE_DEGREE) ? (heading - FULL_CIRCLE_DEGREE) : heading;
//            mCompassDir = compassDir;
//        }
//
//        private long getUtcTime() {
//            // todo replace with System.currentTimeMillis() if mIsLastKnown
//            return mGnssExtData.UTC;
//        }
//
//        private Coordinates getCoordinates(int type) {
//            switch (type) {
//                case MSG_TYPE_LOCATION_1:
//                    return mCoordFinal;
//
//                case MSG_TYPE_LOCATION_3:
//                    return mCoordShifted;
//
//                case MSG_TYPE_LOCATION_4:
//                    return mCoordRaw;
//
//                case MSG_TYPE_LOCATION_5: // fall through
//                default:
//                    return mCoordDefault;
//            }
//        }
//
//        public boolean isValidLocation1() {
//            if (mIsLastKnown) {
//                return false;
//            }
//
//            return (mGnssExtData.fixDimension != FixDimension.DIMENSION_NO_FIX
//                    && mGnssExtData.fixDimension != FixDimension.DIMENSION_LAST_KNOWN);
//        }
//
//        private boolean isValidLocation3() {
//            if (mIsLastKnown) {
//                return false;
//            }
//
//            // shifted coordinates always appear in china, means both latitude and longitude > 0
//            return (mCoordShifted.mLatitude > 0 && mCoordShifted.mLongitude > 0);
//        }
//
//        public FaultInfo getFaultInfo() {
//            return mGnssExtData.faultInfo;
//        }
//
//        public byte getDataGoodToUse() {
//            if (mIsLastKnown) {
//                return DATA_BAD_USE;
//            }
//
//            return mGnssExtData.dataGoodToUse ? DATA_GOOD_USE : DATA_BAD_USE;
//        }
//
//        public byte getCompassDir() {
//            return mCompassDir;
//        }
//
//        public byte getTechMask() {
//            if (mIsLastKnown) {
//                return 0;
//            }
//
//            return mGnssExtData.techMask;
//        }
//
//        public byte getFixDim() {
//            if (mIsLastKnown) {
//                return 0;
//            }
//
//            return mGnssExtData.fixDimension;
//        }
//
//        public float getHeading() {
//            return mHeading;
//        }
//
//        public double getAltitude() {
//            if (mIsLastKnown) {
//                return 0;
//            }
//
//            return mGnssExtData.altitude;
//        }
//
//        public double getVelocity() {
//            if (mIsLastKnown) {
//                return 0;
//            }
//
//            return mGnssExtData.velocity;
//        }
//
//        public float getHDop() {
//            return mGnssExtData.hDop;
//        }
//
//        public float getVDop() {
//            return mGnssExtData.vDop;
//        }
//
//        public float getPDop() {
//            return mGnssExtData.pDop;
//        }
//
//        private byte getAccelCaliStatus() {
//            return mGnssExtData.accelCaliStatus;
//        }
//
//        private byte getGyroCaliStatus() {
//            return mGnssExtData.gyroCaliStatus;
//        }
//
//        public int getNumGpsSvUsed() {
//            return getNumSvUsed(mGnssExtData.numGpsSvUsed);
//        }
//
//        public int getNumGloSvUsed() {
//            return getNumSvUsed(mGnssExtData.numGloSvUsed);
//        }
//
//        public int getNumGalSvUsed() {
//            return getNumSvUsed(mGnssExtData.numGalSvUsed);
//        }
//
//        public int getNumBdsSvUsed() {
//            return getNumSvUsed(mGnssExtData.numBdsSvUsed);
//        }
//
//        private int getNumSvUsed(int num) {
//            if (mIsLastKnown) {
//                return 0;
//            }
//
//            return Math.min(num, SATELLITE_COUNT_MAX);
//        }
//
//        public void setLastKnown() {
//            mIsLastKnown = (SystemClock.elapsedRealtime() - mLastUpdateTime > LAST_KNOWN_INTERVAL_MS);
//        }
//
//        public boolean isLastKnown() {
//            return mIsLastKnown;
//        }
//
//        private File getLocationCacheFile() {
//            SystemInterface systemInterface = CarLocalServices.getService(SystemInterface.class);
//            return new File(systemInterface.getSystemCarDir(), FILENAME);
//        }
//
//        /**
//         * Store data into cache file.
//         */
//        private void storeLocationToCacheFile() {
//            Log.d(TAG, "Storing location");
//            AtomicFile atomicFile = new AtomicFile(getLocationCacheFile());
//            FileOutputStream fos = null;
//            try {
//                fos = atomicFile.startWrite();
//                try (JsonWriter jsonWriter = new JsonWriter(new OutputStreamWriter(fos, "UTF-8"))) {
//                    jsonWriter.beginObject();
//                    jsonWriter.name(LATITUDE_RAW).value(mCoordRaw.getLatitude());
//                    jsonWriter.name(LONGITUDE_RAW).value(mCoordRaw.getLongitude());
//                    jsonWriter.name(LATITUDE_FINAL).value(mCoordFinal.getLatitude());
//                    jsonWriter.name(LONGITUDE_FINAL).value(mCoordFinal.getLongitude());
//                    jsonWriter.name(LATITUDE_SHIFTED).value(mCoordShifted.getLatitude());
//                    jsonWriter.name(LONGITUDE_SHIFTED).value(mCoordShifted.getLongitude());
//                    jsonWriter.name(COMPASS_DIR).value(mCompassDir);
//                    jsonWriter.name(HEADING).value(mHeading);
//                    jsonWriter.name(UPDATE_TIME).value(System.currentTimeMillis());
//                    jsonWriter.endObject();
//                }
//                atomicFile.finishWrite(fos);
//            } catch (IOException e) {
//                Log.e(TAG, "Unable to write to disk", e);
//                atomicFile.failWrite(fos);
//            }
//        }
//
//        /**
//         * read data from the cache file.
//         */
//        private void readLocationFromCacheFile() {
//            File file = getLocationCacheFile();
//            AtomicFile atomicFile = new AtomicFile(file);
//            try (FileInputStream fis = atomicFile.openRead()) {
//                JsonReader reader = new JsonReader(new InputStreamReader(fis, "UTF-8"));
//                reader.beginObject();
//                while (reader.hasNext()) {
//                    String name = reader.nextName();
//                    switch (name) {
//                        case LATITUDE_RAW:
//                            mCoordRaw.setLatitude(reader.nextDouble());
//                            break;
//                        case LONGITUDE_RAW:
//                            mCoordRaw.setLongitude(reader.nextDouble());
//                            break;
//                        case LATITUDE_FINAL:
//                            mCoordFinal.setLatitude(reader.nextDouble());
//                            break;
//                        case LONGITUDE_FINAL:
//                            mCoordFinal.setLongitude(reader.nextDouble());
//                            break;
//                        case LATITUDE_SHIFTED:
//                            mCoordShifted.setLatitude(reader.nextDouble());
//                            break;
//                        case LONGITUDE_SHIFTED:
//                            mCoordShifted.setLongitude(reader.nextDouble());
//                            break;
//                        case COMPASS_DIR:
//                            mCompassDir = (byte) reader.nextInt();
//                            break;
//                        case HEADING:
//                            mHeading = (float) reader.nextDouble();
//                            break;
//                        case UPDATE_TIME:
//                            mCacheTime = reader.nextLong();
//                            break;
//                        default:
//                            Log.w(TAG, String.format("Unrecognized key: %s", name));
//                            reader.skipValue();
//                    }
//                }
//                reader.endObject();
//            } catch (FileNotFoundException e) {
//                Log.w(TAG, "Cache file not found: " + file.getAbsolutePath());
//            } catch (IOException e) {
//                Log.e(TAG, "Unable to read from disk", e);
//            } catch (NumberFormatException | IllegalStateException e) {
//                Log.e(TAG, "Unexpected format", e);
//            }
//        }
//
//        @Override
//        public void onStateChanged(int state) {
//            // do nothing
//        }
//
//        @Override
//        public void onPowerOff() {
//            storeLocationToCacheFile();
//        }
//
//        @Override
//        public void onPopChanged(int popState, int leftSeconds) {
//            // do nothing
//        }
//    }
//
//    private abstract static class AbstractMessageBuilder {
//
//        protected static final int BYTE_MAX = 255;
//        protected static final int MSG_BYTE_0 = 0;
//        protected static final int MSG_BYTE_1 = 1;
//        protected static final int MSG_BYTE_2 = 2;
//        protected static final int MSG_BYTE_3 = 3;
//        protected static final int MSG_BYTE_4 = 4;
//        protected static final int MSG_BYTE_5 = 5;
//        protected static final int MSG_BYTE_6 = 6;
//        protected static final int MSG_BYTE_7 = 7;
//
//        protected static final int SHIFT_BITS_1 = 1;
//        protected static final int SHIFT_BITS_2 = 2;
//        protected static final int SHIFT_BITS_3 = 3;
//        protected static final int SHIFT_BITS_4 = 4;
//        protected static final int SHIFT_BITS_5 = 5;
//        protected static final int SHIFT_BITS_6 = 6;
//        protected static final int SHIFT_BITS_7 = 7;
//        protected static final int SHIFT_BITS_8 = 8;
//        protected static final int SHIFT_BITS_12 = 12;
//        protected static final int SHIFT_BITS_16 = 16;
//
//        protected static final int MSG_HEADER_SHIFT = SHIFT_BITS_4;
//        protected static final int ZOOM_IN_TEN = 10;
//
//        protected final byte mMsgType;
//
//        protected int mPos0;
//        protected int mPos1;
//        protected int mPos2;
//        protected int mPos3;
//        protected int mPos4;
//        protected int mPos5;
//        protected int mPos6;
//        protected int mPos7;
//        protected byte[] mBody;
//
//        public AbstractMessageBuilder(byte msgType) {
//            mMsgType = msgType;
//        }
//
//        public void setOutput(byte[] body, int startPos) {
//            mBody = body;
//
//            mPos0 = startPos + MSG_BYTE_0;
//            mPos1 = startPos + MSG_BYTE_1;
//            mPos2 = startPos + MSG_BYTE_2;
//            mPos3 = startPos + MSG_BYTE_3;
//            mPos4 = startPos + MSG_BYTE_4;
//            mPos5 = startPos + MSG_BYTE_5;
//            mPos6 = startPos + MSG_BYTE_6;
//            mPos7 = startPos + MSG_BYTE_7;
//
//            Log.v(TAG, name() + " range: " + mPos0 + " - " + mPos7);
//        }
//
//        protected byte packMessageHeader() {
//            return (byte) (mMsgType << MSG_HEADER_SHIFT);
//        }
//
//        abstract public void build();
//
//        abstract public String name();
//    }
//
//    /**
//     * build the location quality message
//     */
//    private class LocationQualityBuilder extends AbstractMessageBuilder {
//
//        public LocationQualityBuilder() {
//            super(MSG_TYPE_LOCATION_QUALITY);
//        }
//
//        private byte formatDop(float dop) {
//            // reference SPSS, xDop range is 0 - 25.5, step is 0.1
//            return (byte) Math.min(ZOOM_IN_TEN * dop, BYTE_MAX);
//        }
//
//        @Override
//        public void build() {
//            mBody[mPos0] = packMessageHeader();
//            mBody[mPos1] = 0;
//
//            if (mLocationStorage.isLastKnown()) {
//                // Location Quality fill default value when using last known
//                mBody[mPos2] = 0;
//                mBody[mPos3] = 0;
//                mBody[mPos4] = 0;
//            } else {
//                mBody[mPos2] = formatDop(mLocationStorage.getPDop());
//                mBody[mPos3] = formatDop(mLocationStorage.getHDop());
//                mBody[mPos4] = formatDop(mLocationStorage.getVDop());
//            }
//            mBody[mPos5] = 0;
//            mBody[mPos6] = 0;
//            mBody[mPos7] = 0;
//        }
//
//        @Override
//        public String name() {
//            return "LoQuality";
//        }
//    }
//
//    /**
//     * build the sensor quality message
//     */
//    private class SensorQualityBuilder extends AbstractMessageBuilder {
//
//        private static final int ACCEL_CAL_STAT_SHIFT = SHIFT_BITS_5;
//
//        public SensorQualityBuilder() {
//            super(MSG_TYPE_SENSOR_QUALITY);
//        }
//
//        @Override
//        public void build() {
//            mBody[mPos0] = packMessageHeader();
//            if (mLocationStorage.isLastKnown()) {
//                mBody[mPos1] = 0;
//            } else {
//                mBody[mPos1] = (byte) ((mLocationStorage.getAccelCaliStatus() << ACCEL_CAL_STAT_SHIFT)
//                        | mLocationStorage.getGyroCaliStatus());
//            }
//            mBody[mPos2] = 0;
//            mBody[mPos3] = 0;
//            mBody[mPos4] = 0;
//            mBody[mPos5] = 0;
//            mBody[mPos6] = 0;
//            mBody[mPos7] = 0;
//        }
//
//        @Override
//        public String name() {
//            return "SeQuality";
//        }
//    }
//
//    /**
//     * build the metadata & time message
//     */
//    private class MetaDataTimeBuilder extends AbstractMessageBuilder {
//
//        private static final int VALID_LOCATION_SHIFT = SHIFT_BITS_4;
//        private static final int BIT_MASK_LEN = 4;
//        private static final int GPS_YEAR_MIN = 2014;
//        private static final int UTC_YEAR_BEGIN = 1900;
//        private static final int PROTOCOL_VERSION_SHIFT = 1;
//        private static final byte PROTOCOL_VERSION = 1;
//
//
//        private final Date mDate = new Date();
//
//        public MetaDataTimeBuilder() {
//            super(MSG_TYPE_METADATA_TIME);
//        }
//
//        private byte packMetaMessageHeader(byte dataGoodToUse) {
//            return (byte) ((PROTOCOL_VERSION << PROTOCOL_VERSION_SHIFT) | dataGoodToUse);
//        }
//
//        @Override
//        public void build() {
//            mDate.setTime(mLocationStorage.getUtcTime());
//            int utcYr = 0;
//
//            Log.d(TAG, "mGnssExtData.UTC " + mLocationStorage.getUtcTime());
//            if ((mDate.getYear() + UTC_YEAR_BEGIN) >= GPS_YEAR_MIN) {
//                utcYr = (mDate.getYear() + UTC_YEAR_BEGIN) - GPS_YEAR_MIN;
//            } else {
//                Log.w(TAG, "year < 2014, send 0 to MCU, it is : " + (mDate.getYear() + UTC_YEAR_BEGIN));
//            }
//            mBody[mPos0] = packMetaMessageHeader(mLocationStorage.getDataGoodToUse());
//            mBody[mPos1] = (byte) mDate.getHours();
//            mBody[mPos2] = (byte) mDate.getMinutes();
//            mBody[mPos3] = (byte) mDate.getSeconds();
//            mBody[mPos4] = (byte) mDate.getDate();
//            mBody[mPos5] = (byte) (mDate.getMonth() + 1);
//            mBody[mPos6] = (byte) utcYr;
//
//            if (mLocationStorage.isLastKnown()) {
//                mBody[mPos7] = 0;
//            } else {
//                FaultInfo faultInfo = mLocationStorage.getFaultInfo();
//
//                // both gyro and accel has no error means sensor quality data is valid
//                boolean sensorValid = !(faultInfo.gyro || faultInfo.accel);
//                // Location5 always invalid
//                boolean[] valid = {mLocationStorage.isValidLocation1(), sensorValid,
//                        mLocationStorage.isValidLocation3(), false};
//                byte validLocBitMask = toBitMasks(valid);
//
//                // one of acce and gnss receiver fault means receiver fault
//                boolean receiverFault = (faultInfo.gnssReceiver || faultInfo.accel);
//                boolean[] fault = {faultInfo.gnssAnt, receiverFault, faultInfo.gyro, faultInfo.wheelTick};
//                byte faultMask = toBitMasks(fault);
//
//                mBody[mPos7] = (byte) (faultMask | (validLocBitMask << VALID_LOCATION_SHIFT));
//            }
//        }
//
//        private byte toBitMasks(boolean[] masks) {
//            byte ret = 0;
//            for (int i = 0; i < BIT_MASK_LEN; i++) {
//                if (masks[i]) {
//                    ret |= (byte) (1 << i);
//                }
//            }
//            return ret;
//        }
//
//        @Override
//        public String name() {
//            return "MetDatTim";
//        }
//    }
//
//    /**
//     * build the Location-2 message
//     */
//    private class Location2Builder extends AbstractMessageBuilder {
//
//        /**
//         * Sensors Uncalibrated with no Fix
//         */
//        private static final byte FIX_TYPE_DR_OFF = 0x0;
//        /**
//         * Sensors Calibrated with no Fix and no updated location
//         */
//        private static final byte FIX_TYPE_NO_FIX = 0x1;
//        /**
//         * 2D fix
//         */
//        private static final byte FIX_TYPE_2D_FIX = 0x2;
//        /**
//         * 3D fix and DR Uncalibrated
//         */
//        private static final byte FIX_TYPE_3D_FIX = 0x3;
//        /**
//         * DR only
//         */
//        private static final byte FIX_TYPE_DR_ONLY = 0x4;
//        /**
//         * 3D fix and DR Calibrated
//         */
//        private static final byte FIX_TYPE_3D_FIX_DR = 0x5;
//        /**
//         * 3D fix and DR Calibrated
//         */
//        private static final byte FIX_TYPE_LAST_KNOWN = 0x6;
//
//        private static final int ALTITUDE_SHIFT = 1000;
//        private static final int ALTITUDE_ACCURACY = 5;
//
//        public Location2Builder() {
//            super(MSG_TYPE_LOCATION_2);
//        }
//
//        private byte calFixType(byte techMask, byte fixDimension) {
//            if (techMask == TechMaskBits.UNKOWN) {
//                return FIX_TYPE_NO_FIX;
//            } else if (fixDimension == FixDimension.DIMENSION_2D) {
//                return FIX_TYPE_2D_FIX;
//            } else if ((fixDimension == FixDimension.DIMENSION_3D)
//                    && (techMask == TechMaskBits.GNSS
//                    || techMask == TechMaskBits.SBAS
//                    || techMask == TechMaskBits.GNSS_SBAS)) {
//                return FIX_TYPE_3D_FIX;
//            } else if (techMask == TechMaskBits.DR) {
//                return FIX_TYPE_DR_ONLY;
//            } else if ((fixDimension == FixDimension.DIMENSION_3D)
//                    && (techMask == TechMaskBits.DR_GNSS
//                    || techMask == TechMaskBits.DR_SBAS
//                    || techMask == TechMaskBits.GNSS_DR_SBAS)) {
//                return FIX_TYPE_3D_FIX_DR;
//            } else if (fixDimension == FixDimension.DIMENSION_LAST_KNOWN) {
//                return FIX_TYPE_LAST_KNOWN;
//            }
//
//            return FIX_TYPE_DR_OFF;
//        }
//
//        private byte packMessageHeaderWithHeading(int heading) {
//            return (byte) ((heading >> SHIFT_BITS_8) | (MSG_TYPE_LOCATION_2 << MSG_HEADER_SHIFT));
//        }
//
//        @Override
//        public void build() {
//            // reference SPSS, heading range is 0 - 359.9, step is 0.1.
//            int heading = (int) (ZOOM_IN_TEN * mLocationStorage.getHeading());
//            // reference SPSS, altitude range is -1000 - 9000, and step is 5
//            int mslAlt = (int) ((ALTITUDE_SHIFT + mLocationStorage.getAltitude()) / ALTITUDE_ACCURACY);
//            // reference SPSS, velocity range is 0 - 409.5, step is 0.1
//            int velocity = (int) (ZOOM_IN_TEN * mLocationStorage.getVelocity());
//            byte compDir = mLocationStorage.getCompassDir();
//
//            mBody[mPos0] = packMessageHeaderWithHeading(heading);
//            mBody[mPos1] = (byte) heading;
//            mBody[mPos2] = (byte) (mslAlt >> SHIFT_BITS_3);
//            mBody[mPos3] = (byte) ((mslAlt << SHIFT_BITS_5) | (velocity >> SHIFT_BITS_8));
//            mBody[mPos4] = (byte) velocity;
//
//            if (mLocationStorage.isLastKnown()) {
//                mBody[mPos5] = (byte) ((compDir << SHIFT_BITS_4) | FIX_TYPE_LAST_KNOWN);
//                mBody[mPos6] = 0;
//                mBody[mPos7] = 0;
//            } else {
//                byte fixType = calFixType(mLocationStorage.getTechMask(), mLocationStorage.getFixDim());
//                mBody[mPos5] = (byte) ((compDir << SHIFT_BITS_4) | fixType);
//                mBody[mPos6] = (byte) ((mLocationStorage.getNumGpsSvUsed() << SHIFT_BITS_4)
//                        | mLocationStorage.getNumGloSvUsed());
//                mBody[mPos7] = (byte) ((mLocationStorage.getNumGalSvUsed() << SHIFT_BITS_4)
//                        | mLocationStorage.getNumBdsSvUsed());
//            }
//        }
//
//        @Override
//        public String name() {
//            return "Location2";
//        }
//    }
//
//    /**
//     * build the Location-1,3,4,5 message
//     */
//    private class LocationXBuilder extends AbstractMessageBuilder {
//
//        private static final int LAT_DEG_INT_SHIFT = SHIFT_BITS_3;
//        private static final byte LOC_DEG_NEGATIVE = 0;
//        private static final byte LOC_DEG_POSITIVE = 1;
//        private static final int FRACTIONAL_TIMES = 1000000;
//
//        public LocationXBuilder(byte msgType) {
//            super(msgType);
//        }
//
//        private byte getLocationSign(double loc) {
//            return (loc > 0) ? LOC_DEG_POSITIVE : LOC_DEG_NEGATIVE;
//        }
//
//        private byte packLocationMessageHeader(byte latInt) {
//            return (byte) ((mMsgType << MSG_HEADER_SHIFT) | (latInt >> LAT_DEG_INT_SHIFT));
//        }
//
//        @Override
//        public void build() {
//            Coordinates coord = mLocationStorage.getCoordinates(mMsgType);
//            double latitude = coord.getLatitude();
//            double longitude = coord.getLongitude();
//            byte laSign = getLocationSign(latitude);
//            byte loSign = getLocationSign(longitude);
//            double laAbs = Math.abs(latitude);
//            double loAbs = Math.abs(longitude);
//
//            int laInt = (int) laAbs;
//            int laFrac = (int) Math.round((laAbs - laInt) * FRACTIONAL_TIMES);
//
//            int loInt = (int) loAbs;
//            int loFrac = (int) Math.round((loAbs - loInt) * FRACTIONAL_TIMES);
//
//            Log.d(TAG, String.format("%s lat:%f -> %d %d,s %d", name(), latitude, laInt, laFrac, laSign));
//            Log.d(TAG,
//                    String.format("%s lon:%f -> %d %d,s %d", name(), longitude, loInt, loFrac, loSign));
//
//            mBody[mPos0] = packLocationMessageHeader((byte) laInt);
//            mBody[mPos1] = (byte) ((laInt << SHIFT_BITS_5) | (laSign << SHIFT_BITS_4) | (laFrac
//                    >> SHIFT_BITS_16));
//            mBody[mPos2] = (byte) (laFrac >> SHIFT_BITS_8);
//            mBody[mPos3] = (byte) laFrac;
//            mBody[mPos4] = (byte) loInt;
//            mBody[mPos5] = (byte) (loFrac >> SHIFT_BITS_12);
//            mBody[mPos6] = (byte) (loFrac >> SHIFT_BITS_4);
//            mBody[mPos7] = (byte) ((loFrac << SHIFT_BITS_4) | (loSign << SHIFT_BITS_3));
//        }
//
//        @Override
//        public String name() {
//            switch (mMsgType) {
//                case MSG_TYPE_LOCATION_1:
//                    return "Location1";
//
//                case MSG_TYPE_LOCATION_3:
//                    return "Location3";
//
//                case MSG_TYPE_LOCATION_4:
//                    return "Location4";
//
//                case MSG_TYPE_LOCATION_5:
//                    return "Location5";
//
//                default:
//                    return "";
//            }
//        }
//    }
//}