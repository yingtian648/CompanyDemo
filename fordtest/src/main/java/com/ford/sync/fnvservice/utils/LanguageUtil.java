/**
 * @file LanguageUtil
 * @author zhengjiabo
 * @email zheng.jiabo@zlingsmart.com
 * @copyright Copyright (c) 2024 zlingsmart
 */
package com.ford.sync.fnvservice.utils;
import android.util.Log;
import java.util.Locale;

import com.ford.sync.fnvservice.FordFnvApp;
import com.ford.sync.fnvservice.utils.LogUtils;
/**
 * Provides LanguageUtil
 **/
public class LanguageUtil {

  private static final String TAG = LanguageUtil.class.getSimpleName();

  public static String getLocalLanguage() {
    String language = FordFnvApp.getApplication().getResources()
        .getConfiguration().locale.getLanguage();
    String country = FordFnvApp.getApplication().getResources()
        .getConfiguration().locale.getCountry();
    LogUtils.i("getLocalLanguage:" + country);
    return language + "-" + country.toLowerCase();
  }

  public static boolean isChinese(){
    Locale locale = FordFnvApp.getApplication().getResources().getConfiguration().locale;
    Log.i(TAG, "getLocale curLocale = " + locale);
    return locale.equals(Locale.SIMPLIFIED_CHINESE);

  }

}
