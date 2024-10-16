/**
 * @file ConsumerWithRemoteException.java
 * @author xiarupeng
 * @email xia.rupeng@zlingsmart.com
 * @copyright Copyright (c) 2024 zlingsmart
 */
package com.ford.sync.fnvservice.utils;

import android.os.RemoteException;

/**
 * Interface for functions that might throw a RemoteException.
 *
 * @param <S> the type of the result of the function
 */
public interface ConsumerWithRemoteException<S> {
  void accept(S service) throws RemoteException;
}