package com.gxatek.cockpit.vicelauncher;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.gxatek.cockpit.vicelauncher.IRemoteStatusCallback;

public interface ViceLauncherRemoteInterface extends IInterface {
    int getNavigationBarVisibility() throws RemoteException;

    int getStatusBarVisibility() throws RemoteException;

    void registerCallback(IRemoteStatusCallback iRemoteStatusCallback) throws RemoteException;

    void setNavigationBarVisibility(int i) throws RemoteException;

    void setStatusBarVisibility(int i) throws RemoteException;

    void unregisterCallback(IRemoteStatusCallback iRemoteStatusCallback) throws RemoteException;

    public static class Default implements ViceLauncherRemoteInterface {
        @Override // com.gxatek.cockpit.vicelauncher.ViceLauncherRemoteInterface
        public void setStatusBarVisibility(int visibility) throws RemoteException {
        }

        @Override // com.gxatek.cockpit.vicelauncher.ViceLauncherRemoteInterface
        public void setNavigationBarVisibility(int visibility) throws RemoteException {
        }

        @Override // com.gxatek.cockpit.vicelauncher.ViceLauncherRemoteInterface
        public int getStatusBarVisibility() throws RemoteException {
            return 0;
        }

        @Override // com.gxatek.cockpit.vicelauncher.ViceLauncherRemoteInterface
        public int getNavigationBarVisibility() throws RemoteException {
            return 0;
        }

        @Override // com.gxatek.cockpit.vicelauncher.ViceLauncherRemoteInterface
        public void registerCallback(IRemoteStatusCallback var1) throws RemoteException {
        }

        @Override // com.gxatek.cockpit.vicelauncher.ViceLauncherRemoteInterface
        public void unregisterCallback(IRemoteStatusCallback var1) throws RemoteException {
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    public static abstract class Stub extends Binder implements ViceLauncherRemoteInterface {
        private static final String DESCRIPTOR = "com.gxatek.cockpit.vicelauncher.ViceLauncherRemoteInterface";
        static final int TRANSACTION_getNavigationBarVisibility = 4;
        static final int TRANSACTION_getStatusBarVisibility = 3;
        static final int TRANSACTION_registerCallback = 5;
        static final int TRANSACTION_setNavigationBarVisibility = 2;
        static final int TRANSACTION_setStatusBarVisibility = 1;
        static final int TRANSACTION_unregisterCallback = 6;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ViceLauncherRemoteInterface asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof ViceLauncherRemoteInterface)) {
                return new Proxy(obj);
            }
            return (ViceLauncherRemoteInterface) iin;
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
                    setStatusBarVisibility(data.readInt());
                    reply.writeNoException();
                    return true;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    setNavigationBarVisibility(data.readInt());
                    reply.writeNoException();
                    return true;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    int _result = getStatusBarVisibility();
                    reply.writeNoException();
                    reply.writeInt(_result);
                    return true;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    int _result2 = getNavigationBarVisibility();
                    reply.writeNoException();
                    reply.writeInt(_result2);
                    return true;
                case 5:
                    data.enforceInterface(DESCRIPTOR);
                    registerCallback(IRemoteStatusCallback.Stub.asInterface(data.readStrongBinder()));
                    reply.writeNoException();
                    return true;
                case 6:
                    data.enforceInterface(DESCRIPTOR);
                    unregisterCallback(IRemoteStatusCallback.Stub.asInterface(data.readStrongBinder()));
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
        public static class Proxy implements ViceLauncherRemoteInterface {
            public static ViceLauncherRemoteInterface sDefaultImpl;
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

            @Override // com.gxatek.cockpit.vicelauncher.ViceLauncherRemoteInterface
            public void setStatusBarVisibility(int visibility) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(visibility);
                    if (this.mRemote.transact(1, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().setStatusBarVisibility(visibility);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.gxatek.cockpit.vicelauncher.ViceLauncherRemoteInterface
            public void setNavigationBarVisibility(int visibility) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(visibility);
                    if (this.mRemote.transact(2, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().setNavigationBarVisibility(visibility);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.gxatek.cockpit.vicelauncher.ViceLauncherRemoteInterface
            public int getStatusBarVisibility() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(3, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getStatusBarVisibility();
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.gxatek.cockpit.vicelauncher.ViceLauncherRemoteInterface
            public int getNavigationBarVisibility() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(4, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getNavigationBarVisibility();
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.gxatek.cockpit.vicelauncher.ViceLauncherRemoteInterface
            public void registerCallback(IRemoteStatusCallback var1) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(var1 != null ? var1.asBinder() : null);
                    if (this.mRemote.transact(5, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().registerCallback(var1);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.gxatek.cockpit.vicelauncher.ViceLauncherRemoteInterface
            public void unregisterCallback(IRemoteStatusCallback var1) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(var1 != null ? var1.asBinder() : null);
                    if (this.mRemote.transact(6, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().unregisterCallback(var1);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        public static boolean setDefaultImpl(ViceLauncherRemoteInterface impl) {
            if (Proxy.sDefaultImpl != null) {
                throw new IllegalStateException("setDefaultImpl() called twice");
            } else if (impl == null) {
                return false;
            } else {
                Proxy.sDefaultImpl = impl;
                return true;
            }
        }

        public static ViceLauncherRemoteInterface getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}