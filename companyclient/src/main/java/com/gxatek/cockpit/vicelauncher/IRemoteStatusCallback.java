package com.gxatek.cockpit.vicelauncher;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface IRemoteStatusCallback extends IInterface {
    void onNavigationStatusChange(int i) throws RemoteException;

    void onScreenSaverStatusChange(int i) throws RemoteException;

    void onStatusBarStatusChange(int i) throws RemoteException;

    public static class Default implements IRemoteStatusCallback {
        @Override // com.gxatek.cockpit.vicelauncher.IRemoteStatusCallback
        public void onStatusBarStatusChange(int status) throws RemoteException {
        }

        @Override // com.gxatek.cockpit.vicelauncher.IRemoteStatusCallback
        public void onNavigationStatusChange(int status) throws RemoteException {
        }

        @Override // com.gxatek.cockpit.vicelauncher.IRemoteStatusCallback
        public void onScreenSaverStatusChange(int status) throws RemoteException {
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    public static abstract class Stub extends Binder implements IRemoteStatusCallback {
        private static final String DESCRIPTOR = "com.gxatek.cockpit.vicelauncher.IRemoteStatusCallback";
        static final int TRANSACTION_onNavigationStatusChange = 2;
        static final int TRANSACTION_onScreenSaverStatusChange = 3;
        static final int TRANSACTION_onStatusBarStatusChange = 1;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IRemoteStatusCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof IRemoteStatusCallback)) {
                return new Proxy(obj);
            }
            return (IRemoteStatusCallback) iin;
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    onStatusBarStatusChange(data.readInt());
                    reply.writeNoException();
                    return true;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    onNavigationStatusChange(data.readInt());
                    reply.writeNoException();
                    return true;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    onScreenSaverStatusChange(data.readInt());
                    reply.writeNoException();
                    return true;
                case IBinder.INTERFACE_TRANSACTION:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        /* access modifiers changed from: private */
        public static class Proxy implements IRemoteStatusCallback {
            public static IRemoteStatusCallback sDefaultImpl;
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            @Override // com.gxatek.cockpit.vicelauncher.IRemoteStatusCallback
            public void onStatusBarStatusChange(int status) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(status);
                    if (this.mRemote.transact(1, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().onStatusBarStatusChange(status);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.gxatek.cockpit.vicelauncher.IRemoteStatusCallback
            public void onNavigationStatusChange(int status) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(status);
                    if (this.mRemote.transact(2, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().onNavigationStatusChange(status);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.gxatek.cockpit.vicelauncher.IRemoteStatusCallback
            public void onScreenSaverStatusChange(int status) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(status);
                    if (this.mRemote.transact(3, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().onScreenSaverStatusChange(status);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        public static boolean setDefaultImpl(IRemoteStatusCallback impl) {
            if (Proxy.sDefaultImpl != null) {
                throw new IllegalStateException("setDefaultImpl() called twice");
            } else if (impl == null) {
                return false;
            } else {
                Proxy.sDefaultImpl = impl;
                return true;
            }
        }

        public static IRemoteStatusCallback getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}