package com.gxatek.cockpit.screensaver.aidl;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface IScreenSaverPowerCallbcakAidl extends IInterface {
    void ExitPowerHold(int i) throws RemoteException;

    public static class Default implements IScreenSaverPowerCallbcakAidl {
        @Override // com.gxatek.cockpit.screensaver.aidl.IScreenSaverPowerCallbcakAidl
        public void ExitPowerHold(int states) throws RemoteException {
        }

        public IBinder asBinder() {
            return null;
        }
    }

    public static abstract class Stub extends Binder implements IScreenSaverPowerCallbcakAidl {
        private static final String DESCRIPTOR = "com.gxatek.cockpit.screensaver.aidl.IScreenSaverPowerCallbcakAidl";
        static final int TRANSACTION_ExitPowerHold = 1;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IScreenSaverPowerCallbcakAidl asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof IScreenSaverPowerCallbcakAidl)) {
                return new Proxy(obj);
            }
            return (IScreenSaverPowerCallbcakAidl) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            if (code == 1) {
                data.enforceInterface(DESCRIPTOR);
                ExitPowerHold(data.readInt());
                reply.writeNoException();
                return true;
            } else if (code != 1598968902) {
                return super.onTransact(code, data, reply, flags);
            } else {
                reply.writeString(DESCRIPTOR);
                return true;
            }
        }

        /* access modifiers changed from: private */
        public static class Proxy implements IScreenSaverPowerCallbcakAidl {
            public static IScreenSaverPowerCallbcakAidl sDefaultImpl;
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

            @Override // com.gxatek.cockpit.screensaver.aidl.IScreenSaverPowerCallbcakAidl
            public void ExitPowerHold(int states) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(states);
                    if (this.mRemote.transact(1, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().ExitPowerHold(states);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        public static boolean setDefaultImpl(IScreenSaverPowerCallbcakAidl impl) {
            if (Proxy.sDefaultImpl != null || impl == null) {
                return false;
            }
            Proxy.sDefaultImpl = impl;
            return true;
        }

        public static IScreenSaverPowerCallbcakAidl getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}