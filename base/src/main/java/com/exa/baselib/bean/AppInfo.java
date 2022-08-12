package com.exa.baselib.bean;

public class AppInfo {
    public String name;
    public String packageName;
    public String versionName;
    public String apkSourceDir;
    public int versionCode;

    public AppInfo(String name, String packageName, int versionCode, String versionName, String apkSourceDir) {
        this.name = name;
        this.packageName = packageName;
        this.versionName = versionName;
        this.apkSourceDir = apkSourceDir;
        this.versionCode = versionCode;
    }

    public AppInfo() {
    }

    @Override
    public String toString() {
        return "AppInfo{" +
                "name='" + name + '\'' +
                ", packageName='" + packageName + '\'' +
                ", versionName='" + versionName + '\'' +
                ", apkSourceDir='" + apkSourceDir + '\'' +
                ", versionCode=" + versionCode +
                '}';
    }
}
