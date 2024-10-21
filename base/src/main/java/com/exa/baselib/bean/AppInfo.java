package com.exa.baselib.bean;

import android.graphics.drawable.Drawable;

public class AppInfo{
    public String name;
    public String packageName;
    public String versionName;
    public String apkSourceDir;
    public String activityName;
    public int versionCode;
    public int icon;
    public Drawable iconDrawable;

    public AppInfo(String name, String packageName,String activityName,int versionCode, String versionName, String apkSourceDir) {
        this.name = name;
        this.packageName = packageName;
        this.activityName = activityName;
        this.versionName = versionName;
        this.apkSourceDir = apkSourceDir;
        this.versionCode = versionCode;
    }

    public AppInfo(String name, String packageName,String activityName, int versionCode, String versionName, String apkSourceDir, int icon) {
        this.name = name;
        this.packageName = packageName;
        this.activityName = activityName;
        this.versionName = versionName;
        this.apkSourceDir = apkSourceDir;
        this.versionCode = versionCode;
        this.icon = icon;
    }

    public AppInfo(String name, String packageName,String activityName, int versionCode, String versionName, String apkSourceDir, Drawable iconDrawable) {
        this.name = name;
        this.packageName = packageName;
        this.activityName = activityName;
        this.versionName = versionName;
        this.apkSourceDir = apkSourceDir;
        this.versionCode = versionCode;
        this.iconDrawable = iconDrawable;
    }

    public AppInfo() {
    }

    @Override
    public String toString() {
        return "" + name +
                "\t\r package='" + packageName + '\'' +
                ", activity='" + activityName + '\'' +
                ", apkDir='" + apkSourceDir + '\'' +
                ", vName='" + versionName + '\'' +
                ", vCode='" + versionCode + '\'' +
                ", icon=" + icon;
    }
}
