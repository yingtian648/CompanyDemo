package com.gxatek.cockpit.screensaver.aidl;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.gxatek.cockpit.screensaver.aidl.IScreenSaverPowerCallbcakAidl;
import com.gxatek.cockpit.screensaver.aidl.IScreenViewChangeCallbackAidl;

public interface IScreenSaverViewAidl extends IInterface {
    void destroyScreenViews() throws RemoteException;

    boolean getScreenIsShow() throws RemoteException;

    void registerScreenSaverPowerCallback(IScreenSaverPowerCallbcakAidl iScreenSaverPowerCallbcakAidl) throws RemoteException;

    void registerScreenViewChangeCallback(IScreenViewChangeCallbackAidl iScreenViewChangeCallbackAidl) throws RemoteException;

    void setCurrentBattery(int i) throws RemoteException;

    void startPowerBatteryScreenViews(int i) throws RemoteException;

    void startScreenViews() throws RemoteException;

    void unregisterFocusCallback(IScreenViewChangeCallbackAidl iScreenViewChangeCallbackAidl) throws RemoteException;

    void unregisterScreenSaverPowerCallback(IScreenSaverPowerCallbcakAidl iScreenSaverPowerCallbcakAidl) throws RemoteException;

    public static class Default implements IScreenSaverViewAidl {
        @Override // com.gxatek.cockpit.screensaver.aidl.IScreenSaverViewAidl
        public boolean getScreenIsShow() throws RemoteException {
            return false;
        }

        @Override // com.gxatek.cockpit.screensaver.aidl.IScreenSaverViewAidl
        public void startScreenViews() throws RemoteException {
        }

        @Override // com.gxatek.cockpit.screensaver.aidl.IScreenSaverViewAidl
        public void destroyScreenViews() throws RemoteException {
        }

        @Override // com.gxatek.cockpit.screensaver.aidl.IScreenSaverViewAidl
        public void registerScreenViewChangeCallback(IScreenViewChangeCallbackAidl callback) throws RemoteException {
        }

        @Override // com.gxatek.cockpit.screensaver.aidl.IScreenSaverViewAidl
        public void unregisterFocusCallback(IScreenViewChangeCallbackAidl callback) throws RemoteException {
        }

        @Override // com.gxatek.cockpit.screensaver.aidl.IScreenSaverViewAidl
        public void setCurrentBattery(int values) throws RemoteException {
        }

        @Override // com.gxatek.cockpit.screensaver.aidl.IScreenSaverViewAidl
        public void startPowerBatteryScreenViews(int states) throws RemoteException {
        }

        @Override // com.gxatek.cockpit.screensaver.aidl.IScreenSaverViewAidl
        public void registerScreenSaverPowerCallback(IScreenSaverPowerCallbcakAidl callback) throws RemoteException {
        }

        @Override // com.gxatek.cockpit.screensaver.aidl.IScreenSaverViewAidl
        public void unregisterScreenSaverPowerCallback(IScreenSaverPowerCallbcakAidl callback) throws RemoteException {
        }

        public IBinder asBinder() {
            return null;
        }
    }

    public static abstract class Stub extends Binder implements IScreenSaverViewAidl {
        private static final String DESCRIPTOR = "com.gxatek.cockpit.screensaver.aidl.IScreenSaverViewAidl";
        static final int TRANSACTION_destroyScreenViews = 3;
        static final int TRANSACTION_getScreenIsShow = 1;
        static final int TRANSACTION_registerScreenSaverPowerCallback = 8;
        static final int TRANSACTION_registerScreenViewChangeCallback = 4;
        static final int TRANSACTION_setCurrentBattery = 6;
        static final int TRANSACTION_startPowerBatteryScreenViews = 7;
        static final int TRANSACTION_startScreenViews = 2;
        static final int TRANSACTION_unregisterFocusCallback = 5;
        static final int TRANSACTION_unregisterScreenSaverPowerCallback = 9;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IScreenSaverViewAidl asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof IScreenSaverViewAidl)) {
                return new Proxy(obj);
            }
            return (IScreenSaverViewAidl) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            if (code != 1598968902) {
                switch (code) {
                    case 1:
                        data.enforceInterface(DESCRIPTOR);
                        boolean screenIsShow = getScreenIsShow();
                        reply.writeNoException();
                        reply.writeInt(screenIsShow ? 1 : 0);
                        return true;
                    case 2:
                        data.enforceInterface(DESCRIPTOR);
                        startScreenViews();
                        reply.writeNoException();
                        return true;
                    case 3:
                        data.enforceInterface(DESCRIPTOR);
                        destroyScreenViews();
                        reply.writeNoException();
                        return true;
                    case 4:
                        data.enforceInterface(DESCRIPTOR);
                        registerScreenViewChangeCallback(IScreenViewChangeCallbackAidl.Stub.asInterface(data.readStrongBinder()));
                        reply.writeNoException();
                        return true;
                    case 5:
                        data.enforceInterface(DESCRIPTOR);
                        unregisterFocusCallback(IScreenViewChangeCallbackAidl.Stub.asInterface(data.readStrongBinder()));
                        reply.writeNoException();
                        return true;
                    case 6:
                        data.enforceInterface(DESCRIPTOR);
                        setCurrentBattery(data.readInt());
                        reply.writeNoException();
                        return true;
                    case 7:
                        data.enforceInterface(DESCRIPTOR);
                        startPowerBatteryScreenViews(data.readInt());
                        reply.writeNoException();
                        return true;
                    case 8:
                        data.enforceInterface(DESCRIPTOR);
                        registerScreenSaverPowerCallback(IScreenSaverPowerCallbcakAidl.Stub.asInterface(data.readStrongBinder()));
                        reply.writeNoException();
                        return true;
                    case 9:
                        data.enforceInterface(DESCRIPTOR);
                        unregisterScreenSaverPowerCallback(IScreenSaverPowerCallbcakAidl.Stub.asInterface(data.readStrongBinder()));
                        reply.writeNoException();
                        return true;
                    default:
                        return super.onTransact(code, data, reply, flags);
                }
            } else {
                reply.writeString(DESCRIPTOR);
                return true;
            }
        }

        /* access modifiers changed from: private */
        public static class Proxy implements IScreenSaverViewAidl {
            public static IScreenSaverViewAidl sDefaultImpl;
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            @Override // com.gxatek.cockpit.screensaver.aidl.IScreenSaverViewAidl
            public boolean getScreenIsShow() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _result = false;
                    if (!this.mRemote.transact(1, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getScreenIsShow();
                    }
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = true;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.gxatek.cockpit.screensaver.aidl.IScreenSaverViewAidl
            public void startScreenViews() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(2, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().startScreenViews();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.gxatek.cockpit.screensaver.aidl.IScreenSaverViewAidl
            public void destroyScreenViews() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(3, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().destroyScreenViews();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.gxatek.cockpit.screensaver.aidl.IScreenSaverViewAidl
            public void registerScreenViewChangeCallback(IScreenViewChangeCallbackAidl callback) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
                    if (this.mRemote.transact(4, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().registerScreenViewChangeCallback(callback);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.gxatek.cockpit.screensaver.aidl.IScreenSaverViewAidl
            public void unregisterFocusCallback(IScreenViewChangeCallbackAidl callback) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
                    if (this.mRemote.transact(5, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().unregisterFocusCallback(callback);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.gxatek.cockpit.screensaver.aidl.IScreenSaverViewAidl
            public void setCurrentBattery(int values) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(values);
                    if (this.mRemote.transact(6, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().setCurrentBattery(values);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.gxatek.cockpit.screensaver.aidl.IScreenSaverViewAidl
            public void startPowerBatteryScreenViews(int states) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(states);
                    if (this.mRemote.transact(7, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().startPowerBatteryScreenViews(states);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.gxatek.cockpit.screensaver.aidl.IScreenSaverViewAidl
            public void registerScreenSaverPowerCallback(IScreenSaverPowerCallbcakAidl callback) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
                    if (this.mRemote.transact(8, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().registerScreenSaverPowerCallback(callback);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.gxatek.cockpit.screensaver.aidl.IScreenSaverViewAidl
            public void unregisterScreenSaverPowerCallback(IScreenSaverPowerCallbcakAidl callback) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
                    if (this.mRemote.transact(9, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().unregisterScreenSaverPowerCallback(callback);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        public static boolean setDefaultImpl(IScreenSaverViewAidl impl) {
            if (Proxy.sDefaultImpl != null || impl == null) {
                return false;
            }
            Proxy.sDefaultImpl = impl;
            return true;
        }

        public static IScreenSaverViewAidl getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}