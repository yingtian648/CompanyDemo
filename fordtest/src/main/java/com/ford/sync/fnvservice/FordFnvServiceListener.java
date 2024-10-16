/**
 * @file FordFnvServiceListener.java
 * @author zhengjiabo
 * @email zheng.jiabo@zlingsmart.com
 * @copyright Copyright (c) 2024 zlingsmart
 */
package com.ford.sync.fnvservice;

import android.content.Context;

/**
 * Provides listener APIs to listen FordFnvService connect state
 * <p>
 * APP should register this listener to get ForFnvService connect state
 * {@link FordFnv#createFordFnv(Context, FordFnvServiceListener)}
 **/
public interface FordFnvServiceListener {

  void onServiceConnect(final FordFnv fordFnv);

  void onServiceDisconnect();
}
