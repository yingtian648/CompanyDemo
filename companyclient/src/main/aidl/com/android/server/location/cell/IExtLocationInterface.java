/*
 * This file is auto-generated.  DO NOT MODIFY.
 */
package com.android.server.location.cell;
// Declare any non-default types here with import statements

public interface IExtLocationInterface extends android.os.IInterface
{
  /** Default implementation for IExtLocationInterface. */
  public static class Default implements IExtLocationInterface
  {
    //设置回调位置信息(interval：时间间隔（单位：毫秒）)

    @Override public void setLocationRequest(long interval, IExtLocationCallback callback) throws android.os.RemoteException
    {
    }
    //获取GNSS硬件信息 返回硬件信息

    @Override public String getGnssHwInfo() throws android.os.RemoteException
    {
      return null;
    }
    @Override
    public android.os.IBinder asBinder() {
      return null;
    }
  }
  /** Local-side IPC implementation stub class. */
  public static abstract class Stub extends android.os.Binder implements IExtLocationInterface
  {
    private static final String DESCRIPTOR = "com.android.server.location.cell.IExtLocationInterface";
    /** Construct the stub at attach it to the interface. */
    public Stub()
    {
      this.attachInterface(this, DESCRIPTOR);
    }
    /**
     * Cast an IBinder object into an com.android.server.location.cell.IExtLocationInterface interface,
     * generating a proxy if needed.
     */
    public static IExtLocationInterface asInterface(android.os.IBinder obj)
    {
      if ((obj==null)) {
        return null;
      }
      android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
      if (((iin!=null)&&(iin instanceof IExtLocationInterface))) {
        return ((IExtLocationInterface)iin);
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
        case TRANSACTION_setLocationRequest:
        {
          data.enforceInterface(descriptor);
          long _arg0;
          _arg0 = data.readLong();
          IExtLocationCallback _arg1;
          _arg1 = IExtLocationCallback.Stub.asInterface(data.readStrongBinder());
          this.setLocationRequest(_arg0, _arg1);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_getGnssHwInfo:
        {
          data.enforceInterface(descriptor);
          String _result = this.getGnssHwInfo();
          reply.writeNoException();
          reply.writeString(_result);
          return true;
        }
        default:
        {
          return super.onTransact(code, data, reply, flags);
        }
      }
    }
    private static class Proxy implements IExtLocationInterface
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
      //设置回调位置信息(interval：时间间隔（单位：毫秒）)

      @Override public void setLocationRequest(long interval, IExtLocationCallback callback) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeLong(interval);
          _data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_setLocationRequest, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().setLocationRequest(interval, callback);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      //获取GNSS硬件信息 返回硬件信息

      @Override public String getGnssHwInfo() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        String _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getGnssHwInfo, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getGnssHwInfo();
          }
          _reply.readException();
          _result = _reply.readString();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      public static IExtLocationInterface sDefaultImpl;
    }
    static final int TRANSACTION_setLocationRequest = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
    static final int TRANSACTION_getGnssHwInfo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
    public static boolean setDefaultImpl(IExtLocationInterface impl) {
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
    public static IExtLocationInterface getDefaultImpl() {
      return Proxy.sDefaultImpl;
    }
  }
  //设置回调位置信息(interval：时间间隔（单位：毫秒）)

  public void setLocationRequest(long interval, IExtLocationCallback callback) throws android.os.RemoteException;
  //获取GNSS硬件信息 返回硬件信息

  public String getGnssHwInfo() throws android.os.RemoteException;
}
