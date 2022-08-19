/*
 * This file is auto-generated.  DO NOT MODIFY.
 */
package com.android.server.location.cell;
public interface IExtLocationCallback extends android.os.IInterface
{
  /** Default implementation for IExtLocationCallback. */
  public static class Default implements IExtLocationCallback
  {
    //interval：时间间隔（单位：毫秒）

    @Override public void onLocation(long interval, android.location.Location location) throws android.os.RemoteException
    {
    }
    @Override
    public android.os.IBinder asBinder() {
      return null;
    }
  }
  /** Local-side IPC implementation stub class. */
  public static abstract class Stub extends android.os.Binder implements IExtLocationCallback
  {
    private static final String DESCRIPTOR = "com.android.server.location.cell.IExtLocationCallback";
    /** Construct the stub at attach it to the interface. */
    public Stub()
    {
      this.attachInterface(this, DESCRIPTOR);
    }
    /**
     * Cast an IBinder object into an com.android.server.location.cell.IExtLocationCallback interface,
     * generating a proxy if needed.
     */
    public static IExtLocationCallback asInterface(android.os.IBinder obj)
    {
      if ((obj==null)) {
        return null;
      }
      android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
      if (((iin!=null)&&(iin instanceof IExtLocationCallback))) {
        return ((IExtLocationCallback)iin);
      }
      return new Proxy(obj);
    }
    @Override public android.os.IBinder asBinder()
    {
      return this;
    }
    @Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
    {
      String descriptor = DESCRIPTOR;
      switch (code)
      {
        case INTERFACE_TRANSACTION:
        {
          reply.writeString(descriptor);
          return true;
        }
        case TRANSACTION_onLocation:
        {
          data.enforceInterface(descriptor);
          long _arg0;
          _arg0 = data.readLong();
          android.location.Location _arg1;
          if ((0!=data.readInt())) {
            _arg1 = android.location.Location.CREATOR.createFromParcel(data);
          }
          else {
            _arg1 = null;
          }
          this.onLocation(_arg0, _arg1);
          reply.writeNoException();
          return true;
        }
        default:
        {
          return super.onTransact(code, data, reply, flags);
        }
      }
    }
    private static class Proxy implements IExtLocationCallback
    {
      private android.os.IBinder mRemote;
      Proxy(android.os.IBinder remote)
      {
        mRemote = remote;
      }
      @Override public android.os.IBinder asBinder()
      {
        return mRemote;
      }
      public String getInterfaceDescriptor()
      {
        return DESCRIPTOR;
      }
      //interval：时间间隔（单位：毫秒）

      @Override public void onLocation(long interval, android.location.Location location) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeLong(interval);
          if ((location!=null)) {
            _data.writeInt(1);
            location.writeToParcel(_data, 0);
          }
          else {
            _data.writeInt(0);
          }
          boolean _status = mRemote.transact(Stub.TRANSACTION_onLocation, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().onLocation(interval, location);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      public static IExtLocationCallback sDefaultImpl;
    }
    static final int TRANSACTION_onLocation = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
    public static boolean setDefaultImpl(IExtLocationCallback impl) {
      // Only one user of this interface can use this function
      // at a time. This is a heuristic to detect if two different
      // users in the same process use this function.
      if (Proxy.sDefaultImpl != null) {
        throw new IllegalStateException("setDefaultImpl() called twice");
      }
      if (impl != null) {
        Proxy.sDefaultImpl = impl;
        return true;
      }
      return false;
    }
    public static IExtLocationCallback getDefaultImpl() {
      return Proxy.sDefaultImpl;
    }
  }
  //interval：时间间隔（单位：毫秒）

  public void onLocation(long interval, android.location.Location location) throws android.os.RemoteException;
}
