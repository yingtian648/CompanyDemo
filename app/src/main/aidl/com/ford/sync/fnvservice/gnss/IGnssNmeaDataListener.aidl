package com.ford.sync.fnvservice.gnss;

import com.ford.sync.fnvservice.gnss.GgaNmeaData;
import com.ford.sync.fnvservice.gnss.RmcNmeaData;

oneway interface IGnssNmeaDataListener {
  void onGnssGgaDataChanged(in GgaNmeaData ggaNmeaData);
  void onGnssRmcDataChanged(in RmcNmeaData rmcNmeaData);
}
