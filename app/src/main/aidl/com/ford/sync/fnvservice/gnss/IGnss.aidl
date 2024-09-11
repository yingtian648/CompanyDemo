package com.ford.sync.fnvservice.gnss;

import android.os.Bundle;
import com.ford.sync.fnvservice.gnss.IGnssNmeaDataListener;

interface IGnss {
    void pushLocationShiftedData(in Bundle bundle);
    void registerNmeaDataListener(IGnssNmeaDataListener listener);
    void unregisterNmeaDataListener(IGnssNmeaDataListener listener);
}