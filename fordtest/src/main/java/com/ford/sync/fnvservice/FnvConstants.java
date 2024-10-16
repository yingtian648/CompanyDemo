/**
 * @file FnvConstants.java
 * @author zhengjiabo
 * @email zheng.jiabo@zlingsmart.com
 * @date 2024/8/2
 * @copyright Copyright (c) 2024 zlingsmart
 */
package com.ford.sync.fnvservice;

/**
 * FnvConstants Provides to get xxManager
 **/
public interface FnvConstants {
  String AUTOSAVE_SERVICE = "autosave";
  String BEZEL_SERVICE = "bezel";
  String CCS_SERVICE = "ccs";
  String COMMON_SERVICE = "comss";
  String DIAGNOSTICS_SERVICE = "diagnostics";
  String GNSS_SERVICE = "gnss";
  String IPPT_SERVICE = "ippt";
  String V2V_SERVICE = "v2v";
  String WFHS_SERVICE = "whss";

  interface Swu {
    String OTA = "SWU_";
    String SWU_ACTION = "com.ford.sync.fnvservice.swu.action";
    String SWU_PACKAGE = "com.ford.sync.fnvservice";
  }
}