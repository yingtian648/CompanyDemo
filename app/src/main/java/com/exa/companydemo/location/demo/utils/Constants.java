/*
 * Copyright (C) 2022 SYNCORE AUTOTECH
 *
 * All Rights Reserved by SYNCORE AUTOTECH Co., Ltd and its affiliates.
 * You may not use, copy, distribute, modify, transmit in any form this file
 * except in compliance with SYNCORE AUTOTECH in writing by applicable law.
 *
 */

package com.exa.companydemo.location.demo.utils;

public class Constants {
    public static final String THREAD_NAME = "CarLocationServiceAsyncThread";
    public static final int ID_CONNECT = 1;
    public static final int GET_LOCATION_INFO = 2;
    public static final int REFIND = 3;
    public static final int DATA_HAS_DELAY = 4;
    public static final int DATA_UNRELIABLE = 5;
    public static final int OVERTIME = 6;
    public static final int DELAY_TIME = 500;
    public static final int CYCLE_TIME = 1000;
    public static final int RE_CONNECT_TIME = 1000;
    public static final int COUNTDOWN_TIME = 1200;
    public static final int TIMEOUT_LIMIT = 2;
    public static final String INSTANCE_ID = "0x0001";
    public static final int[] DEFAULT_SAT_INFO_INT = {0,0,0};
    public static final float[] DEFAULT_SAT_INFO = {0,0,0};
    public static final int MAX_DUMP_NUM = 60;
    // for test
    public static final int TEST = 0;
}
