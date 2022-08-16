/*
 * This file is auto-generated.  DO NOT MODIFY.
 */
package gxa.car.extlocationservice;
// Declare any non-default types here with import statements

public interface IExtiLocationInterface extends android.os.IInterface
{
  /** Default implementation for IExtiLocationInterface. */
  public static class Default implements IExtiLocationInterface
  {
    @Override public int getLocationMode() throws android.os.RemoteException
    {
      return 0;
    }
    //获取定位模式

    @Override public int getFirstLocationTime() throws android.os.RemoteException
    {
      return 0;
    }
    //获取第一次定位时间(单位:秒)

    @Override public boolean setLocationMode(int mode) throws android.os.RemoteException
    {
      return false;
    }
    //设置定位模式

    @Override public GnssHwInfo getGnssHwInfo() throws android.os.RemoteException
    {
      return null;
    }
    @Override
    public android.os.IBinder asBinder() {
      return null;
    }
  }
  /** Local-side IPC implementation stub class. */
  public static abstract class Stub extends android.os.Binder implements IExtiLocationInterface
  {
    private static final String DESCRIPTOR = "gxa.car.extlocationservice.IExtiLocationInterface";
    /** Construct the stub at attach it to the interface. */
    public Stub()
    {
      this.attachInterface(this, DESCRIPTOR);
    }
    /**
     * Cast an IBinder object into an gxa.car.extlocationservice.IExtiLocationInterface interface,
     * generating a proxy if needed.
     */
    public static IExtiLocationInterface asInterface(android.os.IBinder obj)
    {
      if ((obj==null)) {
        return null;
      }
      android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
      if (((iin!=null)&&(iin instanceof IExtiLocationInterface))) {
        return ((IExtiLocationInterface)iin);
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
        case TRANSACTION_getLocationMode:
        {
          data.enforceInterface(descriptor);
          int _result = this.getLocationMode();
          reply.writeNoException();
          reply.writeInt(_result);
          return true;
        }
        case TRANSACTION_getFirstLocationTime:
        {
          data.enforceInterface(descriptor);
          int _result = this.getFirstLocationTime();
          reply.writeNoException();
          reply.writeInt(_result);
          return true;
        }
        case TRANSACTION_setLocationMode:
        {
          data.enforceInterface(descriptor);
          int _arg0;
          _arg0 = data.readInt();
          boolean _result = this.setLocationMode(_arg0);
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          return true;
        }
        case TRANSACTION_getGnssHwInfo:
        {
          data.enforceInterface(descriptor);
          GnssHwInfo _result = this.getGnssHwInfo();
          reply.writeNoException();
          if ((_result!=null)) {
            reply.writeInt(1);
            _result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
          }
          else {
            reply.writeInt(0);
          }
          return true;
        }
        default:
        {
          return super.onTransact(code, data, reply, flags);
        }
      }
    }
    private static class Proxy implements IExtiLocationInterface
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
      @Override public int getLocationMode() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        int _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getLocationMode, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getLocationMode();
          }
          _reply.readException();
          _result = _reply.readInt();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      //获取定位模式

      @Override public int getFirstLocationTime() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        int _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getFirstLocationTime, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getFirstLocationTime();
          }
          _reply.readException();
          _result = _reply.readInt();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      //获取第一次定位时间(单位:秒)

      @Override public boolean setLocationMode(int mode) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(mode);
          boolean _status = mRemote.transact(Stub.TRANSACTION_setLocationMode, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().setLocationMode(mode);
          }
          _reply.readException();
          _result = (0!=_reply.readInt());
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      //设置定位模式

      @Override public GnssHwInfo getGnssHwInfo() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        GnssHwInfo _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getGnssHwInfo, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getGnssHwInfo();
          }
          _reply.readException();
          if ((0!=_reply.readInt())) {
            _result = GnssHwInfo.CREATOR.createFromParcel(_reply);
          }
          else {
            _result = null;
          }
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      public static IExtiLocationInterface sDefaultImpl;
    }
    static final int TRANSACTION_getLocationMode = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
    static final int TRANSACTION_getFirstLocationTime = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
    static final int TRANSACTION_setLocationMode = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
    static final int TRANSACTION_getGnssHwInfo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
    public static boolean setDefaultImpl(IExtiLocationInterface impl) {
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
    public static IExtiLocationInterface getDefaultImpl() {
      return Proxy.sDefaultImpl;
    }
  }
  public int getLocationMode() throws android.os.RemoteException;
  //获取定位模式

  public int getFirstLocationTime() throws android.os.RemoteException;
  //获取第一次定位时间(单位:秒)

  public boolean setLocationMode(int mode) throws android.os.RemoteException;
  //设置定位模式

  public GnssHwInfo getGnssHwInfo() throws android.os.RemoteException;
}
