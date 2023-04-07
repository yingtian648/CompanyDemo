package com.exa.companydemo.service;

import android.content.Context;
import android.os.RemoteException;
import android.util.Slog;
//import com.android.server.IoThread;
//import com.android.server.SystemService;

import com.android.internal.graphics.fonts.IFontManager;

/**
 * @Author lsh
 * @Date 2023/3/28 16:47
 * @Description
 */
public class FontManagerService extends IFontManager.Stub {
    private Context mContext;

    public FontManagerService(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void updateDefaultFontFamily(String familyName) throws RemoteException {

    }

    @Override
    public String getDefaultFontFamilyName() throws RemoteException {
        return null;
    }

//    public static final class Lifecycle extends SystemService {
//        private final FontManagerService mService;
//
//        public Lifecycle(Context context) {
//            super(context);
//            this.mService = new FontManagerService(context);
//        }
//
//        @Override
//        public void onBootPhase(int phase) {
//            Slog.e(TAG, "onBootPhase");
//        }
//
//        @Override
//        public void onStopUser(int userHandle) {
//            Slog.e(TAG, "onStopUser");
//        }
//
//        @Override
//        public void onUnlockUser(int userId) {
//            Slog.e(TAG, "onUnlockUser");
//        }
//
//        @Override
//        public void onStart() {
//            Slog.e(TAG, "onStart");
//        }
//
//        @Override
//        public void onUserSwitching(TargetUser from, TargetUser to) {
//            super.onUserSwitching(from, to);
//            Slog.e(TAG, "onUserSwitching " + from + " to " + to);
//        }
//    }

}
