package com.exa.companyclient.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.LauncherApps;
import android.content.pm.PackageManager;
import android.os.UserHandle;

public class PackageManagerHelper {
    private static final String TAG = "PackageManagerHelper";
    private final Context mContext;
    private final LauncherApps mLauncherApps;
    private final PackageManager mPm;

    public PackageManagerHelper(Context context) {
        this.mContext = context;
        this.mPm = context.getPackageManager();
        this.mLauncherApps = context.getSystemService(LauncherApps.class);
    }


    public ApplicationInfo getApplicationInfo(String packageName, UserHandle userHandle, int flags) {
        try {
            ApplicationInfo applicationInfo = this.mLauncherApps.getApplicationInfo(packageName, flags, userHandle);
            if ((applicationInfo.flags & 8388608) == 0 || !applicationInfo.enabled) {
                return null;
            }
            return applicationInfo;
        } catch (PackageManager.NameNotFoundException unused) {
            return null;
        }
    }
}