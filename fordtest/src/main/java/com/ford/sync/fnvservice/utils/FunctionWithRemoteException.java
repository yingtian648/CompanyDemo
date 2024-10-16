/**
 * @file FunctionWithRemoteException.java
 * @author xiarupeng
 * @email xia.rupeng@zlingsmart.com
 * @copyright Copyright (c) 2024 zlingsmart
 */
package com.ford.sync.fnvservice.utils;

import android.os.RemoteException;

/**
 * A functional interface that represents a function which takes a service object of type `S` as
 * input and returns an object of type `T`, while potentially throwing a `RemoteException`.
 *
 * @param <T> the type of the return value
 * @param <S> the type of the input service object
 */
public interface FunctionWithRemoteException<T, S> {
  T accept(S service) throws RemoteException;
}
