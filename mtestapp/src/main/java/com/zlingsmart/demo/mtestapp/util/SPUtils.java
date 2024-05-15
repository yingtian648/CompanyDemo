package com.zlingsmart.demo.mtestapp.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.zlingsmart.demo.mtestapp.App;

/**
 * @author lsh
 */
public class SPUtils {
    private static final String SP_LOCAL_NAME = "e_browser_sp";
    private static SharedPreferences sp = null;

    public static void saveStringData(String key, String data) {
        if (sp == null){
            sp = App.getContext().getSharedPreferences(SP_LOCAL_NAME, Context.MODE_PRIVATE);
        }
        sp.edit().putString(key, data).apply();
    }

    public static String getStringData(String key) {
        return getStringData(key,null);
    }

    public static void saveBooleanData(String key, Boolean data) {
        if (sp == null){
            sp = App.getContext().getSharedPreferences(SP_LOCAL_NAME, Context.MODE_PRIVATE);
        }
        sp.edit().putBoolean(key, data).apply();
    }

    public static boolean getBooleanData(String key) {
        if (sp == null){
            sp = App.getContext().getSharedPreferences(SP_LOCAL_NAME, Context.MODE_PRIVATE);
        }
        return sp.getBoolean(key, false);
    }

    public static void saveIntData(String key, int data) {
        if (sp == null){
            sp = App.getContext().getSharedPreferences(SP_LOCAL_NAME, Context.MODE_PRIVATE);
        }
        sp.edit().putInt(key, data).apply();
    }

    public static int getIntData(String key) {
        return getIntData(key, 0);
    }

    public static int getIntData(String key, int defaultValue) {
        if (sp == null){
            sp = App.getContext().getSharedPreferences(SP_LOCAL_NAME, Context.MODE_PRIVATE);
        }
        return sp.getInt(key, defaultValue);
    }

    public static boolean getBooleanDataAndDefaultTrue(String key) {
        if (sp == null){
            sp = App.getContext().getSharedPreferences(SP_LOCAL_NAME, Context.MODE_PRIVATE);
        }
        return sp.getBoolean(key, true);
    }

    /**
     * 回传传默认值
     */
    public static String getStringData(String key, String defaultStr) {
        if (sp == null){
            sp = App.getContext().getSharedPreferences(SP_LOCAL_NAME, Context.MODE_PRIVATE);
        }
        return sp.getString(key, defaultStr);
    }
}
